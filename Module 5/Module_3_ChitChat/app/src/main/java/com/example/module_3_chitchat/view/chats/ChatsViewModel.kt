package com.example.module_3_chitchat.view.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import android.util.Log
import com.example.module_3_chitchat.Constants
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.*

class ChatsViewModel : ViewModel() {
    private val firestore = Firebase.firestore
    private val userId =
        Firebase.auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    private val _chats = MutableLiveData<List<Chat>>()
    val chats: LiveData<List<Chat>> get() = _chats

    init {
        loadUserChats()
    }

    private fun loadUserChats() {
        firestore.collection("chat_rooms").whereArrayContains("members", userId).get()
            .addOnSuccessListener { result ->
                val chatList = result.mapNotNull { document ->
                    Chat(
                        document.id,
                        document.getString("name") ?: "",
                        document.getString("inviteCode") ?: ""
                    )
                }
                _chats.value = chatList
            }.addOnFailureListener { e ->
                Log.e(Constants.TAG, "Error fetching user's chat rooms", e)
            }
    }

    fun createChat(chatName: String) {
        val inviteCode = UUID.randomUUID().toString().substring(0, 6)
        val newChat = hashMapOf(
            "name" to chatName, "inviteCode" to inviteCode, "members" to listOf(userId)
        )

        firestore.collection("chat_rooms").add(newChat).addOnSuccessListener {
            Log.d(Constants.TAG, "Chat created successfully")
            loadUserChats()
        }.addOnFailureListener { e ->
            Log.e(Constants.TAG, "Error creating chat", e)
        }
    }

    fun joinChat(inviteCode: String) {
        firestore.collection("chat_rooms").whereEqualTo("inviteCode", inviteCode).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.w(Constants.TAG, "No chat found with invite code: $inviteCode")
                    return@addOnSuccessListener
                }

                for (document in documents) {
                    val members = document["members"] as? List<String> ?: listOf()
                    if (userId in members) {
                        Log.d(Constants.TAG, "User is already a member of the chat")
                        return@addOnSuccessListener
                    }

                    firestore.collection("chat_rooms").document(document.id)
                        .update("members", members + userId).addOnSuccessListener {
                            Log.d(Constants.TAG, "Joined chat successfully")
                            loadUserChats()
                        }.addOnFailureListener { e ->
                            Log.e(Constants.TAG, "Error joining chat", e)
                        }
                }
            }.addOnFailureListener { e ->
                Log.e(Constants.TAG, "Error searching for chat with invite code", e)
            }
    }

    fun deleteChat(chatId: String) {
        val TAG = Constants.TAG

        val chatRoomDocumentReference = firestore.collection(Constants.CHAT_ROOMS).document(chatId)
        val chatDocumentReference = firestore.collection(Constants.CHATS).document(chatId)

        chatRoomDocumentReference.get().addOnSuccessListener { chatSnapshot ->
            val membersList = chatSnapshot.get("members") as? List<String> ?: emptyList()

            if (membersList.contains(userId)) {
                chatRoomDocumentReference.update("members", membersList - userId).addOnSuccessListener {
                    Log.d(TAG, "User removed from members list successfully")
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to remove user from members list", exception)
                }
            }

            if (membersList.size == 1 && membersList.contains(userId)) {
                val chatMediaFolderReference = Firebase.storage.reference.child("${Constants.CHATS}/$chatId")

                deleteDirectoryRecursively(chatMediaFolderReference) {
                    deleteChatMessages(chatId) {
                        chatDocumentReference.delete().addOnSuccessListener {
                            Log.d(TAG, "Deleted chat document successfully")
                        }.addOnFailureListener { exception ->
                            Log.e(TAG, "Failed to delete chat document", exception)
                        }

                        chatRoomDocumentReference.delete().addOnSuccessListener {
                            Log.d(TAG, "Deleted chat room document successfully")
                        }.addOnFailureListener { exception ->
                            Log.e(TAG, "Failed to delete chat room document", exception)
                        }
                    }
                }
            }

            loadUserChats()
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Failed to fetch chat room document", exception)
        }
    }

    private fun deleteChatMessages(chatId: String, onComplete: () -> Unit) {
        val TAG = Constants.TAG

        val messagesReference = firestore.collection(Constants.CHATS).document(chatId).collection("messages")
        messagesReference.get().addOnSuccessListener { querySnapshot ->
            val deleteTasks = mutableListOf<Task<Void>>()
            querySnapshot.documents.forEach { document ->
                deleteTasks.add(document.reference.delete())
            }
            Tasks.whenAll(deleteTasks).addOnSuccessListener {
                Log.d(TAG, "All messages deleted successfully")
                onComplete() // Invoke onComplete after all messages are deleted
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Failed to delete messages", exception)
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Failed to fetch messages for deletion", exception)
        }
    }


    private fun deleteDirectoryRecursively(reference: StorageReference, onComplete: () -> Unit) {
        val TAG = Constants.TAG

        reference.listAll().addOnSuccessListener { listResult ->
            if (listResult.prefixes.isEmpty() && listResult.items.isEmpty()) {
                Log.d(TAG, "Directory is empty, completing deletion")
                onComplete()
                return@addOnSuccessListener
            }

            val deleteTasks = mutableListOf<Task<Void>>()

            listResult.prefixes.forEach { prefix ->
                Log.d(TAG, "Deleting directory: ${prefix.path}")
                deleteDirectoryRecursively(prefix) {}
            }

            listResult.items.forEach { item ->
                Log.d(TAG, "Deleting file: ${item.path}")
                deleteTasks.add(item.delete().addOnSuccessListener {
                    Log.d(TAG, "File deleted successfully: ${item.path}")
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to delete file: ${item.path}", exception)
                })
            }

            Tasks.whenAll(deleteTasks).addOnSuccessListener {
                Log.d(TAG, "All files and directories deleted successfully")
                onComplete()
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Failed to delete files and directories", exception)
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Failed to list directory contents", exception)
        }
    }



}

data class Chat(
    val id: String = "",
    val name: String = "",
    val inviteCode: String = "",
    val members: List<String> = emptyList()
)

