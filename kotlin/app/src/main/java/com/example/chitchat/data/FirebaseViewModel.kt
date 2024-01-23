package com.example.chitchat.data

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseViewModel: ViewModel() {
    val database = Firebase.database("https://chitchat-a099f-default-rtdb.europe-west1.firebasedatabase.app/")

    val users = database.getReference("users")
    val chatRoomsReference = database.getReference("chat_rooms")

    private val _roomsList = mutableStateListOf<ChatRoom>()
    val roomsList: List<ChatRoom>
        get() = _roomsList

    private val _usersList = mutableStateListOf<User>()
    val usersList: List<User>
        get() = _usersList

//    private val _chats = mutableStateListOf<ChatMessage>()
//    val chats: List<ChatMessage>
//        get() = _chats
    private val _chatMessages = MutableLiveData<List<ChatMessage>>()
    val chatMessages: LiveData<List<ChatMessage>> get() = _chatMessages


//    val usersList: MutableStateFlow<List<User>> = MutableStateFlow(emptyList())

    fun getChatRooms(userId: String) {
        _roomsList.clear()
//        viewModelScope.launch {
//           try {
//               chatRoomsReference.addValueEventListener(object : ValueEventListener {
//                   override fun onDataChange(snapshot: DataSnapshot) {
//                       // Clear the existing list of users
//                       _roomsList.clear()
//                       // Iterate over the snapshot and add each user to the list
//                       if (snapshot.exists()) {
//                           // Convert DataSnapshot to a list of ChatRoom objects
//                           val rooms = snapshot.children.mapNotNull {
//                               val room = it.getValue(ChatRoom::class.java)
//                               if (room?.roomType == RoomType.PRIVATE) {
//                                   val chatUserId = room.users.findLast { it != userId }
//                                   if (chatUserId != null) {
//                                       val userSnapshot = users.child(chatUserId).get().await()
//                                       val user = userSnapshot.getValue(User::class.java)
//                                       // Add user information to chat room
//
//                                   }
//                               }
//                               if (room != null) {
//                                   _roomsList.add(room)
//                               }
//                           }
//                           print(rooms)
//                       }
//                   }
//
//                   override fun onCancelled(error: DatabaseError) {
//                       // Handle any errors that occur
//                       Log.e("ChatRoomsFailed", "Error fetching users from Firebase", error.toException())
//                   }
//               })
//           } catch (e: Exception) {
//               e.printStackTrace()
//           }
//        }
        viewModelScope.launch {
            try {
                val dataSnapshot = chatRoomsReference.get().await()
                if (dataSnapshot.exists()) {
                    // Convert DataSnapshot to a list of ChatRoom objects
                    val rooms = dataSnapshot.children.mapNotNull {
                        var room = it.getValue(ChatRoom::class.java)
                        if (room?.roomType == RoomType.PRIVATE) {
                            val chatUserId = room.users.findLast { it != userId }
                            if (chatUserId != null) {
                                val userSnapshot = users.child(chatUserId).get().await()
                                val user = userSnapshot.getValue(User::class.java)
                                if (user != null) {
                                    room.roomName = user.username
                                }
                                // Add user information to chat room

                            }
                        }
                        if (room != null) {
                            _roomsList.add(room)
                        }
                    }

                    print(rooms)
                }
            } catch (e: Exception) {
                // Handle errors
                e.printStackTrace()
            }
        }
    }

    fun getUsers() {
        // Get a reference to the users node in your Firebase Realtime Database
        // Attach a listener to the users node to listen for changes
        users.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the existing list of users
                _usersList.clear()

                // Iterate over the snapshot and add each user to the list
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        _usersList.add(user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors that occur
                Log.e("UserViewModel", "Error fetching users from Firebase", error.toException())
            }
        })
    }

    fun createPrivateChat(
        currentUserId: String,
        newChatUserIds: List<String>,
        roomType: RoomType,

    ) {
       newChatUserIds.forEach {
           val roomId = chatRoomsReference.push().key ?: return
           val chatRoom = ChatRoom(
               roomId = roomId,
               users = listOf(currentUserId, it),
               chats = emptyList(),
               roomType = roomType
           )
           chatRoomsReference
               .child(roomId)
               .setValue(chatRoom)
       }
    }

    fun getChats(roomId: String) {
        clearChats()
        val chatReference = chatRoomsReference.child(roomId).child("messages")
        chatReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)

                Log.d("New message", message.toString())
                message?.let {
                    val currentList = _chatMessages.value.orEmpty().toMutableList()
                    currentList.add(it)
                    _chatMessages.value = currentList
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle changes if needed
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Handle removal if needed
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle movement if needed
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }

    fun sendChatMessage(roomId: String, message: ChatMessage) {
        val chatReference = chatRoomsReference
            .child(roomId)
            .child("messages")

        val newMessageRef = chatReference.push()
        val messageId = newMessageRef.key ?: return
        message.id = messageId

        newMessageRef
            .setValue(message)

    }

    fun clearChats() {
        _chatMessages.value = emptyList<ChatMessage>().toMutableList()
    }
}
