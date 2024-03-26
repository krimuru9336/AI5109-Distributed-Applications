package com.example.module_3_chitchat

object Constants {
    const val TAG = "chitchat"

    const val CHATS = "chats"
    const val CHAT_ROOMS = "chat_rooms"
    const val MESSAGES = "messages"
    const val MESSAGE = "message"
    const val SENT_BY = "sent_by"
    const val SENT_ON = "sent_on"
    const val IS_CURRENT_USER = "is_current_user"
    const val MESSAGE_ID = "message_id"
    const val MEDIA_TYPE = "media_type"
    const val REVISION = "revision"

    enum class MessageType {
        TEXT, IMAGE, VIDEO, GIF
    }
}
