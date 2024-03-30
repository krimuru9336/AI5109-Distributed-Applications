package com.example.coolchat.model

import java.util.UUID

data class FriendlyMessage(
    val id: String? = null,
    val text: String? = null,
    val name: String? = null,
    val photoUrl: String? = null,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val timestamp: Long = 0,
)
