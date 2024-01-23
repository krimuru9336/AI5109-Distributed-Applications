package com.example.chitchat.data

data class ChatMessage(
    var id: String = "",
    val sender: String = "",
    val text: String = "",
    val timestamp: Long = 0,
)
