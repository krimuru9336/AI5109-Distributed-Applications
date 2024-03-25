package com.example.rahilchatapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GroupList : AppCompatActivity() {
    private lateinit var recyclerViewGroups: RecyclerView
    private lateinit var mDbRef: DatabaseReference
    private lateinit var groupAdapter: GroupAdapter
    private val groupList: MutableList<String> = mutableListOf()
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_list)

        recyclerViewGroups = findViewById(R.id.recyclerViewGroups)
        mDbRef = FirebaseDatabase.getInstance().getReference("groups")
        groupAdapter = GroupAdapter(groupList) { groupName ->
            onAddMeClick(groupName)
        }
        mAuth = FirebaseAuth.getInstance()

        recyclerViewGroups.apply {
            layoutManager = LinearLayoutManager(this@GroupList)
            adapter = groupAdapter
        }

        // Retrieve groups from Firebase
        retrieveGroups()
    }

    private fun retrieveGroups() {
        mDbRef.addValueEventListener(object : ValueEventListener {
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

    private fun onAddMeClick(groupName: String) {
        val currentUser = mAuth.currentUser
        currentUser?.let { user ->
            val groupRef = mDbRef.child(groupName).child("members")
            groupRef.child(user.uid).setValue(true)
                .addOnSuccessListener {
                    // User added to group successfully
                    showToast("Added to $groupName group successfully.")
                }
                .addOnFailureListener { exception ->
                    // Failed to add user to group
                    showToast("Failed to add to $groupName group: ${exception.message}")
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
