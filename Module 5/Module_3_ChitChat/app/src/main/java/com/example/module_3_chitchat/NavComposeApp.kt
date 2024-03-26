package com.example.module_3_chitchat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.example.module_3_chitchat.nav.Action
import com.example.module_3_chitchat.nav.Destination.AuthenticationOption
import com.example.module_3_chitchat.nav.Destination.Home
import com.example.module_3_chitchat.nav.Destination.Chats
import com.example.module_3_chitchat.nav.Destination.Login
import com.example.module_3_chitchat.nav.Destination.Register
import com.example.module_3_chitchat.ui.theme.ChitChatTheme
import com.example.module_3_chitchat.view.AuthenticationView
import com.example.module_3_chitchat.view.home.HomeView
import com.example.module_3_chitchat.view.login.LoginView
import com.example.module_3_chitchat.view.chats.ChatsView
import com.example.module_3_chitchat.view.register.RegisterView

@Composable
fun NavComposeApp() {
    val navController = rememberNavController()
    val actions = remember(navController) { Action(navController) }
    ChitChatTheme {
        NavHost(
            navController = navController,
            startDestination = if (FirebaseAuth.getInstance().currentUser != null) Chats
            else AuthenticationOption
        ) {
            composable(AuthenticationOption) {
                AuthenticationView(
                    register = actions.register, login = actions.login
                )
            }
            composable(Register) {
                RegisterView(
                    chats = actions.chats, back = actions.navigateBack
                )
            }
            composable(Login) {
                LoginView(
                    chats = actions.chats, back = actions.navigateBack
                )
            }
            composable(Chats) {
                ChatsView(navController, back = actions.authOptions)
            }
            composable(Home) { navBackStackEntry ->
                val chatId = navBackStackEntry.arguments?.getString("chat_id") ?: ""
                val chatName = navBackStackEntry.arguments?.getString("chat_name") ?: ""
                HomeView(
                    chatId = chatId,
                    chatName = chatName,
                    back = actions.chats
                )
            }
        }
    }
}