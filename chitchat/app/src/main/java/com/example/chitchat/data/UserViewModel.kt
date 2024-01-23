package com.example.chitchat.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class UserViewModel: ViewModel() {
    var user by mutableStateOf<String>("")
        private set

    var userObj by mutableStateOf<User?>(null)
        private set

    var userId by mutableStateOf<String?>(null)
        private set

    fun changeUser(username: String) {
        user = username
    }

    fun changeUserObj(newUser: User) {
        userObj = newUser
        userId = newUser.id
    }
}
