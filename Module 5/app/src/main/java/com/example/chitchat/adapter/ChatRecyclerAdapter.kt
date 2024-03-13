package com.example.chitchat.adapter

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chitchat.R
import com.example.chitchat.Utils
import com.example.chitchat.models.ChatmessageModel
import com.example.chitchat.models.MessageType
import com.example.chitchat.models.UserModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot


class ChatRecyclerAdapter(
    options: FirestoreRecyclerOptions<ChatmessageModel>,
    var context: Context,
    var activityContext: Context
) :
    FirestoreRecyclerAdapter<ChatmessageModel, ChatRecyclerAdapter.ChatModelViewHolder>(options) {
    protected override fun onBindViewHolder(
        holder: ChatModelViewHolder,
        position: Int,
        model: ChatmessageModel
    ) {
        if (model.senderId.equals(Utils.currentUserId())) {
            holder.leftChatLayout.visibility = View.GONE
            holder.rightChatLayout.visibility = View.VISIBLE
            holder.rightChatTimestamp.setText(Utils.timestampToString(model.timestamp!!))

            if (model.messageType == MessageType.IMAGE) {
                downloadImageAndSetToView(model.message!!, holder.rightChatImageview);
                holder.rightChatImageview.visibility = View.VISIBLE;
                holder.rightChatTextview.visibility = View.GONE;
            } else if (model.messageType == MessageType.VIDEO) {
                downloadVideoAndSetToView(model.message!!, holder.rightChatVideoview);
                holder.rightChatVideoview.setVisibility(View.VISIBLE);
                holder.rightChatTextview.visibility = View.GONE;
            } else {
                holder.rightChatTextview.setText(model.message)
            }
        } else {
            holder.rightChatLayout.visibility = View.GONE
            holder.leftChatLayout.visibility = View.VISIBLE
            holder.leftChatTextview.setText(model.message)
            holder.leftChatTimestamp.setText(Utils.timestampToString(model.timestamp!!))

            if (model.messageType == MessageType.IMAGE) {
                downloadImageAndSetToView(model.message!!, holder.leftChatImageview);
                holder.leftChatImageview.visibility = View.VISIBLE;
                holder.leftChatTextview.visibility = View.GONE;
            } else if (model.messageType == MessageType.VIDEO) {
                downloadVideoAndSetToView(model.message!!, holder.leftChatVideoview);
                holder.leftChatVideoview.setVisibility(View.VISIBLE);
                holder.leftChatTextview.visibility = View.GONE;
            } else {
                holder.rightChatTextview.setText(model.message)
            }

            model.senderId?.let {
                Utils.getUserFromId(it)
                    .get().addOnCompleteListener { task ->
                        if (task.isSuccessful()) {
                            val userModel: UserModel =
                                task.getResult().toObject(UserModel::class.java)!!
                            holder.leftChatUser.setText(userModel.username)
                        }
                    }
            }
        }

        val documentSnapshot: DocumentSnapshot = snapshots.getSnapshot(position)
        holder.rightChatLayout.setOnLongClickListener() {
            showEditDeleteDialog(
                holder.rightChatTextview,
                activityContext,
                documentSnapshot,
                model.messageType
            )
            true
        }
    }

    private fun downloadImageAndSetToView(url: String, view: ImageView) {
        Glide.with(context)
            .load(url)
            .into(view)
    }

    private fun downloadVideoAndSetToView(url: String, view: VideoView) {
        val videoUri = Uri.parse(url)
        view.setVideoURI(videoUri)

        val mediaController = MediaController(context)
        mediaController.setAnchorView(view)
        view.setMediaController(mediaController)
        view.start()
    }

    private fun showEditDeleteDialog(
        textView: TextView,
        context: Context,
        documentSnapshot: DocumentSnapshot,
        messageType: MessageType
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Message")
        if (messageType == MessageType.TEXT) {
            builder.setTitle("Edit/Delete Message")
            val editText = EditText(context)
            editText.setText(textView.text)
            builder.setView(editText)

            builder.setPositiveButton("Edit") { _, _ ->
                val editedMessage = editText.text.toString()
                textView.text = editedMessage
                val documentReference = documentSnapshot.reference
                documentReference.update("message", editedMessage)
            }
        }

        builder.setNegativeButton("Delete") { _, _ ->
            val documentReference = documentSnapshot.reference
            documentReference.delete()
        }

        builder.create().show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatModelViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.chatmessage_row, parent, false)
        return ChatModelViewHolder(view)
    }

    inner class ChatModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var leftChatLayout: LinearLayout
        var rightChatLayout: LinearLayout
        var leftChatTextview: TextView
        var leftChatImageview: ImageView
        var leftChatVideoview: VideoView
        var rightChatTextview: TextView
        var rightChatImageview: ImageView
        var rightChatVideoview: VideoView
        var leftChatTimestamp: TextView
        var rightChatTimestamp: TextView
        var leftChatUser: TextView

        init {
            leftChatLayout = itemView.findViewById<LinearLayout>(R.id.left_chat_layout)
            leftChatTextview = itemView.findViewById<TextView>(R.id.left_chat_textview)
            leftChatImageview = itemView.findViewById<ImageView>(R.id.left_chat_imageview)
            leftChatVideoview = itemView.findViewById<VideoView>(R.id.left_chat_videoview)
            leftChatTimestamp = itemView.findViewById<TextView>(R.id.left_chat_timestamp)
            rightChatLayout = itemView.findViewById<LinearLayout>(R.id.right_chat_layout)
            rightChatTextview = itemView.findViewById<TextView>(R.id.right_chat_textview)
            rightChatImageview = itemView.findViewById<ImageView>(R.id.right_chat_imageview)
            rightChatVideoview = itemView.findViewById<VideoView>(R.id.right_chat_videoview)
            rightChatTimestamp = itemView.findViewById<TextView>(R.id.right_chat_timestamp)
            leftChatUser = itemView.findViewById<TextView>(R.id.left_chat_username)
        }
    }
}

