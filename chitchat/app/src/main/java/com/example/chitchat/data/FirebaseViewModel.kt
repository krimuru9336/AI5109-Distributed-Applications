package com.example.chitchat.data

import android.net.Uri
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
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

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

    private val _chatMessages = MutableLiveData<List<ChatMessage>>()
    val chatMessages: LiveData<List<ChatMessage>> get() = _chatMessages

    fun getChatRooms(userId: String) {
        _roomsList.clear()
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
    ) {
       newChatUserIds.forEach {
           val roomId = chatRoomsReference.push().key ?: return
           val chatRoom = ChatRoom(
               roomId = roomId,
               users = listOf(currentUserId, it),
               chats = emptyList(),
               roomType = RoomType.PRIVATE
           )
           chatRoomsReference
               .child(roomId)
               .setValue(chatRoom)
       }
    }

    fun createGroupChat(
        currentUserId: String,
        newChatUserIds: List<String>,
        roomName: String
    ) {
        val roomId = chatRoomsReference.push().key ?: return;
        val chatRoom = ChatRoom(
            roomId = roomId,
            users = newChatUserIds + currentUserId,
            chats = emptyList(),
            roomType = RoomType.GROUP,
            roomName = roomName
        );
        print(chatRoom);
        chatRoomsReference
            .child(roomId)
            .setValue(chatRoom)
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
                val message = snapshot.getValue(ChatMessage::class.java)
                message?.let {
                    _chatMessages.value = _chatMessages.value?.toMutableList()?.apply {
                        for ((index, msg) in this.withIndex()) {
                            if (msg.id == message.id) {
                                this[index] = msg.copy(text = message.text)
                                break
                            }
                        }
                    }
                }
                print(_chatMessages.value)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Handle removal if needed
                val message = snapshot.getValue(ChatMessage::class.java)
                message?.let {
                    _chatMessages.value = _chatMessages.value?.filter { it.id != message?.id }
                }
                print(snapshot.value)
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

    fun deleteMessage(messageId: String, roomId: String) {
        val messageRef = chatRoomsReference
            .child(roomId)
            .child("messages")
            .child(messageId)

        messageRef.removeValue()
    }

    fun editMessage(roomId: String, messageId: String, newText: String) {
        val messageRef = chatRoomsReference
            .child(roomId)
            .child("messages")
            .child(messageId)

        val updatedText = mapOf("text" to newText)
        messageRef.updateChildren(updatedText)
    }

    private fun detectExtension(uri: Uri): String? {
        val extension = uri.pathSegments.lastOrNull()?.substringAfterLast(".")
        extension?.let {
            // Handle the detected file extension here (e.g., display a toast message)
            return extension
        }
        return null
    }



    suspend fun onMediaUpload(uri: Uri, type: MediaType): String? {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val ext = if (type === MediaType.Image) "jpg" else if (type === MediaType.Video) "mp4" else "gif"
        val imagesRef = storageRef.child("media/${UUID.randomUUID()}.${ext}")

        return try {
            imagesRef.putFile(uri).await()
            print(imagesRef)
            val url = imagesRef.downloadUrl.await().toString()
            return url
        } catch (e: Exception) {
            // Handle any exceptions that occur during the upload process
            e.printStackTrace()
            null
        }
//        val storage = Firebase.storage
//
//        // Create a storage reference from our app
//        var storageRef = storage.reference
//
//        val unique_image_name = UUID.randomUUID()
//        var spaceRef: StorageReference
//
//        val type = detectMediaType(uri, context)
//        val ext = detectExtension(uri)
//
//        if (type == "image"){
//            spaceRef = storageRef.child("images/$unique_image_name.jpg")
//        } else{
//            spaceRef = storageRef.child("videos/$unique_image_name.mp4")
//        }
//
//        val byteArray: ByteArray? = context.contentResolver
//            .openInputStream(uri)
//            ?.use { it.readBytes() }
//
//        byteArray?.let{
//
//            var uploadTask = spaceRef.putBytes(byteArray)
//
//            uploadTask.await().let { taskSnapshot ->
//                return taskSnapshot.storage.path
//            }
//            }
//        }
    }
}
