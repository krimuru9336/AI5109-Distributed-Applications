package com.example.disapp.db

import com.example.disapp.data.Message
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

class FireBaseDB {

    private val database: FirebaseDatabase =
        Firebase.database("CENSORED")

    private val storage: FirebaseStorage =
        Firebase.storage("CENSORED")

    fun getMessagesRef(): DatabaseReference = this.database.getReference("messages")

    fun getStorageImageRef(): StorageReference = this.storage.getReference("images")

    fun getStorageVideoRef():StorageReference = this.storage.getReference("videos")

    fun pushMessage(ref: DatabaseReference, msg: Message) = ref.push().setValue(msg)

    fun updateMessage(ref: DatabaseReference, key: String, newMsg: Message) =
        ref.child(key).setValue(newMsg)

    fun deleteMessage(ref: DatabaseReference, key: String) = ref.child(key).removeValue()

}