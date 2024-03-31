package com.example.mychat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserListAdapter(val context:Context, val userList: ArrayList<User>) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    private var userClickListener: OnUserClickListener? = null
    fun setUserClickListener(listener: OnUserClickListener) {
        this.userClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val currentUser = userList[position]
        holder.textName.text = currentUser.name
        holder.itemView.setOnClickListener {

        }

            // Bind data and set click listener for each item
            holder.itemView.setOnClickListener {
                userClickListener?.onUserClick(currentUser) // Invoke interface method
            }


    }
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
    }


    interface OnUserClickListener {
        fun onUserClick(user: User)
    }
}
