/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

package com.felixstumpf.chatapp.models

// Message Entity data class that represents a message in the database
data class MessageEntity(
    val id: String,
    val senderEmail: String,
    val receiverEmails: List<String>,
    val message: String,
    val timestamp: com.google.firebase.Timestamp = com.google.firebase.Timestamp(
        java.util.Date(
            System.currentTimeMillis()
        )
    ),
    val mediaType : String? = null,
    val mediaUrl : String = ""
) {

    // Empty constructor for Firebase
    constructor() : this(
        "",
        "",
        listOf(),
        "",
        com.google.firebase.Timestamp(java.util.Date(System.currentTimeMillis())),
        null,
        ""
    )

}


