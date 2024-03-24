package com.example.coolchat

import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.coolchat.model.FriendlyMessage
import com.example.coolchat.MainActivity.Companion.ANONYMOUS
import com.example.coolchat.databinding.ImageMessageBinding
import com.example.coolchat.databinding.MessageBinding
import com.example.coolchat.databinding.VideoMessageBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseError
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FriendlyMessageAdapter(
    private val options: FirebaseRecyclerOptions<FriendlyMessage>,
    private val currentUserName: String?,
    private val onDeleteClick: (item: FriendlyMessage) -> Unit = {},
    private val onEditClick: (item: FriendlyMessage) -> Unit = {}
) : FirebaseRecyclerAdapter<FriendlyMessage, ViewHolder>(options) {

    var mediaControls: MediaController? = null

  /*  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_TEXT) {
            val view = inflater.inflate(R.layout.message, parent, false)
            val binding = MessageBinding.bind(view)
            MessageViewHolder(binding)
        } else {
            val view = inflater.inflate(R.layout.image_message, parent, false)
            val binding = ImageMessageBinding.bind(view)
            ImageMessageViewHolder(binding)
        }
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_TEXT -> {
                val view = inflater.inflate(R.layout.message, parent, false)
                val binding = MessageBinding.bind(view)
                MessageViewHolder(binding)
            }
            VIEW_TYPE_IMAGE -> {
                val view = inflater.inflate(R.layout.image_message, parent, false)
                val binding = ImageMessageBinding.bind(view)
                ImageMessageViewHolder(binding)
            }
            VIEW_TYPE_VIDEO -> {
                val view = inflater.inflate(R.layout.video_message, parent, false)
                val binding = VideoMessageBinding.bind(view)
                VideoMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }


   /* override fun onBindViewHolder(holder: ViewHolder, position: Int, model: FriendlyMessage) {
        if (options.snapshots[position].text != null) {
            (holder as MessageViewHolder).bind(model)
        } else {
            (holder as ImageMessageViewHolder).bind(model)
        }
    }*/

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: FriendlyMessage) {
        when (getItemViewType(position)) {
            VIEW_TYPE_TEXT -> {
                (holder as MessageViewHolder).bind(model)
            }
            VIEW_TYPE_VIDEO -> {
                (holder as VideoMessageViewHolder).bind(model)
            }
            VIEW_TYPE_IMAGE -> {
                (holder as ImageMessageViewHolder).bind(model)
            }
            else -> throw IllegalArgumentException("Invalid view type at position $position")
        }
    }


    /*   override fun getItemViewType(position: Int): Int {
           return if (options.snapshots[position].text != null) VIEW_TYPE_TEXT else VIEW_TYPE_IMAGE
       }*/

    override fun getItemViewType(position: Int): Int {
        val message = options.snapshots[position]
        return when {
            message.text != null -> VIEW_TYPE_TEXT
            message.imageUrl != null -> VIEW_TYPE_IMAGE
            message.videoUrl != null -> VIEW_TYPE_VIDEO
            else -> throw IllegalArgumentException("Invalid message type at position $position")
        }
    }


    override fun onDataChanged() {
        Log.d(TAG, "onDataChanged: new data added")
        super.onDataChanged()
    }

    override fun onError(error: DatabaseError) {
        Log.d(TAG, "onError: " + error.message)
    }

    inner class MessageViewHolder(private val binding: MessageBinding) : ViewHolder(binding.root) {
        fun bind(item: FriendlyMessage) {
            binding.messageTextView.text = item.text
            setTextColor(item.name, binding.messageTextView)

            binding.messengerTextView.text = if (item.name == null) ANONYMOUS else item.name + " " + convertTimestampToReadableDate(item.timestamp)
            if (item.photoUrl != null) {
                loadImageIntoView(binding.messengerImageView, item.photoUrl!!)
            } else {
                binding.messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
            }
            binding.messengerDelete.setOnClickListener {
                onDeleteClick(item)
            }
            binding.messengerEdit.setOnClickListener {
                onEditClick(item)
            }
        }

        private fun setTextColor(userName: String?, textView: TextView) {
            if (userName != ANONYMOUS && currentUserName == userName && userName != null) {
                textView.setBackgroundResource(R.drawable.rounded_message_blue)
                textView.setTextColor(Color.WHITE)
            } else {
                textView.setBackgroundResource(R.drawable.rounded_message_gray)
                textView.setTextColor(Color.BLACK)
            }
        }

        private fun convertTimestampToReadableDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = Date(timestamp)
            return sdf.format(date)
        }
    }

    inner class ImageMessageViewHolder(private val binding: ImageMessageBinding) :
        ViewHolder(binding.root) {
        fun bind(item: FriendlyMessage) {
            Log.d(TAG, "Loading VIEW_TYPE_IMAGE")
            loadImageIntoView(binding.messageImageView, item.imageUrl!!, false)
            Log.d(TAG, "Loading video from URL:" + item.imageUrl)
            binding.messengerTextView.text = item.name ?: ANONYMOUS
            if (item.photoUrl != null) {
                loadImageIntoView(binding.messengerImageView, item.photoUrl)
            } else {
                binding.messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
            }
        }
    }

    private fun loadImageIntoView(view: ImageView, url: String, isCircular: Boolean = true) {
        Log.d(TAG, "Loading image from URL: $url")

        if (url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    loadWithGlide(view, downloadUrl, isCircular)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to load image URL: $url", e)
                    Log.w(
                        TAG,
                        "Getting download url was not successful.",
                        e
                    )
                }
        } else {
            loadWithGlide(view, url, isCircular)
        }
    }


    inner class VideoMessageViewHolder(private val binding: VideoMessageBinding) : ViewHolder(binding.root) {
        fun bind(item: FriendlyMessage) {
            Log.d(TAG, "Loading VIEW_TYPE_VIDEO")
            // Load video into VideoView
            if (item.videoUrl != null) {
                loadVideoIntoView(binding.messageVideoView, item.videoUrl)
                Log.d(TAG, "Loading video from URL:" + item.videoUrl)
            }

            // Set default image if photoUrl is not available
            binding.messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)

            // Set messengerTextView text
            binding.messengerTextView.text = item.name ?: ANONYMOUS
        }
    }

    private fun loadVideoIntoView(videoView: VideoView, videoUrl: String) {
        Log.d(TAG, "Loading video from URL: $videoUrl")

        if (mediaControls == null) {
            // creating an object of media controller class
            mediaControls = MediaController(videoView.context)
            // set the anchor view for the video view
            mediaControls!!.setAnchorView(videoView)
        }
        videoView.setMediaController(mediaControls)
        if (videoUrl.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(videoUrl)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    videoView.setVideoURI(Uri.parse(downloadUrl))
                    videoView.setOnPreparedListener { mediaPlayer ->
                        mediaPlayer.isLooping = true
                        videoView.start()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to load video URL: $videoUrl", e)
                    Log.w(
                        TAG,
                        "Getting download url was not successful.",
                        e
                    )
                }
        } else {
            videoView.setVideoURI(Uri.parse(videoUrl))
            videoView.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
                videoView.start()
            }
        }
    }


/*    private fun loadWithGlide(view: ImageView, url: String, isCircular: Boolean = true) {
        Glide.with(view.context).load(url).into(view)
        var requestBuilder = Glide.with(view.context).load(url)
        if (isCircular) {
            requestBuilder = requestBuilder.transform(CircleCrop())
        }
        requestBuilder.into(view)
    }*/

    private fun loadWithGlide(view: View, url: String, isCircular: Boolean = true) {
        if (view is ImageView) {
            Glide.with(view.context).load(url).into(view)
            var requestBuilder = Glide.with(view.context).load(url)
            if (isCircular) {
                requestBuilder = requestBuilder.transform(CircleCrop())
            }
            requestBuilder.into(view)
        } else if (view is VideoView) {
            // Load thumbnail image into the ImageView associated with the VideoView
            // For example:
            // Glide.with(view.context).load(thumbnailUrl).into(imageView)
        } else {
            throw IllegalArgumentException("View must be ImageView or VideoView")
        }
    }


    companion object {
        const val TAG = "MessageAdapter"
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_IMAGE = 2
        const val VIEW_TYPE_VIDEO = 3
    }
}
