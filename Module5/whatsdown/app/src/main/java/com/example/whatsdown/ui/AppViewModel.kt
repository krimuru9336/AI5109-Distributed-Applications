package com.example.whatsdown.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    val player: ExoPlayer
) : ViewModel() {
    private var _appState = MutableStateFlow(value = AppState())
    val appState = _appState.asStateFlow()

    fun signIn(
        displayName: String,
        profilePictureUri: String,
        onSuccess: () -> Unit
    ) {
        val user = User(displayName, profilePictureUri)
        var friends: List<Friend>
        var groups: List<Group>

        db.collection("friends").whereEqualTo("yourName", displayName).get()
            .addOnSuccessListener { result1 ->
                friends = result1.map { document ->
                    Friend(document.id, document.getString("friendName")!!)
                }
                db.collection("groups").whereEqualTo("member", displayName).get()
                    .addOnSuccessListener { result2 ->
                        groups = result2.map { document ->
                            Group(document.id, document.getString("name")!!)
                        }
                        _appState.update {
                            it.copy(
                                user = user,
                                friends = friends,
                                groups = groups
                            )
                        }
                        onSuccess()
                    }
            }
    }

    fun signOut() {
        _appState.update { it.copy(user = null) }
    }

    fun addNewFriend(friendName: String) {
        db.collection("friends").add(
            mapOf(
                "yourName" to appState.value.user?.displayName,
                "friendName" to friendName
            )
        ).addOnSuccessListener { document ->
            _appState.update { it.copy(friends = it.friends + Friend(document.id, friendName)) }
        }
    }

    fun unfriend(friendId: String) {
        db.collection("friends").document(friendId).delete().addOnSuccessListener {
            _appState.update {
                it.copy(friends = it.friends.filter { friend -> friend.id != friendId })
            }
        }
    }

    fun startChat(friendName: String) {
        val currentPair = listOf(_appState.value.user?.displayName, friendName)
        db.collection("messages")
            .whereEqualTo("groupName", null)
            .whereIn("sender", currentPair)
            .whereIn("receiver", currentPair)
            .orderBy("timestamp")
            .addSnapshotListener { value, _ ->
                _appState.update { currentState ->
                    currentState.copy(
                        currentFriend = friendName,
                        messages = value?.map { document ->
                            Message(
                                id = document.id,
                                sender = document.getString("sender")!!,
                                receiver = document.getString("receiver")!!,
                                message = document.getString("message")!!,
                                type = document.getString("type")!!,
                                timeStamp = document.getTimestamp("timestamp")
                                    ?: Timestamp(1711699999, 0)
                            )
                        } ?: listOf()
                    )
                }
            }
    }

    fun sendMessage(message: String, type: String, groupName: String?) {
        db.collection("messages")
            .add(
                mapOf(
                    "sender" to appState.value.user?.displayName,
                    "receiver" to (appState.value.currentFriend ?: ""),
                    "message" to message,
                    "type" to type,
                    "groupName" to groupName,
                    "timestamp" to FieldValue.serverTimestamp()
                )
            )
    }

    fun editMessage(messageId: String, message: String) {
        db.collection("messages").document(messageId).update(
            mapOf(
                "message" to message
            )
        )
    }

    fun deleteMessage(messageId: String) {
        db.collection("messages").document(messageId).delete()
    }

    fun sendFile(file: Uri, type: String, groupName: String?) {
        val storageRef = storage.reference
        val fileRef = storageRef.child(file.lastPathSegment!!)
        val uploadTask = fileRef.putFile(file)

        uploadTask.continueWithTask { fileRef.downloadUrl }
            .addOnSuccessListener { sendMessage(it.toString(), type, groupName) }
    }

    fun createGroup(groupName: String) {
        db.collection("groups").add(
            mapOf(
                "name" to groupName,
                "member" to appState.value.user?.displayName
            )
        ).addOnSuccessListener { document ->
            _appState.update { it.copy(groups = it.groups + Group(document.id, groupName)) }
        }
    }

    fun leaveGroup(groupId: String) {
        db.collection("groups").document(groupId).delete().addOnSuccessListener {
            _appState.update {
                it.copy(groups = it.groups.filter { group -> group.id != groupId })
            }
        }
    }

    fun startGroupChat(groupName: String) {
        db.collection("messages")
            .whereEqualTo("groupName", groupName)
            .orderBy("timestamp")
            .addSnapshotListener { value, _ ->
                _appState.update { currentState ->
                    currentState.copy(
                        currentGroup = groupName,
                        messages = value?.map { document ->
                            Message(
                                id = document.id,
                                sender = document.getString("sender")!!,
                                receiver = document.getString("receiver")!!,
                                message = document.getString("message")!!,
                                type = document.getString("type")!!,
                                timeStamp = document.getTimestamp("timestamp")
                                    ?: Timestamp(1711699999, 0)
                            )
                        } ?: listOf()
                    )
                }
            }
    }
}