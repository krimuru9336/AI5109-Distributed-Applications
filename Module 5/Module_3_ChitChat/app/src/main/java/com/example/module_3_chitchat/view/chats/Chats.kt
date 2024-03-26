package com.example.module_3_chitchat.view.chats


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.example.module_3_chitchat.view.Appbar
import com.example.module_3_chitchat.view.Info
import com.example.module_3_chitchat.view.RoundedIconButton

@Composable
fun ChatsView(
    navController: NavHostController, chatsViewModel: ChatsViewModel = viewModel(), back: () -> Unit
) {
    val chats by chatsViewModel.chats.observeAsState(initial = emptyList())
    var chatName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Appbar(title = "Chats",
            action = back,
            icon = Icons.Default.Close,
            onAction = { FirebaseAuth.getInstance().signOut() })

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = chatName,
                onValueChange = { chatName = it },
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp)
                    .clip(RoundedCornerShape(32.dp)),
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                placeholder = {
                    Text(
                        "Add a chat or @invite",
                        style = LocalTextStyle.current.copy(fontSize = 16.sp)
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
            )

            Spacer(modifier = Modifier.width(8.dp))

            RoundedIconButton(
                onClick = {
                    if (chatName.isNotEmpty()) {
                        if (chatName.startsWith("@")) {
                            chatsViewModel.joinChat(chatName.substringAfter("@"))
                        } else {
                            chatsViewModel.createChat(chatName)
                        }
                        chatName = ""
                    }
                },
                icon = Icons.Default.Add,
                backgroundColor = MaterialTheme.colorScheme.primary,
                iconColor = MaterialTheme.colorScheme.onPrimary,
                contentDescription = "Delete"
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(chats) { chat ->
                ChatListItem(navController = navController,
                    chat = chat,
                    viewModel = chatsViewModel,
                    onStartEditing = {},
                    onStopEditing = {})
            }
        }
        Info()
    }
}

@Composable
fun ChatListItem(
    navController: NavHostController,
    chat: Chat,
    viewModel: ChatsViewModel,
    onStartEditing: () -> Unit,
    onStopEditing: () -> Unit
) {
    var showEditMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Text(text = chat.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .clickable {
                        navController.navigate("home/${chat.id}/${chat.name}")
                    })

            if (!showEditMenu) {
                Text(text = "@${chat.inviteCode}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .clickable { showEditMenu = true }
                        .padding(end = 8.dp))
            }
        }

        if (showEditMenu) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                RoundedIconButton(
                    onClick = {
                        viewModel.deleteChat(chat.id)
                        showEditMenu = false
                        onStopEditing()
                    },
                    icon = Icons.Default.Delete,
                    backgroundColor = Color(0xFFB94343),
                    iconColor = Color.White,
                    contentDescription = "Delete"
                )

                Spacer(Modifier.width(8.dp))

                RoundedIconButton(
                    onClick = {
                        onStartEditing()
                        showEditMenu = !showEditMenu
                    },
                    icon = Icons.Default.Close,
                    backgroundColor = Color(0xFF61B943),
                    iconColor = Color.White,
                    contentDescription = "Edit"
                )
            }
        }
    }
}