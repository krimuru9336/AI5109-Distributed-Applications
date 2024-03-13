package com.example.chitchat.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.R
import com.example.chitchat.ChatActivity
import com.example.chitchat.Utils
import com.example.chitchat.models.ChatroomModel
import com.example.chitchat.models.UserModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class RecentChatRecyclerAdapter(
    options: FirestoreRecyclerOptions<ChatroomModel>,
    var context: Context
) :
    FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder>(
        options
    ) {
    protected override fun onBindViewHolder(
        holder: ChatroomModelViewHolder,
        position: Int,
        model: ChatroomModel
    ) {
        Utils.getOtherUserFromChatroom(model.userIds!!)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    val otherUserModel: UserModel = task.getResult().toObject(UserModel::class.java)!!
                    holder.usernameText.setText(otherUserModel.username)
                    holder.itemView.setOnClickListener { v: View? ->
                        val intent = Intent(
                            context,
                            ChatActivity::class.java
                        )
                        Utils.passUserModelAsIntent(intent, otherUserModel)
                        Utils.passChatroomModelAsIntent(intent, model)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                }
            }

        if (model.userIds!!.count() > 2) {
            val stringBuilder = StringBuilder()
            Utils.getOtherUsersFromChatroom(model.userIds!!)
                .forEach { userRef ->
                    userRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val otherUserModel: UserModel? =
                                task.result?.toObject(UserModel::class.java)
                            if (otherUserModel != null) {
                                if (stringBuilder.isNotEmpty()) {
                                    stringBuilder.append(", ");
                                }
                                stringBuilder.append(otherUserModel.username)
                                holder.usernameText.text = stringBuilder.toString();
                            }
                        }
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomModelViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.recent_chat_row, parent, false)
        return ChatroomModelViewHolder(view)
    }

    inner class ChatroomModelViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var usernameText: TextView

        init {
            usernameText = itemView.findViewById<TextView>(R.id.user_name_text)
        }
    }
}

