/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

package com.felixstumpf.chatapp


data class MessageEntity(
    val id: String,
    val senderEmail: String,
    val receiverEmail: String,
    val message: String,
    val timestamp: com.google.firebase.Timestamp = com.google.firebase.Timestamp(
        java.util.Date(
            System.currentTimeMillis()
        )
    )
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        com.google.firebase.Timestamp(java.util.Date(System.currentTimeMillis()))
    )

}


