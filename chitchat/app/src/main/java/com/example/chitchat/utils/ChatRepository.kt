package com.example.chitchat.utils

import com.example.chitchat.data.ChatMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatRepository(private val roomId: String) {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val chatReference: DatabaseReference = database.reference.child("chats").child(roomId)

    fun sendMessage(sender: String, text: String) {
        val messageId = chatReference.push().key
        val timestamp = System.currentTimeMillis()

        if (messageId != null) {
            val message = ChatMessage(
                sender,
                text,
                timestamp=System.currentTimeMillis()
            )
            chatReference.child(messageId).setValue(message)
        }
    }

    fun getMessages(callback: (List<ChatMessage>) -> Unit) {
        chatReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<ChatMessage>()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(ChatMessage::class.java)
                    message?.let { messages.add(it) }
                }
                callback(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}