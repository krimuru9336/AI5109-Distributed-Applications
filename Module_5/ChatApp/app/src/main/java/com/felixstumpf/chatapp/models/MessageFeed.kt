/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

package com.felixstumpf.chatapp.models

data class MessageFeed(
    val participants: List<String>,
    var messages: List<MessageEntity>
)
