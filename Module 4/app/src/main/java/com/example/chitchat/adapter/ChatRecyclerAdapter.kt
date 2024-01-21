package com.example.chitchat.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.R
import com.example.chitchat.Utils
import com.example.chitchat.models.ChatmessageModel
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
            holder.rightChatTextview.setText(model.message)
            holder.rightChatTimestamp.setText(Utils.timestampToString(model.timestamp!!))
        } else {
            holder.rightChatLayout.visibility = View.GONE
            holder.leftChatLayout.visibility = View.VISIBLE
            holder.leftChatTextview.setText(model.message)
            holder.leftChatTimestamp.setText(Utils.timestampToString(model.timestamp!!))
        }

        val documentSnapshot: DocumentSnapshot = snapshots.getSnapshot(position)
        holder.rightChatLayout.setOnLongClickListener() {
            showEditDeleteDialog(holder.rightChatTextview, activityContext, documentSnapshot)
            true
        }
    }

    private fun showEditDeleteDialog(
        textView: TextView,
        context: Context,
        documentSnapshot: DocumentSnapshot
    ) {
        val editText = EditText(context)
        editText.setText(textView.text)

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Edit/Delete Message")
        builder.setView(editText)

        builder.setPositiveButton("Edit") { _, _ ->
            val editedMessage = editText.text.toString()
            textView.text = editedMessage
            val documentReference = documentSnapshot.reference
            documentReference.update("message", editedMessage)
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
        var rightChatTextview: TextView
        var leftChatTimestamp: TextView
        var rightChatTimestamp: TextView

        init {
            leftChatLayout = itemView.findViewById<LinearLayout>(R.id.left_chat_layout)
            leftChatTextview = itemView.findViewById<TextView>(R.id.left_chat_textview)
            leftChatTimestamp = itemView.findViewById<TextView>(R.id.left_chat_timestamp)
            rightChatLayout = itemView.findViewById<LinearLayout>(R.id.right_chat_layout)
            rightChatTextview = itemView.findViewById<TextView>(R.id.right_chat_textview)
            rightChatTimestamp = itemView.findViewById<TextView>(R.id.right_chat_timestamp)
        }
    }
}

