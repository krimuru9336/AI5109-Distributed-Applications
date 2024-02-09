/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

package com.felixstumpf.chatapp.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.felixstumpf.chatapp.models.MessageEntity
import com.felixstumpf.chatapp.models.MessageFeed
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow

// ViewModel for a specific Chat
class ChatViewModel : ViewModel() {

    //region Firebase Firestore

    // Initialize Firebase Firestore:
    val db = Firebase.firestore

    // Initialize Firebase Storage with root reference:
    val storageRef = Firebase.storage.reference

    //endregion


    //region LiveData

    // Current User:
    private val _currentUser = MutableStateFlow<FirebaseUser?>(Firebase.auth.currentUser)
    val currentUser: MutableStateFlow<FirebaseUser?> = _currentUser

    // Currently selected Message:
    private val _selectedMessage =
        MutableLiveData<MessageEntity>(MessageEntity("", "", listOf(), ""))
    val selectedMessage: LiveData<MessageEntity> = _selectedMessage

    // MessageFeed:
    private val _messageFeed = MutableLiveData(MessageFeed(listOf(), listOf()))
    val messageFeed: LiveData<MessageFeed> = _messageFeed

    // Message to send:
    private val _messageToSend = MutableLiveData(MessageEntity("", "", listOf(), ""))
    val messageToSend: LiveData<MessageEntity> = _messageToSend

    // Media to send:
    private val _mediaToSendUri = MutableLiveData<Uri?>(null)
    val mediaToSendUri: LiveData<Uri?> = _mediaToSendUri

    // Media Data as ByteArray:
    private val _mediaAsByteArray = MutableLiveData<ByteArray?>(null)
    val mediaAsByteArray: LiveData<ByteArray?> = _mediaAsByteArray

    //endregion


    //region Setters

    // Function to set Media URI to send:
    fun setMediaToSendUri(uri: Uri?) {
        _mediaToSendUri.value = uri
    }

    // Function to set Media Data as ByteArray:
    fun setMediaAsByteArray(byteArray: ByteArray?) {
        _mediaAsByteArray.value = byteArray
    }

    // Function to set currently selected Message:
    fun setSelectedMessage(message: MessageEntity) {
        _selectedMessage.value = message
    }

    // Function to set MessageFeed:
    fun setMessageFeed(messageFeed: MessageFeed) {
        _messageFeed.value = messageFeed
    }

    // Function to set Message to send:
    fun setMessageToSend(message: MessageEntity) {
        _messageToSend.value = message
    }

    // Function to set Message to send by only text, initializing a MessageEntity object:
    fun setMessageToSendByText(text: String) {
        val message = MessageEntity(
            id = "", //set when sending
            message = text,
            senderEmail = currentUser.value?.email!!.lowercase(),
            receiverEmails = _messageFeed.value!!.participants
        )

        _messageToSend.value = message
    }

    //endregion

    //region Functions

    // Function to send Message/push message to Firebase:
    fun sendMessage() {

        var message: MessageEntity =
            _messageToSend.value ?: throw IllegalArgumentException("Message expected!!")


        if ((message.message.isNotEmpty() || mediaToSendUri.value != null) && message.receiverEmails.isNotEmpty() && message.senderEmail.isNotEmpty()) {

            val chatsRef = db.collection("chats")
            val participants: List<String> =
                listOf(
                    currentUser.value?.email!!
                ).plus(message.receiverEmails.map { it.lowercase() }).sortedBy { it }

            val query = chatsRef.whereArrayContains(
                "participants",
                currentUser.value?.email!!.lowercase().toString()
            )

            query.get().addOnSuccessListener { querySnapshot ->
                //println("FIREBASE DEBUG $querySnapshot / ${querySnapshot.documents}")
                if (!querySnapshot.isEmpty) {
                    val chatRef = querySnapshot.documents.firstOrNull {
                        it.data!!.values.contains(participants)
                    }!!.reference
                    val messagesRef = chatRef.collection("messages").document()
                    message = message.copy(id = messagesRef.id)

                    //IF MEDIA:
                    if (_mediaToSendUri.value != null) {

                        storageRef.child(messagesRef.id).putBytes(_mediaAsByteArray.value!!)
                            .addOnSuccessListener {
                                storageRef.child(messagesRef.id).downloadUrl.addOnSuccessListener {
                                    storageRef.child(messagesRef.id).metadata.addOnSuccessListener { meta ->
                                        message = message.copy(
                                            mediaUrl = it.toString(),
                                            mediaType = meta.contentType
                                        )
                                        messagesRef.set(message).addOnSuccessListener {
                                            _messageToSend.value =
                                                MessageEntity("", "", listOf(), "")
                                            _mediaToSendUri.value = null
                                            messagesRef.set(message).addOnSuccessListener {
                                                _messageToSend.value =
                                                    MessageEntity("", "", listOf(), "")
                                                _mediaToSendUri.value = null
                                            }
                                        }
                                    }
                                }
                            }

                    } else {
                        messagesRef.set(message).addOnSuccessListener {
                            _messageToSend.value = MessageEntity("", "", listOf(), "")
                            _mediaToSendUri.value = null
                        }
                    }
                } else {
                    val chatRef = chatsRef.document()
                    chatRef.set(
                        hashMapOf(
                            "participants" to participants
                        )
                    ).addOnSuccessListener {


                        val messagesRef = chatRef.collection("messages").document()

                        if (_mediaToSendUri.value != null) {

                            storageRef.child(messagesRef.id).putBytes(_mediaAsByteArray.value!!)
                                .addOnSuccessListener {
                                    storageRef.child(messagesRef.id).downloadUrl.addOnSuccessListener {
                                        storageRef.child(messagesRef.id).metadata.addOnSuccessListener { meta ->
                                            message = message.copy(
                                                mediaUrl = it.toString(),
                                                mediaType = meta.contentType
                                            )
                                            messagesRef.set(message).addOnSuccessListener {
                                                _messageToSend.value =
                                                    MessageEntity("", "", listOf(), "")
                                                _mediaToSendUri.value = null
                                                messagesRef.set(message).addOnSuccessListener {
                                                    _messageToSend.value =
                                                        MessageEntity("", "", listOf(), "")
                                                    _mediaToSendUri.value = null
                                                }
                                            }
                                        }
                                    }
                                }

                        } else {
                            message = message.copy(id = messagesRef.id)
                            messagesRef.set(message).addOnSuccessListener {
                                _messageToSend.value = MessageEntity("", "", listOf(), "")
                                _mediaToSendUri.value = null
                            }
                        }
                    }
                }
            }
        }
    }

    // Function to update already sent message in Firebase:
    fun updateMessage() {

        if (selectedMessage.value!!.senderEmail == currentUser.value?.email!!) {

            var message: MessageEntity =
                _selectedMessage.value ?: throw IllegalArgumentException("Message expected!!")

            if (!message.message.contains("[EDITED]")) {
                message = message.copy(message = "[EDITED] ${message.message}")
            }

            if (message.message.isNotEmpty() && message.receiverEmails.isNotEmpty() && message.senderEmail.isNotEmpty()) {

                val chatsRef = db.collection("chats")
                val participants: List<String> =
                    listOf(
                        currentUser.value?.email!!
                    ).plus(message.receiverEmails.map { it.lowercase() }).sortedBy { it }

                val query = chatsRef.whereArrayContains(
                    "participants",
                    currentUser.value?.email!!.lowercase().toString()
                )

                query.get().addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val chatRef = querySnapshot.documents.firstOrNull {
                            it.data!!.values.contains(participants)
                        }!!.reference
                        val messagesRef = chatRef.collection("messages").document(message.id)

                        messagesRef.set(message)
                            .addOnSuccessListener {
                                _selectedMessage.value = MessageEntity("", "", listOf(), "")
                            }
                    }
                }
            }
        }
    }

    // Function to delete already sent message in Firebase:
    fun deleteMessage() {

        if (selectedMessage.value!!.senderEmail == currentUser.value?.email!!) {

            var message: MessageEntity =
                _selectedMessage.value ?: throw IllegalArgumentException("Message expected!!")


            if (message.message.isNotEmpty() && message.receiverEmails.isNotEmpty() && message.senderEmail.isNotEmpty()) {

                val chatsRef = db.collection("chats")
                val participants: List<String> =
                    listOf(
                        currentUser.value?.email!!
                    ).plus(message.receiverEmails.map { it.lowercase() }).sortedBy { it }

                val query = chatsRef.whereArrayContains(
                    "participants",
                    currentUser.value?.email!!.lowercase().toString()
                )

                query.get().addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val chatRef = querySnapshot.documents.firstOrNull {
                            it.data!!.values.contains(participants)
                        }!!.reference
                        val messagesRef = chatRef.collection("messages").document(message.id)

                        messagesRef.delete()
                            .addOnSuccessListener {
                                _selectedMessage.value = MessageEntity("", "", listOf(), "")
                            }

                    }
                }
            }
        }
    }


    // Function to get all messages / get MessageFeed for current current with a specific participant:
    fun getChatMessagesWithParticipant(participantEmail: String) {

        val collection = "chats"
        val participants = listOf(
            currentUser.value?.email!!.lowercase(),
            participantEmail.lowercase()
        ).sortedBy { it }

        setMessageFeed(MessageFeed(participants = listOf(participantEmail), messages = listOf()))

        val query = db.collection(collection)
            .whereArrayContains("participants", currentUser.value?.email!!.lowercase().toString())

        query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                println("Firestore Listen failed: $e")
                return@addSnapshotListener
            }

            val ref = snapshots?.documents?.firstOrNull {
                it.data?.values?.contains(participants) ?: false
            }

            if (ref == null) {
                val newChatRef = db.collection(collection).document()
                newChatRef.set(
                    hashMapOf("participants" to participants)
                )
                    .addOnSuccessListener {
                        observeMessagesInChat(newChatRef, participantEmail)
                    }.addOnFailureListener { exception ->
                        println("Failed to create a new chat: $exception")
                    }
            } else {
                val existingChatRef = ref.reference
                observeMessagesInChat(existingChatRef, participantEmail)
            }
        }
    }

    // Function to get all Messages / get MessageFeed for current current with a specific group of participants:
    fun getGroupChatMessagesByParticipants(multipleParticipants: List<String>) {

        val collection = "chats"
        val participants: List<String> =
            listOf(
                currentUser.value?.email!!
            ).plus(multipleParticipants.map { it.lowercase() }).sortedBy { it }

        setMessageFeed(MessageFeed(participants = multipleParticipants, messages = listOf()))

        val query = db.collection(collection)
            .whereArrayContains("participants", currentUser.value?.email!!.lowercase().toString())

        query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                println("Firestore Listen failed: $e")
                return@addSnapshotListener
            }


            val ref = snapshots?.documents?.firstOrNull {
                it.data?.values?.contains(participants) ?: false
            }
            if (ref == null) {
                val newChatRef = db.collection(collection).document()
                val newChatRefId = newChatRef.id
                newChatRef.set(
                    hashMapOf("participants" to participants)
                )
                    .addOnSuccessListener {
                        observeMessagesInChat(newChatRef, newChatRefId)
                    }.addOnFailureListener { exception ->
                        println("Failed to create a new chat: $exception")
                    }
            } else {
                val existingChatRef = ref.reference
                val existingChatRefId = existingChatRef.id
                observeMessagesInChat(existingChatRef, existingChatRefId)
            }
        }
    }

    //endregion

    //region Private Functions

    // Function to observe Messages in the current chat:
    private fun observeMessagesInChat(chatRef: DocumentReference, participantEmail: String) {

        chatRef.collection("messages").addSnapshotListener { messageSnapshots, messageError ->
            if (messageError != null) {
                println("Firestore Listen for messages failed: $messageError")
                return@addSnapshotListener
            }

            val messageFeed =
                MessageFeed(participants = listOf(participantEmail), messages = mutableListOf())

            messageSnapshots?.documents?.forEach { msgDocument ->
                val msg = msgDocument.toObject(MessageEntity::class.java)
                if (msg != null) {
                    messageFeed.messages += msg
                }
            }

            println("Updated Message Feed: $messageFeed")
            setMessageFeed(_messageFeed.value!!.copy(messages = messageFeed.messages.sortedBy { it.timestamp }))
        }
    }

    //endregion


}