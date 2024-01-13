package com.example.module_3_chitchat.view.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.module_3_chitchat.Constants
import java.lang.IllegalArgumentException

class HomeViewModel : ViewModel() {
    init {
        getMessages()
    }

    private val _message = MutableLiveData("")
    val message: LiveData<String> = _message

    private var _messages = MutableLiveData(emptyList<Map<String, Any>>().toMutableList())
    val messages: LiveData<MutableList<Map<String, Any>>> = _messages

    fun updateMessage(message: String) {
        _message.value = message
    }

    fun addMessage() {
        val message: String =
            _message.value?.trim() ?: throw IllegalArgumentException("message empty")
        if (message.isNotEmpty()) {
            val newMessageRef = Firebase.firestore.collection(Constants.MESSAGES).document()
            newMessageRef.set(
                hashMapOf(
                    Constants.MESSAGE_ID to newMessageRef.id,
                    Constants.MESSAGE to message,
                    Constants.SENT_BY to Firebase.auth.currentUser?.uid,
                    Constants.SENT_ON to System.currentTimeMillis()
                )
            ).addOnSuccessListener {
                _message.value = ""
            }
        }
    }

    fun deleteMessage(messageId: String) {
        Firebase.firestore.collection(Constants.MESSAGES).document(messageId).delete()
            .addOnSuccessListener {
                Log.d(Constants.TAG, "Message deleted successfully. ID: $messageId")
            }.addOnFailureListener { e ->
                Log.e(Constants.TAG, "Failed to delete message with ID: $messageId", e)
            }
    }

    fun updateMessage(messageId: String, updatedMessage: String) {
        val trimmedMessage = updatedMessage.trim()
        val currentUserUid = Firebase.auth.currentUser?.uid

        if (trimmedMessage.isNotEmpty() && currentUserUid == _messages.value?.find { it[Constants.MESSAGE_ID] == messageId }
                ?.get(Constants.SENT_BY)) {
            Firebase.firestore.collection(Constants.MESSAGES).document(messageId)
                .update(Constants.MESSAGE, trimmedMessage).addOnSuccessListener {
                    Log.d(Constants.TAG, "Message updated successfully. ID: $messageId")
                }.addOnFailureListener { e ->
                    Log.e(Constants.TAG, "Failed to update message with ID: $messageId", e)
                }
        } else if (trimmedMessage.isEmpty()) {
            deleteMessage(messageId)
        }
    }

    private fun getMessages() {
        Firebase.firestore.collection(Constants.MESSAGES).orderBy(Constants.SENT_ON)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val list = emptyList<Map<String, Any>>().toMutableList()

                if (value != null) {
                    for (doc in value) {
                        val data = doc.data
                        data[Constants.IS_CURRENT_USER] =
                            Firebase.auth.currentUser?.uid.toString() == data[Constants.SENT_BY].toString()

                        list.add(data)
                    }
                }

                updateMessages(list)
            }
    }

    private fun updateMessages(list: MutableList<Map<String, Any>>) {
        _messages.value = list.asReversed()
    }
}
