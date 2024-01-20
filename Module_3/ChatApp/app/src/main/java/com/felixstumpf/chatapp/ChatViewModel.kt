/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

package com.felixstumpf.chatapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow

class ChatViewModel() : ViewModel() {

    //region Firebase Firestore

    val db = Firebase.firestore

    //endregion


    //region LiveData

    private val _currentUser = MutableStateFlow<FirebaseUser?>(Firebase.auth.currentUser)
    val currentUser: MutableStateFlow<FirebaseUser?> = _currentUser

    private val _messageFeed = MutableLiveData(MessageFeed("", listOf()))
    val messageFeed: LiveData<MessageFeed> = _messageFeed

    private val _messageToSend = MutableLiveData(MessageEntity(0, "", "", ""))
    val messageToSend: LiveData<MessageEntity> = _messageToSend

    //endregion


    //region Setters

    fun setMessageFeed(messageFeed: MessageFeed) {
        _messageFeed.value = messageFeed
    }

    fun setMessageToSend(message: MessageEntity) {
        _messageToSend.value = message
    }

    fun setMessageToSendByText(text: String) {
        val message: MessageEntity = MessageEntity(
            id = 0, //TODO
            message = text,
            senderEmail = currentUser.value?.email!!.lowercase(),
            receiverEmail = _messageFeed.value!!.participant.lowercase()
        ) //TODO

        _messageToSend.value = message
    }

    //endregion

    //region Functions

    fun sendMessage() {
        val message: MessageEntity =
            _messageToSend.value ?: throw IllegalArgumentException("Message expected!!")

        if (message.message.isNotEmpty() && message.receiverEmail.isNotEmpty() && message.senderEmail.isNotEmpty()) {

            val chatsRef = db.collection("chats")
            val participants =
                listOf(
                    currentUser.value?.email!!.lowercase(),
                    message.receiverEmail.lowercase()
                ).sortedBy { it }

            val query = chatsRef.whereIn("participants", participants)



            query.get().addOnSuccessListener { querySnapshot ->
                //println("FIREBASE DEBUG $querySnapshot / ${querySnapshot.documents}")
                if (!querySnapshot.isEmpty) {
                    val chatRef = querySnapshot.documents[0].reference
                    val messagesRef = chatRef.collection("messages").document()

                    messagesRef.set(message).addOnSuccessListener {
                        _messageToSend.value = MessageEntity(0, "", "", "")
                    }

                } else {
                    val chatRef = chatsRef.document(participants.joinToString(""))
                    chatRef.set(
                        hashMapOf(
                            "participants" to participants
                        )
                    ).addOnSuccessListener {
                        val messagesRef = chatRef.collection("messages").document()
                        messagesRef.set(message).addOnSuccessListener {
                            _messageToSend.value = MessageEntity(0, "", "", "")
                        }
                    }
                }
            }
        }
    }


    fun getChatMessagesWithParticipant(participantEmail: String) {
        val collection = "chats"
        val fieldValue = currentUser.value?.email!!
        val participants = listOf(
            currentUser.value?.email!!.lowercase(),
            participantEmail.lowercase()
        ).sortedBy { it }

        setMessageFeed(MessageFeed(participant = participantEmail, messages = listOf()))

        val query = db.collection(collection)
            .whereIn("participants", participants)



        query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                println("Firestore Listen failed: $e")
                return@addSnapshotListener
            }

            if (snapshots?.documents?.isEmpty() == true) {
                val newChatRef = db.collection(collection).document(participants.joinToString(""))
                newChatRef.set(
                    hashMapOf("participants" to participants)
                )
                    .addOnSuccessListener {
                        observeMessagesInChat(newChatRef, participantEmail)
                    }.addOnFailureListener { exception ->
                        println("Failed to create a new chat: $exception")
                    }
            } else {
                val existingChatRef = snapshots!!.documents[0].reference
                observeMessagesInChat(existingChatRef, participantEmail)
            }
        }
    }

    private fun observeMessagesInChat(chatRef: DocumentReference, participantEmail: String) {
        chatRef.collection("messages").addSnapshotListener { messageSnapshots, messageError ->
            if (messageError != null) {
                println("Firestore Listen for messages failed: $messageError")
                return@addSnapshotListener
            }

            val messageFeed =
                MessageFeed(participant = participantEmail, messages = mutableListOf())

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