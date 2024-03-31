package com.example.mychat

import android.R.id.message
import android.content.Context
import android.media.browse.MediaBrowser
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth


interface DeleteMessageListener {
    fun onDeleteMessage(position: Int)
}
interface EditMessageListener {
    fun onEditMessage(position: Int)
}
class MessageAdapter(
    val context: Context,
    val messageList: ArrayList<Message>,
    deleteListener: DeleteMessageListener,
    editListener: EditMessageListener,
):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val deleteListener = deleteListener
        val editListener = editListener


        val ITEM_RECIEVE = 1;
    val ITEM_SENT =2;


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == 1){
            //inflate recieve
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive,parent,false)
            return ReceiveViewholder(view)
        }else{
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent,parent,false)
            return SentViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
            }else{
                return ITEM_RECIEVE
        }
    }

    override fun getItemCount(): Int {

        return messageList.size

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]


        if(holder.javaClass == SentViewHolder::class.java){
            //do the stuff for sent view holder

            val viewHolder = holder as SentViewHolder
            if (currentMessage.message != null && currentMessage.mediaType != null) {
                // Media message
                if (currentMessage.mediaType?.startsWith("image/") == true) {
                    // Load image into ImageView
                    Log.d("d",currentMessage.message.toString()+"Ref")
                    Glide.with(context).load(currentMessage.message).into(holder.imageView)
                    // Hide TextView for text messages
                    holder.sentMessage.visibility = View.GONE
                    // Show ImageView for media messages
                    holder.imageView.visibility = View.VISIBLE
                    // Show VideoView For Media
                    holder.PlayerView.visibility = View.GONE
                } else if (currentMessage.mediaType?.startsWith("video/") == true) {
                    // Load video thumbnail or display video player (if implementing video playback)
                    val player = ExoPlayer.Builder(context).build()
                    val mediaItem: MediaItem = MediaItem.fromUri(currentMessage.message!!)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play ()
                    holder.PlayerView.setPlayer(player)
                    player.playWhenReady = true
                    // Example: Glide.with(context).load(videoThumbnailUrl).into(holder.imageView)
                    // Hide TextView for text messages
                    holder.sentMessage.visibility = View.GONE
                    // Show ImageView for media messages
                    holder.imageView.visibility = View.GONE
                    // Show VideoView For Media
                    holder.PlayerView.visibility = View.VISIBLE
                }else if(currentMessage.mediaType == "text"){
                    holder.sentMessage.text = currentMessage.message
                    // Hide TextView for text messages
                    holder.sentMessage.visibility = View.VISIBLE
                    // Show ImageView for media messages
                    holder.imageView.visibility = View.GONE
                    // Show VideoView For Media
                    holder.PlayerView.visibility = View.GONE
                }

            } else {
                // Text message
                holder.sentMessage.text = currentMessage.message
                // Hide ImageView for media messages
                holder.imageView.visibility = View.GONE
                // Show TextView for text messages
                holder.sentMessage.visibility = View.VISIBLE
            }

            holder.timestamp.text = currentMessage.timestamp
            holder.editButton.setOnClickListener {
                editListener.onEditMessage(position)
            }

        }else{
            //do the stuff for receive view holder
            val viewHolder = holder as ReceiveViewholder
            if (currentMessage.mediaType?.startsWith("image/") == true) {
                Glide.with(context).load(currentMessage.message).into(holder.imageView)
                // Hide TextView for text messages
                holder.receiveMessage.visibility = View.GONE
                // Show ImageView for media messages
                holder.imageView.visibility = View.VISIBLE
                // Show VideoView For Media
                holder.PlayerView.visibility = View.GONE
            } else if (currentMessage.mediaType?.startsWith("video/") == true) {
                // Load video thumbnail or display video player (if implementing video playback)
                val player = ExoPlayer.Builder(context).build()
                val mediaItem: MediaItem = MediaItem.fromUri(currentMessage.message!!)
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play ()
                holder.PlayerView.setPlayer(player)
                player.playWhenReady = true
                // Example: Glide.with(context).load(videoThumbnailUrl).into(holder.imageView)
                // Hide TextView for text messages
                holder.receiveMessage.visibility = View.GONE
                // Show ImageView for media messages
                holder.imageView.visibility = View.GONE
                // Show VideoView For Media
                holder.PlayerView.visibility = View.VISIBLE
            }else if(currentMessage.mediaType == "text"){
                holder.receiveMessage.text = currentMessage.message
                // Hide TextView for text messages
                holder.receiveMessage.visibility = View.VISIBLE
                // Show ImageView for media messages
                holder.imageView.visibility = View.GONE
                // Show VideoView For Media
                holder.PlayerView.visibility = View.GONE
            }
        }

        holder.itemView.setOnLongClickListener {

            if (position != RecyclerView.NO_POSITION) {
                // Handle long click actions here
                // For example, you can show a contextual menu or perform some other action
                deleteListener.onDeleteMessage(position)
                true // Indicate that the long click has been handled
            } else {
                false // Indicate that the long click has not been handled
            }
        }
    }



    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val timestamp = itemView.findViewById<TextView>(R.id.txt_sent_timestamp)
        val  editButton = itemView.findViewById<Button>(R.id.editButton)
        val  imageView = itemView.findViewById<ImageView>(R.id.Images)
        val  PlayerView = itemView.findViewById<PlayerView>(R.id.Video)


    }


    class ReceiveViewholder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
        val timestamp = itemView.findViewById<TextView>(R.id.txt_receive_timestamp)
        val  imageView = itemView.findViewById<ImageView>(R.id.Images)
        val  PlayerView = itemView.findViewById<PlayerView>(R.id.Video)
    }

}




