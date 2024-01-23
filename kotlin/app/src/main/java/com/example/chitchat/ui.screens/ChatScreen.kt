package com.example.chitchat.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chitchat.data.ChatMessage
import com.example.chitchat.data.FirebaseViewModel
import com.example.chitchat.data.UserViewModel
import com.example.chitchat.ui.composables.MainAppBar
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    roomId: String,
    firebaseViewModel: FirebaseViewModel,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var messageInput by remember { mutableStateOf("") }
    val chatMessages by firebaseViewModel.chatMessages.observeAsState(emptyList())


    LaunchedEffect(Unit) {
        firebaseViewModel.getChats(roomId)
    }


    fun onSendClick(): Unit {
        print("onSendClick")
        Log.d("SUBMIT", messageInput)
        if (userViewModel.userObj != null) {
            val message = ChatMessage(
                id = "",
                text = messageInput,
                sender = userViewModel.userObj!!.id,
                timestamp =  System.currentTimeMillis()
            )
            messageInput = ""
            Log.d("message", message.toString())
            firebaseViewModel.sendChatMessage(
                roomId = roomId,
                message = message,
            )
        }
    }

    fun onMessageChange(message: String) {
        Log.d("Input", message)
        messageInput = message

    }

//    Scaffold(
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
//        topBar = {
//            MainAppBar(
//                title = "Chat " + roomId,
//                scrollBehavior = scrollBehavior,
//                navController,
//            )
//        },
//    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            MainAppBar(
                title = "Azamat Afzalov - 1492864",
                scrollBehavior = scrollBehavior,
                navController,
            )
            LazyColumn(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
//                    .padding(innerPadding)
                    .weight(1f)
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                items(chatMessages){message ->
                    Message(message = message, userId = userViewModel.userId)
                }
            }
            MessageInput(
                message = messageInput,
                onMessageChange = {message ->
                    onMessageChange(message)
                },
                onSendClick = {
                    onSendClick()
                },
            ) {}
        }
//    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MessageInput(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    function: () -> Unit,
    ) {
    var keyboardController by remember {
        mutableStateOf<SoftwareKeyboardController?>(null)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(8.dp)
            .height(56.dp)
    ) {
        // Message Input
        OutlinedTextField(
            value = message,
            onValueChange = { onMessageChange(it) },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    onSendClick()
                }
            ),
            label = null
        )

        // Send Button
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.CenterVertically)
        ) {
            IconButton(
                onClick = {
                    if (message.isNotBlank()) {
                        onSendClick()
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxHeight()

            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun Message(message: ChatMessage, userId: String?) {
    fun convertTimestamp(timestampMillis: Long): String {
        val date = Date(timestampMillis)
        val sdf = SimpleDateFormat("HH:mm")
        return sdf.format(date)
    }
    if (userId == message.sender) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text = convertTimestamp(message.timestamp),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp),
                fontSize = 12.sp,
            )
            Card(
                shape = RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 8.dp,
                    bottomStart = 8.dp,
                    bottomEnd = 0.dp
                ),
                modifier = Modifier
//                    .weight(0.7f),

            ) {
                Box(
                    modifier = Modifier.align(Alignment.End),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = message.text,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(
                                vertical = 8.dp,
                                horizontal = 16.dp
                            )
                    )
                }

            }

        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 8.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 8.dp
                ),
                modifier = Modifier,
            ) {
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = message.text,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(
                                vertical = 8.dp,
                                horizontal = 16.dp
                            )

                    )
                }
            }
            Text(
                text = convertTimestamp(message.timestamp),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp),
                fontSize = 12.sp,
            )
        }
    }
    Box(
        modifier = Modifier.height(8.dp)
    ) {}
}