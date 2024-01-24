package com.example.whatsdown

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.whatsdown.ui.AppTopBar
import com.example.whatsdown.ui.AppViewModel
import com.example.whatsdown.ui.Destination
import com.example.whatsdown.ui.screens.ChatScreen
import com.example.whatsdown.ui.screens.JoinChatScreen
import com.example.whatsdown.ui.screens.StartScreen
import com.example.whatsdown.ui.theme.WhatsDownTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhatsDownTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppUI()
                }
            }
        }
    }
}

@Composable
fun AppUI(
    modifier: Modifier = Modifier,
    vm: AppViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    fun showSnackbar(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message = message)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                currentScreen = backStackEntry?.destination?.route,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        val appState by vm.appState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = Destination.Start.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = Destination.Start.name) {
                StartScreen(
                    onEnterName = {
                        try {
                            vm.startChatting(it)
                            navController.navigate(Destination.JoinChat.name)
                        } catch (e: Exception) {
                            showSnackbar(e.message ?: "StartScreen.onEnterName")
                        }
                    }
                )
            }
            composable(route = Destination.JoinChat.name) {
                JoinChatScreen(
                    usersList = appState.usersList,
                    onJoinChat = {
                        try {
                            vm.joinChat(it)
                            navController.navigate(Destination.Chat.name)
                        } catch (e: Exception) {
                            showSnackbar(e.message ?: "JoinChatScreen.onJoinChat")
                        }
                    }
                )
            }
            composable(route = Destination.Chat.name) {
                ChatScreen(
                    currentUser = appState.currentUser,
                    currentReceiver = appState.currentReceiver,
                    messages = appState.chats,
                    sendMessage = {
                        try {
                            vm.sendMessage(it)
                        } catch (e: Exception) {
                            showSnackbar(e.message ?: "ChatScreen.sendMessage")
                        }
                    }
                )
            }
        }
    }
}