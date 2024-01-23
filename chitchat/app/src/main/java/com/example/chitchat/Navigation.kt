package com.example.chitchat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chitchat.data.FirebaseViewModel
import com.example.chitchat.data.UserViewModel
import com.example.chitchat.ui.screens.ChatScreen
import com.example.chitchat.ui.screens.ChatsListScreen
import com.example.chitchat.ui.screens.UsernameScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    val firebaseViewModel: FirebaseViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.UsernameScreen.route) {
        composable(route = Screen.UsernameScreen.route) {
            UsernameScreen(
                navController = navController,
                userViewModel = userViewModel,
                firebaseViewModel = firebaseViewModel,
            )
        }
        composable(
            route = Screen.ChatsListScreen.route + "/{name}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = "Azamat"
                    nullable = true
                }
            )
        ) { entry ->
            ChatsListScreen(
                navController = navController,
                userViewModel = userViewModel,
                firebaseViewModel = firebaseViewModel,
                name = entry.arguments?.getString("name"))
        }
        composable(
            route = Screen.ChatItemScreen.route + "/{chatId}",
            arguments = listOf(
                navArgument("chatId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            it.arguments?.getString("chatId")?.let { roomId ->
                ChatScreen(
                    navController = navController,
                    userViewModel = userViewModel,
                    roomId = roomId,
                    firebaseViewModel = firebaseViewModel,
                )
            }
        }

    }
}