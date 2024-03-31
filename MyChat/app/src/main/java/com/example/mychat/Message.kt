package com.example.mychat

class Message {
    var message: String? = null
    var senderId: String? = null
    var timestamp: String? = null
    var id:String? = null
    var isEdited: Boolean = false
    var editTimestamp: String? = null
    var mediaType: String? = null
constructor(){}

    constructor(message: String?, senderId: String?, timestamp: String?, mediaType: String){
        this.message = message
        this.senderId = senderId
        this.timestamp = timestamp
        this.mediaType = mediaType
    }
}
