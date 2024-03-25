package com.example.rahilchatapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GroupList : AppCompatActivity() {
    private lateinit var recyclerViewGroups: RecyclerView
    private lateinit var mDbRef: DatabaseReference
    private lateinit var groupAdapter: GroupAdapter
    private val groupList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_list)

        recyclerViewGroups = findViewById(R.id.recyclerViewGroups)
        mDbRef = FirebaseDatabase.getInstance().getReference("groups")
        groupAdapter = GroupAdapter(groupList)

        recyclerViewGroups.apply {
            layoutManager = LinearLayoutManager(this@GroupList)
            adapter = groupAdapter
        }

        // Retrieve groups from Firebase
        retrieveGroups()
    }

    private fun retrieveGroups() {
        mDbRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                groupList.clear()
                for (groupSnapshot in snapshot.children) {
                    val groupName = groupSnapshot.child("name").getValue(String::class.java)
                    groupName?.let {
                        groupList.add(it)
                    }
                }
                groupAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }
}
