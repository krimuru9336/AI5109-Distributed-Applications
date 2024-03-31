package com.example.mychat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GroupList : AppCompatActivity(),
    GroupListAdapter.OnUserClickListener {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var groupList: ArrayList<Group>
    private lateinit var adapter: GroupListAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var selectedUsers: MutableList<User> = mutableListOf()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grouplist)

        mAuth = FirebaseAuth.getInstance()

        groupList = ArrayList()
        adapter = GroupListAdapter(this, groupList)
        adapter.setUserClickListener(this)
        mDbRef = FirebaseDatabase.getInstance().getReference().child("group_chats")

        userRecyclerView = findViewById(R.id.userRecyclerView)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter
        mDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(Group::class.java)
                    groupList.add(currentUser!!)

                }
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


    override fun onUserClick(group: Group) {
        val intent = Intent(this, GroupChatActivity::class.java)

        intent.putExtra("groupId",group.uid )
        startActivity(intent)
    }
}
