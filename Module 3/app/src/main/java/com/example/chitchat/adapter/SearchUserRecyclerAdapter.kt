package com.example.chitchat.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.R
import com.example.chitchat.Utils
import com.example.chitchat.models.UserModel
import com.example.chitchat.ChatActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class SearchUserRecyclerAdapter(
    options: FirestoreRecyclerOptions<UserModel?>,
    var context: Context
) :
    FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder>(options) {
    protected override fun onBindViewHolder(
        holder: UserModelViewHolder,
        position: Int,
        model: UserModel
    ) {
        holder.usernameText.setText(model.username)
        holder.itemView.setOnClickListener { v: View? ->
            val intent = Intent(
                context,
                ChatActivity::class.java
            )
            Utils.passUserModelAsIntent(intent, model)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserModelViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.search_user_row, parent, false)
        return UserModelViewHolder(view)
    }

    inner class UserModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var usernameText: TextView

        init {
            usernameText = itemView.findViewById<TextView>(R.id.user_name_text)
        }
    }
}

