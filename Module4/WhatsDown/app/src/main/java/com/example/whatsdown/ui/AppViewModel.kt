package com.example.whatsdown.ui

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {
    private var _appState = MutableStateFlow(value = AppState())
    val appState = _appState.asStateFlow()

    fun startChatting(name: String) {
        db.collection("users")
            .whereNotEqualTo("name", name)
            .get()
            .addOnSuccessListener { documents ->
                _appState.update { currentState ->
                    currentState.copy(
                        currentUser = name,
                        usersList = documents.map { document ->
                            document.getString("name") ?: "name not found"
                        })
                }
            }
            .addOnFailureListener { throw it }
    }

    fun joinChat(selectedUser: String) {
        val currentPair = listOf(_appState.value.currentUser, selectedUser)
        db.collection("chats")
            .whereIn("sender", currentPair)
            .whereIn("receiver", currentPair)
            .orderBy("timestamp")
            .addSnapshotListener { value, e ->
                if (e != null) throw e
                _appState.update { currentState ->
                    currentState.copy(
                        currentReceiver = selectedUser,
                        chats = value?.map { document ->
                            Chat(
                                id = document.id,
                                sender = document.getString("sender") ?: "sender not found",
                                receiver = document.getString("receiver") ?: "receiver not found",
                                message = document.getString("message") ?: "message not found",
                                timeStamp = document.getLong("timestamp") ?: 0
                            )
                        } ?: listOf()
                    )
                }
            }
    }

    fun sendMessage(message: String) {
        db.collection("chats")
            .add(
                mapOf(
                    "sender" to _appState.value.currentUser,
                    "receiver" to _appState.value.currentReceiver,
                    "message" to message,
                    "timestamp" to System.currentTimeMillis()
                )
            )
            .addOnFailureListener { throw it }
    }

    fun editMessage(messageId: String, message: String) {
        db.collection("chats")
            .document(messageId)
            .update("message", message)
            .addOnFailureListener { throw it }
    }

    fun deleteMessage(messageId: String) {
        db.collection("chats")
            .document(messageId)
            .delete()
            .addOnFailureListener { throw it }
    }
}