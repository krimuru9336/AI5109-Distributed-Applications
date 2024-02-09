/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

package com.felixstumpf.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.felixstumpf.chatapp.models.MessageEntity
import com.felixstumpf.chatapp.models.MessageFeed
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow

// ViewModel for ChatOverview
class ChatOverviewViewModel(private val chatViewModel: ChatViewModel) : ViewModel() {

    //region Firebase Firestore

    // Initialize Firebase Firestore:
    val db = Firebase.firestore

    //endregion

    //region LiveData

    // Current User:
    private val _currentUser = MutableStateFlow<FirebaseUser?>(Firebase.auth.currentUser)
    val currentUser: MutableStateFlow<FirebaseUser?> = _currentUser

    // MessageFeeds:
    private val _messageFeeds = MutableLiveData(listOf<MessageFeed>())
    val messageFeeds: LiveData<List<MessageFeed>> = _messageFeeds

    // Refreshing:
    private val _refreshing = MutableLiveData(false)
    val refreshing: LiveData<Boolean> = _refreshing
    //endregion

    //region Functions

    //region Setters

    // Function to set MessageFeeds:
    fun setMessageFeeds(messageFeeds: List<MessageFeed>) {
        _messageFeeds.value = messageFeeds
    }

    // Function to set currently selected MessageFeed:
    fun setSelectedMessageFeed(messageFeed: MessageFeed) {
        chatViewModel.setMessageFeed(messageFeed)
        if (messageFeed.participants.size == 1){
        chatViewModel.getChatMessagesWithParticipant(messageFeed.participants.first()) }
        else{
            chatViewModel.getGroupChatMessagesByParticipants(messageFeed.participants)
        }
    }

    //endregion

    // Function to get all Chats for current User:
    fun getChatsForUser() {
        _refreshing.value = true
        val collection = "chats"
        val fieldValue = currentUser.value?.email!!

        val query = db.collection(collection)
            .whereArrayContains("participants", fieldValue)

        query.get()
            .addOnSuccessListener { documents ->
                val retrievedMessageFeeds: MutableList<MessageFeed> = mutableListOf()
                for (document in documents) {
                    val data = document.data
                    val participants =
                        data.entries.first({ it.key == "participants" }).value as ArrayList<String>
                    retrievedMessageFeeds += MessageFeed(
                        participants = participants.minus(fieldValue),
                        messages = listOf()
                    )


                    //println("FIREBASE DEBUG $document")
                    document.reference.collection("messages").get()
                        .addOnSuccessListener { docs ->
                            for (doc in docs) {
                                val msg = doc.toObject(MessageEntity::class.java)
                                retrievedMessageFeeds.last().messages += msg
                                //println("FIREBASE DEBUG $doc")

                            }
                            setMessageFeeds(retrievedMessageFeeds)

                            //println("FIREBASE DEBUG $retrievedMessageFeeds")
                        }



                }
                _refreshing.value = false
            }
            .addOnFailureListener { exception ->
                println("Firestore Query Error getting documents: $exception")
                _refreshing.value = false
            }


    }

    //endregion

}