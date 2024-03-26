package com.example.rahilchatapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class GroupUsers : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var groupName: String
    private val userList: ArrayList<User> = ArrayList()
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_users)

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
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key
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
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }
}
