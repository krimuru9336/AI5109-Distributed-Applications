package com.example.module_3_chitchat.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.module_3_chitchat.Constants
import com.example.module_3_chitchat.view.Appbar
import com.example.module_3_chitchat.view.Info
import com.example.module_3_chitchat.view.SingleMessage
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeView(
    homeViewModel: HomeViewModel = viewModel(), back: () -> Unit
) {
    val message: String by homeViewModel.message.observeAsState(initial = "")
    val messages: List<Map<String, Any>> by homeViewModel.messages.observeAsState(
        initial = emptyList<Map<String, Any>>().toMutableList()
    )

    var editMessageDialogState by remember { mutableStateOf(false) }
    var editedMessage by remember { mutableStateOf("") }
    var messageIdToEdit by remember { mutableStateOf("") }

    fun showEditDialog(messageId: String, currentMessage: String, isCurrentUser: Boolean) {
        if (isCurrentUser) {
            editedMessage = currentMessage
            editMessageDialogState = true
            messageIdToEdit = messageId
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Appbar(title = "ChitChat", action = back, icon = Icons.Default.Close, onAction = {
            FirebaseAuth.getInstance().signOut()
        })

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 0.85f, fill = true),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            reverseLayout = true
        ) {
            items(messages) { messageItem ->
                val isCurrentUser = messageItem[Constants.IS_CURRENT_USER] as Boolean

                SingleMessage(messageData = messageItem,
                    isCurrentUser = isCurrentUser,
                    onHold = { messageIdToEdit ->
                        showEditDialog(messageIdToEdit,
                            messages.find { it[Constants.MESSAGE_ID] == messageIdToEdit }
                                ?.get(Constants.MESSAGE).toString(),
                            isCurrentUser)
                    })
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val keyboardController = LocalSoftwareKeyboardController.current

            if (!editMessageDialogState) {
                TextField(
                    value = message,
                    onValueChange = { updatedMessage: String ->
                        homeViewModel.updateMessage(updatedMessage)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .padding(4.dp)
                        .background(
                            color = Color.Transparent,
                        )
                        .clip(RoundedCornerShape(32.dp)),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    placeholder = {
                        Text(
                            "Enter some text...",
                            style = LocalTextStyle.current.copy(fontSize = 16.sp)
                        )
                    },
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )

                RoundedIconButton(
                    onClick = {
                        // Handle attachment button click
                    },
                    icon = Icons.Default.Add,
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    iconColor = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "Attachment Button"
                )

                Spacer(modifier = Modifier.width(4.dp))

                RoundedIconButton(
                    onClick = {
                        homeViewModel.addMessage()
                        keyboardController?.hide()
                    },
                    icon = Icons.Default.Send,
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    iconColor = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "Send Button"
                )
            } else {
                var isConfirmButtonVisible = editedMessage.isNotBlank()

                TextField(value = editedMessage,
                    onValueChange = { messageId: String ->
                        editedMessage = messageId
                        isConfirmButtonVisible = messageId.isNotBlank()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .padding(4.dp)
                        .background(
                            color = Color.Transparent,
                        )
                        .clip(RoundedCornerShape(32.dp)),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    placeholder = {
                        Text(
                            text = "Delete your message",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = LocalTextStyle.current.copy(fontSize = 16.sp)
                        )
                    })

                RoundedIconButton(
                    onClick = {
                        homeViewModel.deleteMessage(messageIdToEdit)
                        editMessageDialogState = false
                    },
                    icon = Icons.Default.Delete,
                    backgroundColor = Color(0xFFD32F2F),
                    iconColor = Color.White,
                    contentDescription = "Delete"
                )

                Spacer(modifier = Modifier.width(4.dp))

                if (isConfirmButtonVisible) {
                    RoundedIconButton(
                        onClick = {
                            homeViewModel.updateMessage(messageIdToEdit, editedMessage)
                            editMessageDialogState = false
                        },
                        icon = Icons.Default.Check,
                        backgroundColor = Color(0xFF00C853),
                        iconColor = Color.White,
                        contentDescription = "Check"
                    )
                }
            }
        }
        Info()
    }
}

@Composable
fun RoundedIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    contentDescription: String
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() }, contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon, contentDescription = contentDescription, tint = iconColor
        )
    }
}

