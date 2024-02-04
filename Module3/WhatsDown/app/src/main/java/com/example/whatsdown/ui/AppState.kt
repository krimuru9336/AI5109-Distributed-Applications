package com.example.whatsdown.ui

enum class Destination {
    Start, JoinChat, Chat
}

data class Chat(
    val sender: String,
    val receiver: String,
    val message: String,
    val timeStamp: Long
)

data class AppState(
    val currentUser: String = "",
    val currentReceiver: String = "",
    val usersList: List<String> = listOf(),
    val chats: List<Chat> = listOf()
)