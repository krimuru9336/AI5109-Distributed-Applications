package com.example.chitchat

import android.content.Intent
import com.example.chitchat.models.ChatroomModel
import com.example.chitchat.models.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.UUID


object Utils {
    fun passUserModelAsIntent(intent: Intent, model: UserModel) {
        intent.putExtra("username", model.username)
        intent.putExtra("userId", model.userId)
    }

    fun passChatroomModelAsIntent(intent: Intent, model: ChatroomModel) {
        intent.putExtra("chatroomId", model.chatroomId)
        intent.putStringArrayListExtra("userIds", model.userIds as ArrayList<String>?)
    }

    fun getUserModelFromIntent(intent: Intent): UserModel {
        val userModel = UserModel()
        userModel.username = intent.getStringExtra("username")
        userModel.userId = intent.getStringExtra("userId")
        return userModel
    }

    fun getChatroomIdFromIntent(intent: Intent): String? {
        return intent.getStringExtra("chatroomId")
    }

    fun getChatroomUserIds(intent: Intent): ArrayList<String>? {
        return intent.getStringArrayListExtra("userIds")
    }

    fun currentUserId(): String? {
        return FirebaseAuth.getInstance().uid
    }

    fun isLoggedIn(): Boolean {
        return currentUserId() != null
    }

    fun currentUserDetails(): DocumentReference {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId()!!)
    }

    fun allUserCollectionReference(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("users")
    }

    fun getChatroomReference(chatroomId: String?): DocumentReference {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId!!)
    }

    fun getChatroomMessageReference(chatroomId: String?): CollectionReference {
        return getChatroomReference(chatroomId).collection("chats")
    }

    fun getChatroomId(userId1: String, userId2: String): String {
        val myUuid = UUID.randomUUID()
        return myUuid.toString()
    }

    fun allChatroomCollectionReference(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("chatrooms")
    }

    fun getOtherUserFromChatroom(userIds: List<String>): DocumentReference {
        return if (userIds[0] == currentUserId()) {
            allUserCollectionReference().document(userIds[1])
        } else {
            allUserCollectionReference().document(userIds[0])
        }
    }

    fun getOtherUsersFromChatroom(userIds: List<String>): List<DocumentReference> {
        val userDocuments = mutableListOf<DocumentReference>()

        for (userId in userIds) {
            if (userId != currentUserId()) {
                val userRef = allUserCollectionReference().document(userId)
                userDocuments.add(userRef)
            }
        }
        return userDocuments
    }

    fun getUserFromId(userId: String): DocumentReference {
        return allUserCollectionReference().document(userId)
    }

    fun getUserFromName(username: String): Task<QuerySnapshot> {
        return allUserCollectionReference().whereEqualTo("username", username).limit(1).get()
    }

    fun timestampToString(timestamp: Timestamp): String {
        return SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(timestamp.toDate())
    }
}

