package com.example.chitchat.data

enum class RoomType(val stringValue: String) {
    PRIVATE("private"),
    GROUP("group")
}

data class ChatRoom(
    val roomId: String = "",
    val users: List<String> = emptyList(),
    val roomType: RoomType,
    val chats: List<Any> = emptyList(),
    var roomName: String = "" // Either group name or other chat users name
) {
    constructor() : this(
        "",
        emptyList(),
        RoomType.PRIVATE,
        emptyList()
    )
}

