/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications


package com.felixstumpf.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import com.felixstumpf.chatapp.ui.composables.MainNavigation
import com.felixstumpf.chatapp.ui.theme.ChatAppTheme
import com.felixstumpf.chatapp.viewmodels.ChatOverviewViewModel
import com.felixstumpf.chatapp.viewmodels.ChatViewModel
import com.felixstumpf.chatapp.viewmodels.LoginViewModel
import com.felixstumpf.chatapp.viewmodels.RegisterViewModel

class MainActivity : ComponentActivity() {

    //region ViewModels
    // Initialize ViewModels:
    private val registerViewModel = RegisterViewModel()
    private val loginViewModel = LoginViewModel()
    private val chatViewModel = ChatViewModel()
    private val chatOverviewViewModel = ChatOverviewViewModel(chatViewModel)

    //endregion

    //region On Create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme(isSystemInDarkTheme()) {
                // Pass ViewModels to MainNavigation:
                MainNavigation(
                    chatOverviewViewModel,
                    chatViewModel,
                    loginViewModel,
                    registerViewModel
                )
            }
        }

    }
    //endregion


}