/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

@file:OptIn(ExperimentalMaterial3Api::class)

package com.felixstumpf.chatapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.felixstumpf.chatapp.R
import com.felixstumpf.chatapp.viewmodels.ChatOverviewViewModel
import com.felixstumpf.chatapp.viewmodels.ChatViewModel
import kotlinx.coroutines.launch

// Scaffold to organize the entire chat overview screen
@Composable
fun ChatOverviewScaffold(
    navController: NavController,
    chatViewModel: ChatViewModel,
    chatOverviewViewModel: ChatOverviewViewModel
) {
    val route = remember { mutableStateOf(Routes.CHATS) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val showBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Divider()
                Text(
                    stringResource(id = R.string.app_menu),
                    modifier = Modifier.padding(16.dp)
                )
                DevInfoForLecturer()
                Divider()

                NavigationDrawerItem(
                    label = {
                        Text(stringResource(id = R.string.chats))
                    },
                    selected = false,
                    onClick = {
                        route.value = Routes.CHATS
                        scope.launch { drawerState.close() }
                    },
                    icon = {
                        Icon(
                            Icons.Outlined.Favorite,
                            contentDescription = stringResource(id = R.string.chats)
                        )
                    }
                )

                NavigationDrawerItem(
                    label = {
                        Text(stringResource(id = R.string.changeUser))
                    },
                    selected = false,
                    onClick = {
                        route.value = Routes.USER
                        scope.launch { drawerState.close() }
                    },
                    icon = {
                        Icon(
                            Icons.Outlined.AccountBox,
                            contentDescription = stringResource(id = R.string.changeUser)
                        )
                    }
                )


            }
        },
        gesturesEnabled = true
    ) {


        Scaffold(
            topBar = {
                TopAppBar(

                    title = {
                        Column {
                            Text(stringResource(id = R.string.app_name))
                            Text(
                                stringResource(id = R.string.currentUser) + ": " + chatViewModel.currentUser.value?.email!!,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                    }

                )
            },

            floatingActionButton = {
                FloatingActionButton(onClick = {
                    scope.launch {
                        showBottomSheet.value = true
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                when (route.value) {
                    Routes.CHATS -> ChatOverview(
                        navController = navController,
                        chatOverviewViewModel = chatOverviewViewModel
                    )

                    Routes.USER -> navController.navigate("start")

                }
                if (showBottomSheet.value) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet.value = false },
                        sheetState = sheetState
                    ) {
                        AddChatDialog(navController, chatViewModel)
                    }

                }

            }
        }

    }
}

// Routes companion object for managing navigation drawer
object Routes {
    const val CHATS = "Chats"
    const val USER = "User"
}


