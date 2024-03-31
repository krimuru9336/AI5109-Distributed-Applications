package com.example.whatsdown.ui

import com.google.firebase.Timestamp

enum class Destination {
    Auth, Friends, Chat, Image, Video, Groups, GroupChat
}

data class User(
    val displayName: String,
    val profilePictureUri: String
)

data class Friend(
    val id: String,
    val name: String
)

data class Group(
    val id: String,
    val name: String
)

data class Message(
    val id: String,
    val sender: String,
    val receiver: String,
    val message: String,
    val type: String,
    val timeStamp: Timestamp
)

data class AppState(
    val user: User? = null,
    val friends: List<Friend> = listOf(),
    val groups: List<Group> = listOf(),
    val currentFriend: String? = null,
    val messages: List<Message> = listOf(),
    val currentGroup: String? = null
)