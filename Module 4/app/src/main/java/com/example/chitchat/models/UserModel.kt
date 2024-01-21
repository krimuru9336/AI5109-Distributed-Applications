package com.example.chitchat.models

class UserModel {
    var username: String? = null
    var userId: String? = null

    constructor()
    constructor(username: String?, userId: String?) {
        this.username = username
        this.userId = userId
    }
}

