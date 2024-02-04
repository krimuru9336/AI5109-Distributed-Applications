package com.suhaila.chatapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suhaila.chatapp.ui.Chat
import com.suhaila.chatapp.ui.dialogs.DeleteMessageDialog
import com.suhaila.chatapp.ui.dialogs.EditMessageDialog
import com.suhaila.chatapp.ui.theme.ChatAppTheme
import com.suhaila.chatapp.R
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    currentUser: String,
    messages: List<Chat>,
    onSendMessage: (String) -> Unit,
    onEditMessage: (String, String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf("") }
    var chatToEditOrDelete by remember { mutableStateOf(Chat()) }
    var openEditMessageDialog by remember { mutableStateOf(false) }
    var openDeleteMessageDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.`img`),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
        Column(
            modifier = modifier.fillMaxHeight()
        ) {
            LazyColumn(
                modifier = modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
            ) {
                items(messages) { chat ->
                    var expanded by remember { mutableStateOf(false) }
                    val isCurrentUserSender = chat.sender == currentUser

                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        contentAlignment = if (isCurrentUserSender) Alignment.CenterEnd
                        else Alignment.CenterStart
                    ) {
                        Card(
                            modifier = modifier
                                .fillMaxWidth(0.7f)
                                .wrapContentWidth(
                                    if (isCurrentUserSender) Alignment.End
                                    else Alignment.Start
                                )
                                .combinedClickable(
                                    onLongClick = { if (isCurrentUserSender) expanded = true },
                                    onClick = {},
                                )
                        ) {
                            Text(
                                text = SimpleDateFormat
                                    .getDateTimeInstance()
                                    .format(Date(chat.timeStamp)),
                                modifier = modifier
                                    .align(if (isCurrentUserSender) Alignment.End else Alignment.Start)
                                    .padding(horizontal = 4.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = chat.message,
                                modifier = modifier.padding(
                                    start = 4.dp,
                                    end = 4.dp,
                                    bottom = 4.dp
                                ),
                                textAlign = TextAlign.Justify,
                                lineHeight = 20.sp
                            )
                            if (isCurrentUserSender) {
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }) {
                                    DropdownMenuItem(
                                        text = { Text(text = "Edit") },
                                        onClick = {
                                            chatToEditOrDelete = chat
                                            openEditMessageDialog = true
                                            expanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(text = "Delete") },
                                        onClick = {
                                            chatToEditOrDelete = chat
                                            openDeleteMessageDialog = true
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Row(
                modifier = modifier.padding(start = 4.dp, end = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = modifier.weight(weight = 1f),
                    label = { Text(text = "Write your message here") }
                )
                Button(
                    onClick = {
                        onSendMessage(message)
                        message = ""
                    },
                    modifier = modifier.padding(start = 4.dp, top = 8.dp),
                    enabled = message.isNotEmpty()
                ) {
                    Text(text = "Send")
                }
            }
        }

        if (openEditMessageDialog) {
            EditMessageDialog(
                currentMessage = chatToEditOrDelete.message,
                onDismissRequest = { openEditMessageDialog = false },
                onConfirm = {
                    openEditMessageDialog = false
                    onEditMessage(chatToEditOrDelete.id, it)
                }
            )
        }

        if (openDeleteMessageDialog) {
            DeleteMessageDialog(
                onDismissRequest = { openDeleteMessageDialog = false },
                onConfirm = {
                    openDeleteMessageDialog = false
                    onDeleteMessage(chatToEditOrDelete.id)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatPreview() {
    ChatAppTheme {
        ChatScreen(
            currentUser = "Suhaila",
            messages = listOf(
                Chat(
                    id = "dsf",
                    sender = "Suhaila",
                    receiver = "Nizam",
                    message = "Long message to test. even long message to test width of the element according to display width",
                    timeStamp = 1705882035019
                ),
                Chat(
                    id = "dsf",
                    sender = "Nizam",
                    receiver = "Suhaila",
                    message = "another long message to test",
                    timeStamp = 1705862035019
                ),
                Chat(
                    id = "dsf",
                    sender = "Nizam",
                    receiver = "Suhaila",
                    message = "bye",
                    timeStamp = 1705862035019
                ),
                Chat(
                    id = "dsf",
                    sender = "Suhaila",
                    receiver = "Nizam",
                    message = "short",
                    timeStamp = 1705882035019
                ),
            ),
            onSendMessage = {},
            onEditMessage = { a, b -> listOf(a, b) },
            onDeleteMessage = {}
        )
    }
}