package com.shafi.zaptalk.ui

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
            .whereNotEqualTo("name", name).get()
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
        db.collection("chats")
            .whereIn("sender", listOf(_appState.value.currentUser, selectedUser))
            .whereIn("receiver", listOf(_appState.value.currentUser, selectedUser))
            .orderBy("timeStamp")
            .addSnapshotListener { value, e ->
                if (e != null) throw e
                _appState.update { currentState ->
                    currentState.copy(
                        currentReceiver = selectedUser,
                        chats = value?.map { document ->
                            Chat(
                                messageId = document.id, // Get the document ID as messageId
                                sender = document.getString("sender") ?: "sender not found",
                                receiver = document.getString("receiver") ?: "receiver not found",
                                message = document.getString("message") ?: "message not found",
                                timeStamp = document.getLong("timeStamp") ?: 0
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
                    "timeStamp" to System.currentTimeMillis()
                )
            )
            .addOnSuccessListener { documentReference ->
                val messageId = documentReference.id // Get the auto-generated document ID
                val newChat = Chat(
                    messageId = messageId,
                    sender = _appState.value.currentUser,
                    receiver = _appState.value.currentReceiver,
                    message = message,
                    timeStamp = System.currentTimeMillis()
                )
                // Optionally, update local state with the new message
            }
            .addOnFailureListener { throw it }
    }

    fun editMessage(newMessage: String, messageId: String) {
        db.collection("chats")
            .document(messageId)
            .update("message", newMessage)
            .addOnSuccessListener {
                // Optionally, update local state with the edited message
            }
            .addOnFailureListener { throw it }
    }

    fun deleteMessage(messageId: String) {
        db.collection("chats")
            .document(messageId)
            .delete()
            .addOnSuccessListener {
                // Optionally, update local state by removing the deleted message
            }
            .addOnFailureListener { throw it }
    }
}