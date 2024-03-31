package com.suhaila.chatapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.suhaila.chatapp.R
import com.suhaila.chatapp.ui.dialogs.AddNewFriendDialog
import com.suhaila.chatapp.ui.dialogs.CreateNewGroupDialog
import com.suhaila.chatapp.ui.theme.ChatAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    currentRoute: String?,
    enabled: Boolean,
    onSignOut: () -> Unit,
    onAddNewFriend: (String) -> Unit,
    onCreateNewGroup: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var openAddNewFriendDialog by remember { mutableStateOf(false) }
    var openCreateNewGroupDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(title) },
        actions = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More Options",
                    tint = Color.Black
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                when (currentRoute) {
                    Destination.Friends.name -> {
                        DropdownMenuItem(
                            text = { Text(text = "Add New Friend") },
                            onClick = {
                                expanded = false
                                openAddNewFriendDialog = true
                            }
                        )
                    }

                    Destination.Groups.name -> {
                        DropdownMenuItem(
                            text = { Text(text = "Create New Group") },
                            onClick = {
                                expanded = false
                                openCreateNewGroupDialog = true
                            }
                        )
                    }
                }
                DropdownMenuItem(
                    text = { Text(text = "Sign Out") },
                    onClick = {
                        expanded = false
                        onSignOut()
                    },
                    enabled = enabled
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.Black,
            actionIconContentColor = MaterialTheme.colorScheme.primaryContainer
        ),
    )

    if (openAddNewFriendDialog) {
        AddNewFriendDialog(
            onDismissRequest = { openAddNewFriendDialog = false },
            onConfirm = {
                openAddNewFriendDialog = false
                onAddNewFriend(it)
            }
        )
    }

    if (openCreateNewGroupDialog) {
        CreateNewGroupDialog(
            onDismissRequest = { openCreateNewGroupDialog = false },
            onConfirm = {
                openCreateNewGroupDialog = false
                onCreateNewGroup(it)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    ChatAppTheme {
        TopBar(
            title = stringResource(id = R.string.app_name),
            currentRoute = Destination.Auth.name,
            enabled = false,
            onSignOut = {},
            onAddNewFriend = {},
            onCreateNewGroup = {}
        )
    }
}