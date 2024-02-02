package com.example.rahilchatapplication

import java.time.LocalDateTime

class Message {
    var message: String? = null
    var senderId: String? = null
    var time: String? = null
    var senderRoomId: String? = null
    var receiverRoomId: String? = null

    constructor(){}

    constructor(message: String?, senderId: String?, time: String?, senderRoomId: String?, receiverRoomId: String?){
        this.message = message
        this.senderId = senderId
        this.time = time
        this.senderRoomId = senderRoomId
        this.receiverRoomId = receiverRoomId

    }
}