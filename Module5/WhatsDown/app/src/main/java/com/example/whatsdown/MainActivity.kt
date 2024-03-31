package com.example.whatsdown

import android.os.Bundle
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.whatsdown.ui.AppViewModel
import com.example.whatsdown.ui.BottomBar
import com.example.whatsdown.ui.Destination
import com.example.whatsdown.ui.TopBar
import com.example.whatsdown.ui.screens.AuthScreen
import com.example.whatsdown.ui.screens.ChatScreen
import com.example.whatsdown.ui.screens.FriendsScreen
import com.example.whatsdown.ui.screens.GroupChatScreen
import com.example.whatsdown.ui.screens.GroupsScreen
import com.example.whatsdown.ui.screens.ImageScreen
import com.example.whatsdown.ui.screens.VideoScreen
import com.example.whatsdown.ui.theme.WhatsDownTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { WhatsDownTheme { AppUI() } }
    }
}

@Composable
fun AppUI(
    modifier: Modifier = Modifier,
    vm: AppViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val appState by vm.appState.collectAsState()
    val signedIn = appState.user != null

    val credentialManager = CredentialManager.create(context)
    val googleIdOption = GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false)
        .setServerClientId(stringResource(R.string.server_client_id)).build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption).build()

    fun showSnackbar(message: String) {
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    fun signIn() {
        scope.launch {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            vm.signIn(
                googleIdTokenCredential.displayName!!,
                googleIdTokenCredential.profilePictureUri.toString()
            ) {
                navController.navigate(Destination.Friends.name) {
                    popUpTo(Destination.Auth.name) { inclusive = true }
                }
                showSnackbar("Welcome ${googleIdTokenCredential.displayName} in WhatsDown")
            }
        }
    }

    fun signOut() {
        scope.launch { credentialManager.clearCredentialState(ClearCredentialStateRequest()) }
        vm.signOut()
        navController.navigate(Destination.Auth.name) {
            popUpTo(Destination.Friends.name) { inclusive = true }
        }
        showSnackbar("Signed Out Successfully")
    }

    Scaffold(
        topBar = {
            TopBar(
                title = when (currentRoute) {
                    Destination.Friends.name -> "Friends"
                    Destination.Chat.name -> appState.currentFriend ?: "Loading..."
                    Destination.Groups.name -> "Groups"
                    Destination.GroupChat.name -> appState.currentGroup ?: "Loading..."
                    else -> stringResource(R.string.app_name)
                },
                currentRoute = currentRoute,
                enabled = signedIn,
                onSignOut = ::signOut,
                onAddNewFriend = {
                    vm.addNewFriend(it)
                    showSnackbar("New Friend Added Successfully")
                },
                onCreateNewGroup = {
                    vm.createGroup(it)
                    showSnackbar("New Group Created Successfully")
                }
            )
        },
        bottomBar = {
            BottomBar(
                enabled = signedIn,
                selectedIndex = when (currentRoute) {
                    Destination.Friends.name, Destination.Chat.name -> 0
                    Destination.Groups.name -> 1
                    else -> -1
                },
                onChats = {
                    if (currentRoute != Destination.Friends.name)
                        navController.navigate(Destination.Friends.name) {
                            popUpTo(Destination.Friends.name) { inclusive = true }
                        }
                },
                onGroups = {
                    if (currentRoute != Destination.Groups.name)
                        navController.navigate(Destination.Groups.name)
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Auth.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = Destination.Auth.name) {
                AuthScreen(onSignIn = ::signIn)
            }

            composable(route = Destination.Friends.name) {
                FriendsScreen(
                    friends = appState.friends,
                    onUnfriend = { vm.unfriend(it) },
                    onStartChat = {
                        vm.startChat(it)
                        navController.navigate(Destination.Chat.name)
                    }
                )
            }

            composable(route = Destination.Chat.name) {
                ChatScreen(
                    user = appState.user?.displayName ?: "Loading...",
                    messages = appState.messages,
                    onSendMessage = { vm.sendMessage(message = it, type = "txt", null) },
                    onEditMessage = { messageId, message ->
                        vm.editMessage(messageId = messageId, message = message)
                    },
                    onDeleteMessage = { vm.deleteMessage(messageId = it) },
                    onSendFile = {
                        val cR = context.contentResolver
                        val mime = MimeTypeMap.getSingleton()
                        val type = mime.getExtensionFromMimeType(cR.getType(it))
                        vm.sendFile(it, type!!, null)
                        showSnackbar("Sending File...")
                    },
                    onOpenImage = { navController.navigate("${Destination.Image.name}?imageLink=$it") },
                    onOpenVideo = { navController.navigate("${Destination.Video.name}?videoLink=$it") }
                )
            }

            composable(
                route = "${Destination.Image.name}?imageLink={imageLink}",
                arguments = listOf(navArgument("imageLink") { type = NavType.StringType })
            ) {
                ImageScreen(link = it.arguments?.getString("imageLink")!!)
            }

            composable(
                route = "${Destination.Video.name}?videoLink={videoLink}",
                arguments = listOf(navArgument("videoLink") { type = NavType.StringType })
            ) {
                VideoScreen(
                    link = it.arguments?.getString("videoLink")!!,
                    player = vm.player
                )
            }

            composable(route = Destination.Groups.name) {
                GroupsScreen(
                    groups = appState.groups,
                    onLeaveGroup = { vm.leaveGroup(it) },
                    onStartChat = {
                        vm.startGroupChat(it)
                        navController.navigate(Destination.GroupChat.name)
                    }
                )
            }

            composable(route = Destination.GroupChat.name) {
                GroupChatScreen(
                    user = appState.user?.displayName ?: "Loading...",
                    messages = appState.messages,
                    onSendMessage = {
                        vm.sendMessage(
                            message = it,
                            type = "txt",
                            appState.currentGroup
                        )
                    },
                    onEditMessage = { messageId, message ->
                        vm.editMessage(messageId = messageId, message = message)
                    },
                    onDeleteMessage = { vm.deleteMessage(messageId = it) },
                    onSendFile = {
                        val cR = context.contentResolver
                        val mime = MimeTypeMap.getSingleton()
                        val type = mime.getExtensionFromMimeType(cR.getType(it))
                        vm.sendFile(it, type!!, appState.currentGroup)
                        showSnackbar("Sending File...")
                    },
                    onOpenImage = { navController.navigate("${Destination.Image.name}?imageLink=$it") },
                    onOpenVideo = { navController.navigate("${Destination.Video.name}?videoLink=$it") }
                )
            }
        }
    }
}
