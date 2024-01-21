package com.example.chitchat

import android.content.Intent
import com.example.chitchat.models.UserModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat


object Utils {
    fun passUserModelAsIntent(intent: Intent, model: UserModel) {
        intent.putExtra("username", model.username)
        intent.putExtra("userId", model.userId)
    }

    fun getUserModelFromIntent(intent: Intent): UserModel {
        val userModel = UserModel()
        userModel.username =intent.getStringExtra("username")
        userModel.userId = intent.getStringExtra("userId")
        return userModel
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
        return if (userId1.hashCode() < userId2.hashCode()) {
            userId1 + "_" + userId2
        } else {
            userId2 + "_" + userId1
        }
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

    fun timestampToString(timestamp: Timestamp): String {
        return SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(timestamp.toDate())
    }
}

