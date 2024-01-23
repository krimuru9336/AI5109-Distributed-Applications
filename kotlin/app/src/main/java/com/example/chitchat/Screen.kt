package com.example.chitchat

sealed class Screen(val route: String) {
    object UsernameScreen: Screen("username_screen")
    object ChatsListScreen: Screen("chats_list_screen")
    object ChatItemScreen: Screen("chat_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}