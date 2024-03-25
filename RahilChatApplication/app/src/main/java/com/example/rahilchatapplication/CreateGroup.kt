package com.example.rahilchatapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateGroup : AppCompatActivity(){
    private lateinit var etGroupName: EditText
    private lateinit var btnCreateGroup: Button
    private lateinit var btnListGroups: Button
    private lateinit var mDbRef: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_group)

        etGroupName = findViewById(R.id.etGroupName)
        btnCreateGroup = findViewById(R.id.btnCreateGroup)
        btnListGroups = findViewById(R.id.btnListGroups)
        mDbRef = FirebaseDatabase.getInstance().reference

        btnCreateGroup.setOnClickListener {
            val groupName = etGroupName.text.toString().trim()
            if (groupName.isNotEmpty()) {
                addGroupToFirebase(groupName)
            } else {
                showToast("Please enter a group name.")
            }
        }

        btnListGroups.setOnClickListener {
            val intent = Intent(this, GroupList::class.java)
            finish()
            startActivity(intent)
        }
    }

    private fun addGroupToFirebase(groupName: String) {
        val groupsRef = mDbRef.child("groups")
        val groupKey = groupsRef.push().key

        groupKey?.let {
            groupsRef.child(it).child("name").setValue(groupName)
                .addOnSuccessListener {
                    showToast("Group added successfully to Firebase Database.")
                }
                .addOnFailureListener { exception ->
                    showToast("Failed to add group to Firebase Database: $exception")
                }
        } ?: run {
            showToast("Failed to get group key.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
