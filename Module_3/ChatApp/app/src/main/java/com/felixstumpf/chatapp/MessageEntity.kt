/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

package com.felixstumpf.chatapp


data class MessageEntity(
    val id: Int,
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
        0,
        "",
        "",
        "",
        com.google.firebase.Timestamp(java.util.Date(System.currentTimeMillis()))
    )

}


