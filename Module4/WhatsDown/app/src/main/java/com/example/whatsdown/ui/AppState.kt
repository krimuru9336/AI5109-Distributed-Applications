package com.example.whatsdown.ui

enum class Destination {
    Start, JoinChat, Chat
}

data class Chat(
    val id: String = "id",
    val sender: String = "sender",
    val receiver: String = "receiver",
    val message: String = "message",
    val timeStamp: Long = 2024
)

data class AppState(
    val currentUser: String = "",
    val currentReceiver: String = "",
    val usersList: List<String> = listOf(),
    val chats: List<Chat> = listOf()
)