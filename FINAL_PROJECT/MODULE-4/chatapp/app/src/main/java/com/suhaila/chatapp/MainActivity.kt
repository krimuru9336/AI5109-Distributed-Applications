package com.suhaila.chatapp

import StartScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.suhaila.chatapp.R
import com.suhaila.chatapp.ui.AppTopBar
import com.suhaila.chatapp.ui.AppViewModel
import com.suhaila.chatapp.ui.Destination
import com.suhaila.chatapp.ui.MyDetails
import com.suhaila.chatapp.ui.screens.ChatScreen
import com.suhaila.chatapp.ui.screens.JoinChatScreen
import com.suhaila.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
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
    val appState by vm.appState.collectAsState()

    fun showSnackbar(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message = message)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titleText = when (backStackEntry?.destination?.route) {
                    Destination.JoinChat.name -> stringResource(R.string.join_chat)
                    Destination.Chat.name -> appState.currentReceiver
                    else -> stringResource(R.string.app_name)
                },
                canNavigateBack = navController.previousBackStackEntry != null,
                onNavigateUp = { navController.navigateUp() }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Start.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = Destination.Start.name) {
                Column {
                    MyDetails()
                    StartScreen(
                        onEnterName = {
                            try {
                                vm.startChatting(name = it)
                                navController.navigate(route = Destination.JoinChat.name)
                            } catch (e: Exception) {
                                showSnackbar(e.message ?: "StartScreen.onEnterName")
                            }
                        }
                    )
                }
            }
            composable(route = Destination.JoinChat.name) {
                Column {
                    MyDetails()
                    JoinChatScreen(
                        usersList = appState.usersList,
                        onJoinChat = {
                            try {
                                vm.joinChat(selectedUser = it)
                                navController.navigate(route = Destination.Chat.name)
                            } catch (e: Exception) {
                                showSnackbar(e.message ?: "JoinChatScreen.onJoinChat")
                            }
                        }
                    )
                }
            }
            composable(route = Destination.Chat.name) {
                Column {
                    MyDetails()
                    ChatScreen(
                        currentUser = appState.currentUser,
                        messages = appState.chats,
                        onSendMessage = {
                            try {
                                vm.sendMessage(message = it)
                            } catch (e: Exception) {
                                showSnackbar(e.message ?: "ChatScreen.onSendMessage")
                            }
                        },
                        onEditMessage = { messageId, message ->
                            try {
                                vm.editMessage(messageId = messageId, message = message)
                            } catch (e: Exception) {
                                showSnackbar(e.message ?: "ChatScreen.onEditMessage")
                            }
                        },
                        onDeleteMessage = {
                            try {
                                vm.deleteMessage(messageId = it)
                            } catch (e: Exception) {
                                showSnackbar(e.message ?: "ChatScreen.onDeleteMessage")
                            }
                        }
                    )
                }
            }
        }
    }
}
