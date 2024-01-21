package com.example.chitchat.models


class ChatroomModel {
    var chatroomId: String? = null
    var userIds: List<String>? = null

    constructor()
    constructor(chatroomId: String?, userIds: List<String>?) {
        this.chatroomId = chatroomId
        this.userIds = userIds
    }
}

