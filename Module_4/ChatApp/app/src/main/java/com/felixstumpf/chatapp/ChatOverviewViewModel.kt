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
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow

class ChatOverviewViewModel(private val chatViewModel: ChatViewModel) : ViewModel() {

    //region Firebase Firestore

    val db = Firebase.firestore

    //endregion

    //region LiveData

    private val _currentUser = MutableStateFlow<FirebaseUser?>(Firebase.auth.currentUser)
    val currentUser: MutableStateFlow<FirebaseUser?> = _currentUser

    private val _messageFeeds = MutableLiveData(listOf<MessageFeed>())
    val messageFeeds: LiveData<List<MessageFeed>> = _messageFeeds

    //endregion

    //region Functions

    //region Setters

    fun setMessageFeeds(messageFeeds: List<MessageFeed>) {
        _messageFeeds.value = messageFeeds
    }

    fun setSelectedMessageFeed(messageFeed: MessageFeed) {
        chatViewModel.setMessageFeed(messageFeed)
        chatViewModel.getChatMessagesWithParticipant(messageFeed.participant)
    }

    //endregion


    fun getChatsForUser() {


        val collection = "chats"
        val fieldValue = currentUser.value?.email!!

        val query = db.collection(collection)
            .whereArrayContains("participants", fieldValue)

        query.get()
            .addOnSuccessListener { documents ->
                var retrievedMessageFeeds: List<MessageFeed> = listOf()
                for (document in documents) {
                    val data = document.data
                    val participants =
                        data.entries.first({ it.key == "participants" }).value as ArrayList<*>
                    retrievedMessageFeeds += MessageFeed(
                        participant = participants.first({ it != currentUser.value!!.email }) as String,
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

            }
            .addOnFailureListener { exception ->
                println("Firestore Query Error getting documents: $exception")
            }


    }

    //endregion

}