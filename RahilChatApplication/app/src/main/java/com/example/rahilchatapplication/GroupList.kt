package com.example.rahilchatapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

        supportActionBar?.show()
        recyclerViewGroups = findViewById(R.id.recyclerViewGroups)
        mDbRef = FirebaseDatabase.getInstance().getReference("groups")
        groupAdapter = GroupAdapter(
            groupList,
            { groupName -> onAddMeClick(groupName) },
            { groupName -> showGroupUsers(groupName) }
        )

        mAuth = FirebaseAuth.getInstance()

        recyclerViewGroups.apply {
            layoutManager = LinearLayoutManager(this@GroupList)
            adapter = groupAdapter
        }

        // Retrieve groups from Firebase
        retrieveGroups()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
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

    private fun showGroupUsers(groupName: String) {
        val intent = Intent(this, GroupUsers::class.java)
        intent.putExtra("groupName", groupName)
        startActivity(intent)
    }

    private fun onAddMeClick(groupName: String) {
        val currentUser = mAuth.currentUser
        currentUser?.let { user ->
            val userEmail = user.email
            if (userEmail != null) {
                val usersRef = FirebaseDatabase.getInstance().getReference("user")
                val userRef = usersRef.child(user.uid)

                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userName = snapshot.child("name").getValue(String::class.java)
                        if (userName != null) {
                            val groupRef = mDbRef.child(groupName).child("members")

                            // Check if the current user ID is already in the group's members
                            groupRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.hasChild(user.uid)) {
                                        showToast("You are already a member of this group.")
                                    } else {
                                        // Add the user ID, name, and email to the group's members
                                        val userData = HashMap<String, Any>()
                                        userData["id"] = user.uid
                                        userData["name"] = userName
                                        userData["email"] = userEmail

                                        groupRef.child(user.uid).setValue(userData)
                                            .addOnSuccessListener {
                                                showToast("Added to $groupName group successfully.")
                                            }
                                            .addOnFailureListener { exception ->
                                                showToast("Failed to add to $groupName group: ${exception.message}")
                                            }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle onCancelled event
                                }
                            })
                        } else {
                            showToast("User name not found.")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle onCancelled event
                    }
                })
            } else {
                showToast("User email not found.")
            }
        }
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logout){
            mAuth.signOut()
            val intent = Intent(this, LogIn::class.java)
            finish()
            startActivity(intent)
            return true;
        }else if(item.itemId == R.id.create_group) {
            val intent = Intent(this, CreateGroup::class.java)
            finish()
            startActivity(intent)
            return true;
        } else if(item.itemId == R.id.list_groups) {
            val intent = Intent(this, GroupList::class.java)
            finish()
            startActivity(intent)
            return true;
        }else if(item.itemId == R.id.chat_list) {
            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
            return true;
        }

        return true;
    }
}
