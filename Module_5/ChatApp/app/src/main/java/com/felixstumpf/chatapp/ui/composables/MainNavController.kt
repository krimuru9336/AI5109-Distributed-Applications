/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications


package com.felixstumpf.chatapp.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.felixstumpf.chatapp.viewmodels.ChatOverviewViewModel
import com.felixstumpf.chatapp.viewmodels.ChatViewModel
import com.felixstumpf.chatapp.viewmodels.LoginViewModel
import com.felixstumpf.chatapp.viewmodels.RegisterViewModel

// NavController for the entire app
// Handles navigation between screens and passes ViewModels to screen composables
@Composable
fun MainNavigation(
    chatOverviewViewModel: ChatOverviewViewModel,
    chatViewModel: ChatViewModel,
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel
) {
    val navController = rememberNavController()

    val startDestination = remember { mutableStateOf("start") }

    // If user is already logged in / logged in in persisted state, start at chats screen instead of start screen:
    if (chatOverviewViewModel.currentUser.value != null) {
        chatOverviewViewModel.getChatsForUser()
        startDestination.value = "chats"
    } else {
        startDestination.value = "start"
    }

    NavHost(navController = navController, startDestination = startDestination.value) {
        composable("start") {
            StartScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController, registerViewModel)
        }
        composable("login") {
            LoginScreen(navController, loginViewModel, chatViewModel, chatOverviewViewModel)
        }
        composable("chats") {
            ChatOverviewScaffold(navController, chatViewModel, chatOverviewViewModel)
        }
        composable("chatMessageFeed") {
            ChatScaffold(
                navController,
                chatViewModel = chatViewModel
            )
        }
    }
}