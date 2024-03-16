package com.example.disapp.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Message(
    val sender: String,
    val message: String,
    val mediaUrl:String,
    val date: String = getFormattedDate(),
    val group:Int
)

data class FBMessage(
    val key: String,
    val sender: String,
    val message: String,
    val date: String,
    val group:Int,
    val mediaUrl:String,
)


typealias OnSelection = (u: User) -> Unit
typealias OnGroupSelection = (v:Int) -> Unit

enum class User {
    Iman,
    Elina
}

const val time_stamp_pattern = "yy-dd-MM HH:mm:ss"
fun getFormattedDate(): String {
    val formatter = DateTimeFormatter.ofPattern(time_stamp_pattern)
    return LocalDateTime.now().format(formatter)
}

fun generateRandomString(length: Int): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}
