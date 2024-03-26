package com.example.module_3_chitchat.view.home

import android.util.Log
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.module_3_chitchat.Constants
import com.google.firebase.storage.ktx.storage
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.StorageReference

class HomeViewModel() : ViewModel() {
    private val firestore = Firebase.firestore
    private val storage = Firebase.storage
    private lateinit var currentChatId: String

    fun init(chatId: String, chatName: String) {
        currentChatId = chatId
        if (chatId.isNotEmpty()) {
            checkOrCreateChat(chatId, chatName)
            getMessages()
        } else {
            Log.e(Constants.TAG, "Chat ID is empty!")
        }

    }

    private fun checkOrCreateChat(chatId: String, chatName: String) {
        val chatRef = firestore.collection(Constants.CHATS).document(chatId)
        chatRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val newChatData = mapOf(
                    "name" to chatName,
                )
                chatRef.set(newChatData).addOnSuccessListener {
                    Log.d(Constants.TAG, "New chat created: $chatId")
                }.addOnFailureListener { e ->
                    Log.e(Constants.TAG, "Failed to create new chat: $chatId", e)
                }
            }
        }.addOnFailureListener { e ->
            Log.e(Constants.TAG, "Failed to check if chat exists: $chatId", e)
        }
    }


    private val _message = MutableLiveData("")
    val message: LiveData<String> = _message

    private var _messages = MutableLiveData(emptyList<Map<String, Any>>().toMutableList())
    val messages: LiveData<MutableList<Map<String, Any>>> = _messages

    fun updateMessage(message: String) {
        _message.value = message
    }

    fun addMedia(uri: Uri) {
        val chatId = currentChatId
        val messageId =
            firestore.collection(Constants.CHATS).document(chatId).collection(Constants.MESSAGES)
                .document().id
        val revision = 0
        val fileName = "chats/$chatId/media/$messageId/${messageId}_rev_$revision"

        uploadFile(uri, fileName, messageId)
    }

    private fun uploadFile(uri: Uri, fileName: String, messageId: String) {
        val storageRef = storage.reference.child(fileName)

        storageRef.putFile(uri).addOnSuccessListener {
            checkAndHandleMimeType(storageRef, fileName, messageId)
        }.addOnFailureListener { e ->
            Log.e(Constants.TAG, "Failed to upload media: $fileName", e)
        }
    }

    private fun checkAndHandleMimeType(
        storageRef: StorageReference, fileName: String, messageId: String
    ) {
        storageRef.metadata.addOnSuccessListener { metadata ->
            val mimeType = metadata.contentType

            if (mimeType != null && mimeType in listOf(
                    "image/gif", "video/mp4", "image/jpeg", "image/png"
                )
            ) {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val mediaUrl = downloadUri.toString()
                    val messageType = when (mimeType) {
                        "image/gif" -> Constants.MessageType.GIF
                        "video/mp4" -> Constants.MessageType.VIDEO
                        else -> Constants.MessageType.IMAGE
                    }
                    addMessage(messageType, mediaUrl, messageId)
                    Log.d(
                        Constants.TAG, "File uploaded and message added with MIME type: $mimeType"
                    )
                }
            } else if (mimeType == null) {
                Log.e(Constants.TAG, "MIME type is null for file: $fileName")
            } else {
                storageRef.delete().addOnSuccessListener {
                    Log.d(
                        Constants.TAG,
                        "Deleted unsupported file type: $mimeType for file: $fileName"
                    )
                }.addOnFailureListener { e ->
                    Log.e(Constants.TAG, "Failed to delete unsupported file type: $fileName", e)
                }
            }
        }.addOnFailureListener { e ->
            Log.e(Constants.TAG, "Failed to get metadata for file: $fileName", e)
        }
    }


    fun addMessage(
        messageType: Constants.MessageType, messageText: String? = null, messageId: String? = null
    ) {
        val chatId = currentChatId

        val finalMessageText = when (messageType) {
            Constants.MessageType.TEXT -> {
                val trimmedText = _message.value?.trim()
                if (trimmedText.isNullOrEmpty()) {
                    Log.w(Constants.TAG, "Attempting to send empty text message.")
                    return
                }
                trimmedText
            }

            Constants.MessageType.IMAGE, Constants.MessageType.GIF, Constants.MessageType.VIDEO -> {
                if (messageText.isNullOrBlank()) {
                    Log.w(Constants.TAG, "Attempting to send media message without URL.")
                    return
                }
                messageText
            }
        }

        val messageData = hashMapOf(
            Constants.MESSAGE_ID to (messageId ?: firestore.collection(Constants.MESSAGES)
                .document().id),
            Constants.MESSAGE to finalMessageText,
            Constants.SENT_BY to Firebase.auth.currentUser?.uid,
            Constants.SENT_ON to System.currentTimeMillis(),
            Constants.MEDIA_TYPE to messageType.name
        )

        firestore.collection(Constants.CHATS).document(chatId).collection(Constants.MESSAGES)
            .document(messageData[Constants.MESSAGE_ID].toString()).set(messageData)
            .addOnSuccessListener {
                if (messageType == Constants.MessageType.TEXT) {
                    _message.value = ""
                }
                Log.d(
                    Constants.TAG,
                    "Message saved successfully: ${messageData[Constants.MESSAGE_ID]}"
                )
            }.addOnFailureListener { e ->
                Log.e(
                    Constants.TAG, "Failed to save message: ${messageData[Constants.MESSAGE_ID]}", e
                )
            }
    }


    fun deleteMessage(messageId: String) {
        val chatId = currentChatId
        val messageRef =
            firestore.collection(Constants.CHATS).document(chatId).collection(Constants.MESSAGES)
                .document(messageId)

        val message = _messages.value?.find { it[Constants.MESSAGE_ID] == messageId }
        val messageType = message?.get(Constants.MEDIA_TYPE) as? String

        if (messageType != Constants.MessageType.TEXT.name) {
            val mediaUrl = message?.get(Constants.MESSAGE) as? String

            mediaUrl?.let {
                val mediaRef = storage.getReferenceFromUrl(it)
                mediaRef.delete().addOnSuccessListener {
                    Log.d(Constants.TAG, "Media deleted successfully. URL: $mediaUrl")
                    deleteMessageFromFirestore(messageRef)
                }.addOnFailureListener { e ->
                    Log.e(Constants.TAG, "Failed to delete media. URL: $mediaUrl", e)
                }
            }
        } else {
            deleteMessageFromFirestore(messageRef)
        }
    }

    private fun deleteMessageFromFirestore(messageRef: DocumentReference) {
        messageRef.delete().addOnSuccessListener {
            Log.d(Constants.TAG, "Message deleted successfully.")
        }.addOnFailureListener { e ->
            Log.e(Constants.TAG, "Failed to delete message.", e)
        }
    }

    fun updateMessage(messageId: String, updatedContent: String?) {
        val chatId = currentChatId
        val messageRef =
            firestore.collection(Constants.CHATS).document(chatId).collection(Constants.MESSAGES)
                .document(messageId)

        messageRef.get().addOnSuccessListener { documentSnapshot ->
            val currentRevision = documentSnapshot.getLong(Constants.REVISION) ?: 0
            val newRevision = currentRevision + 1
            val messageType =
                documentSnapshot.getString(Constants.MEDIA_TYPE) ?: Constants.MessageType.TEXT.name

            if (messageType == Constants.MessageType.TEXT.name) {
                val newText = updatedContent ?: _message.value?.trim() ?: ""
                if (newText.isNotEmpty()) {
                    messageRef.update(
                        mapOf(
                            Constants.MESSAGE to newText, Constants.REVISION to newRevision
                        )
                    ).addOnSuccessListener {
                        Log.d(
                            Constants.TAG,
                            "Text message updated successfully. ID: $messageId, revision: $newRevision"
                        )
                    }.addOnFailureListener { e ->
                        Log.e(
                            Constants.TAG,
                            "Failed to update text message with ID: $messageId, revision: $newRevision",
                            e
                        )
                    }
                }
            } else {
                Log.d(Constants.TAG, "Use updateMediaMessage for media message updates.")
            }
        }.addOnFailureListener { e ->
            Log.e(Constants.TAG, "Failed to fetch message for update with ID: $messageId", e)
        }
    }

    fun updateMedia(messageId: String, newMediaUri: Uri) {
        val chatId = currentChatId
        val messageRef =
            firestore.collection(Constants.CHATS).document(chatId).collection(Constants.MESSAGES)
                .document(messageId)

        messageRef.get().addOnSuccessListener { documentSnapshot ->
            val currentMediaUrl = documentSnapshot.getString(Constants.MESSAGE)
            val currentRevision = extractRevision(currentMediaUrl) ?: 0
            val newRevision = currentRevision + 1

            currentMediaUrl?.let { oldMediaUrl ->
                val oldMediaRef = storage.getReferenceFromUrl(oldMediaUrl)
                oldMediaRef.delete().addOnSuccessListener {
                    Log.d(Constants.TAG, "Old media deleted successfully: $oldMediaUrl")
                    val newFileName = "chats/$chatId/media/$messageId/${messageId}_rev_$newRevision"
                    uploadNewMedia(newMediaUri, newFileName, messageId)
                }.addOnFailureListener { e ->
                    Log.e(Constants.TAG, "Failed to delete old media: $oldMediaUrl", e)
                }
            }
        }.addOnFailureListener { e ->
            Log.e(Constants.TAG, "Failed to get message for update: $messageId", e)
        }
    }

    private fun extractRevision(mediaUrl: String?): Int? {
        return mediaUrl?.substringAfterLast("_rev_")?.filter { it.isDigit() }?.toIntOrNull()
    }

    private fun uploadNewMedia(newMediaUri: Uri, fileName: String, messageId: String) {
        val newMediaRef = storage.reference.child(fileName)

        newMediaRef.putFile(newMediaUri).addOnSuccessListener {
            newMediaRef.metadata.addOnSuccessListener { metadata ->
                val mimeType = metadata.contentType
                val newMediaType = when (mimeType) {
                    "image/gif" -> Constants.MessageType.GIF
                    "image/jpeg", "image/png" -> Constants.MessageType.IMAGE
                    "video/mp4" -> Constants.MessageType.VIDEO
                    else -> null
                }

                if (newMediaType != null) {
                    newMediaRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val newMediaUrl = downloadUrl.toString()
                        updateMediaMessageInFirestore(messageId, newMediaUrl, newMediaType.name)
                        Log.d(
                            Constants.TAG,
                            "New media uploaded and message updated: $fileName with type: $newMediaType"
                        )
                    }.addOnFailureListener { e ->
                        Log.e(
                            Constants.TAG, "Failed to get download URL for new media: $fileName", e
                        )
                    }
                } else {
                    Log.e(Constants.TAG, "Unsupported media type: $mimeType for file: $fileName")
                }
            }
        }.addOnFailureListener { e ->
            Log.e(Constants.TAG, "Failed to upload new media: $fileName", e)
        }
    }

    private fun updateMediaMessageInFirestore(
        messageId: String, newContent: String, mediaType: String
    ) {
        val chatId = currentChatId
        val messageRef =
            firestore.collection(Constants.CHATS).document(chatId).collection(Constants.MESSAGES)
                .document(messageId)

        messageRef.get().addOnSuccessListener { documentSnapshot ->
            val currentRevision = documentSnapshot.getLong(Constants.REVISION) ?: 0
            val newRevision = currentRevision + 1

            messageRef.update(
                mapOf(
                    Constants.MESSAGE to newContent,
                    Constants.MEDIA_TYPE to mediaType,
                    Constants.REVISION to newRevision
                )
            ).addOnSuccessListener {
                Log.d(
                    Constants.TAG,
                    "Message updated in Firestore: $messageId with new media type: $mediaType and revision: $newRevision"
                )
            }.addOnFailureListener { e ->
                Log.e(Constants.TAG, "Failed to update message in Firestore: $messageId", e)
            }
        }
    }

    private fun getMessages() {
        val chatId = currentChatId
        firestore.collection(Constants.CHATS).document(chatId).collection(Constants.MESSAGES)
            .orderBy(Constants.SENT_ON).addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val list = mutableListOf<Map<String, Any>>()
                value?.forEach { doc ->
                    val data = doc.data
                    data[Constants.IS_CURRENT_USER] =
                        Firebase.auth.currentUser?.uid == data[Constants.SENT_BY].toString()
                    list.add(data)
                }

                updateMessages(list)
            }
    }

    private fun updateMessages(list: MutableList<Map<String, Any>>) {
        _messages.value = list.asReversed()
    }
}

