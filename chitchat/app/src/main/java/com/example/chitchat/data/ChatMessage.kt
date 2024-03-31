package com.example.chitchat.data

enum class MediaType{
    Image,
    Video,
    Gif,
    Text,
}

data class ChatMessage(
    var id: String = "",
    val sender: String = "",
    var text: String = "",
    val timestamp: Long = 0,
    val type: MediaType = MediaType.Text
)
