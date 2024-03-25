package com.example.rahilchatapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupAdapter(
    private val groupList: List<String>,
    private val onAddMeClick: (String) -> Unit
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val groupName = groupList[position]
        holder.bind(groupName)
        holder.btnAddMe.setOnClickListener { onAddMeClick(groupName) }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtGroupName: TextView = itemView.findViewById(R.id.txtGroupName)
        val btnAddMe: Button = itemView.findViewById(R.id.btnAddMe)

        fun bind(groupName: String) {
            txtGroupName.text = groupName
        }
    }
}
