package com.example.rahilchatapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth

class GroupUsers : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var groupName: String
    private val userList: ArrayList<User> = ArrayList()
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_users)
        mAuth = FirebaseAuth.getInstance()
        // Get the group name passed from the previous activity
        groupName = intent.getStringExtra("groupName") ?: ""

        recyclerView = findViewById(R.id.recyclerViewGroupUsers)
        userAdapter = UserAdapter(this, userList)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@GroupUsers)
            adapter = userAdapter
        }

        mDbRef = FirebaseDatabase.getInstance().getReference("groups").child(groupName).child("members")
        retrieveUsers()
    }

    private fun retrieveUsers() {
        mDbRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val currentUserID = mAuth.currentUser?.uid // Get the ID of the current user
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key
                    if (userId != currentUserID) { // Exclude the current user from the list
                        val userName = userSnapshot.child("name").getValue(String::class.java)
                        val userEmail = userSnapshot.child("email").getValue(String::class.java)

                        userId?.let { id ->
                            userName?.let { name ->
                                userEmail?.let { email ->
                                    userList.add(User(name, email, id))
                                }
                            }
                        }
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }
}
