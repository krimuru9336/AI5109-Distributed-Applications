package com.example.rahilchatapplication

import java.time.LocalDateTime

class Message {
    var message: String? = null
    var senderId: String? = null
    var time: String? = null
    var roomId: String? = null

    constructor(){}

    constructor(message: String?, senderId: String?, time: String?, roomId: String?){
        this.message = message
        this.senderId = senderId
        this.time = time
        this.roomId = roomId

    }
}