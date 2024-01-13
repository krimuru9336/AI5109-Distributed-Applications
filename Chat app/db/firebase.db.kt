package com.example.disapp.db

import com.example.disapp.data.Message
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class FireBaseDB {

    private val database: FirebaseDatabase =
        Firebase.database("https://CENSORED.europe-west1.firebasedatabase.app/")

    fun getMessagesRef(): DatabaseReference = this.database.getReference("messages")

    fun pushMessage(ref: DatabaseReference, msg: Message) = ref.push().setValue(msg)

    fun updateMessage(ref:DatabaseReference,key:String,newMsg:Message) = ref.child(key).setValue(newMsg)

    fun deleteMessage(ref:DatabaseReference,key:String) = ref.child(key).removeValue()

}