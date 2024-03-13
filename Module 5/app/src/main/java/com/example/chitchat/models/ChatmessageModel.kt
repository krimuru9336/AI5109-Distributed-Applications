package com.example.chitchat.models

import com.google.firebase.Timestamp


class ChatmessageModel {
    var message: String? = null
    var senderId: String? = null
    var timestamp: Timestamp? = null
    lateinit var messageType: MessageType

    constructor()
    constructor(
        message: String?,
        senderId: String?,
        timestamp: Timestamp?,
        messageType: MessageType
    ) {
        this.message = message
        this.senderId = senderId
        this.timestamp = timestamp
        this.messageType = messageType
    }
}

enum class MessageType {
    TEXT, IMAGE, VIDEO
}
