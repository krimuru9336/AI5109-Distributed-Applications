package com.example.mychat

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserList : AppCompatActivity(), UserListAdapter.OnUserClickListener {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserListAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef : DatabaseReference
    private var selectedUsers: MutableList<User> = mutableListOf()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        userList = ArrayList()
        adapter = UserListAdapter(this,userList)
        adapter.setUserClickListener(this)

        userRecyclerView = findViewById(R.id.userRecyclerView)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter
        mDbRef.child("user").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if (mAuth.currentUser?.uid != currentUser?.uid)

                        userList.add(currentUser!!)

                }
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun onCreateGroupChatButtonClick(view: View) {



        val groupChatRef = mDbRef.child("group_chats").push()
        val groupId = groupChatRef.key

        Log.d("d",groupId.toString()+" groupid");

        if (groupId != null) {
            // Add selected users to the group chat
            val editText:EditText = findViewById(R.id.messagebox)
            for (user in selectedUsers) {
                groupChatRef.child("members").child(user.uid.toString()).setValue(true)
            }
            groupChatRef.child("members").child(mAuth.uid.toString()).setValue(true)

            groupChatRef.child("group_name").setValue(editText.text.toString())
            groupChatRef.child("uid").setValue(groupId)

            // Start GroupChatActivity with the group chat ID
            val intent = Intent(this, GroupChatActivity::class.java)

            intent.putExtra("groupId", groupId)

            startActivity(intent)
        } else {
            Toast.makeText(this, "Failed to create group chat", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onUserClick(user: User) {
        // Handle the selected user here
        // For example, update the text box with the selected user's name
        val textBox: TextView = findViewById(R.id.UserList)
        textBox.setText(user.name)
        selectedUsers.add(user)
        textBox.setText(buildText(selectedUsers))
    }

    private fun buildText(users: List<User>): String {
        val names = users.map { it.name }
        return names.joinToString(", ")
    }
}