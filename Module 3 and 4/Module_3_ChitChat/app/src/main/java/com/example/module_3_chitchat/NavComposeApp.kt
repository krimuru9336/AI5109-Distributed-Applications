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
import com.example.module_3_chitchat.nav.Destination.Login
import com.example.module_3_chitchat.nav.Destination.Register
import com.example.module_3_chitchat.ui.theme.ChitChatTheme
import com.example.module_3_chitchat.view.AuthenticationView
import com.example.module_3_chitchat.view.home.HomeView
import com.example.module_3_chitchat.view.login.LoginView
import com.example.module_3_chitchat.view.register.RegisterView

@Composable
fun NavComposeApp() {
    val navController = rememberNavController()
    val actions = remember(navController) { Action(navController) }
    ChitChatTheme {
        NavHost(
            navController = navController,
            startDestination = if (FirebaseAuth.getInstance().currentUser != null) Home
            else AuthenticationOption
        ) {
            composable(AuthenticationOption) {
                AuthenticationView(
                    register = actions.register, login = actions.login
                )
            }
            composable(Register) {
                RegisterView(
                    home = actions.home, back = actions.navigateBack
                )
            }
            composable(Login) {
                LoginView(
                    home = actions.home, back = actions.navigateBack
                )
            }
            composable(Home) {
                HomeView(
                    back = actions.authOptions
                )
            }
        }
    }
}