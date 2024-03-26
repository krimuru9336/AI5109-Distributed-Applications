package com.example.module_3_chitchat.nav

import androidx.navigation.NavHostController
import com.example.module_3_chitchat.nav.Destination.Home
import com.example.module_3_chitchat.nav.Destination.Login
import com.example.module_3_chitchat.nav.Destination.Register
import com.example.module_3_chitchat.nav.Destination.AuthenticationOption
import com.example.module_3_chitchat.nav.Destination.Chats

object Destination {
    const val AuthenticationOption = "authenticationOption"
    const val Register = "register"
    const val Login = "login"
    const val Chats = "chats"
    const val Home = "home/{chat_id}/{chat_name}"
}

class Action(navController: NavHostController) {
    val home: () -> Unit = { navController.navigate(Home) }
    val login: () -> Unit = { navController.navigate(Login) }
    val register: () -> Unit = { navController.navigate(Register) }
    val authOptions: () -> Unit = { navController.navigate(AuthenticationOption) }

    val chats: () -> Unit = {
        navController.navigate(Chats) {
            popUpTo(Login) {
                inclusive = true
            }
            popUpTo(Register) {
                inclusive = true
            }
        }
    }

    val navigateBack: () -> Unit = { navController.popBackStack() }

}