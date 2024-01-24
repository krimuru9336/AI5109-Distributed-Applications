/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

package com.felixstumpf.chatapp

data class MessageFeed(
    val participant: String,
    var messages: List<MessageEntity>
)
