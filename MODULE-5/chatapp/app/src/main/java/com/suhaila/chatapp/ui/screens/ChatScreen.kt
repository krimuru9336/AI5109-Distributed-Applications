package com.suhaila.chatapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import com.suhaila.chatapp.ui.Message
import com.suhaila.chatapp.ui.dialogs.EditMessageDialog
import com.google.firebase.Timestamp
import java.text.DateFormat
import com.suhaila.chatapp.R.*
import com.suhaila.chatapp.ui.theme.ChatAppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    user: String,
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    onEditMessage: (String, String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onSendFile: (Uri) -> Unit,
    onOpenImage: (String) -> Unit,
    onOpenVideo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf("") }
    var messageToEdit by remember { mutableStateOf<Message?>(null) }
    var openEditMessageDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Background Image
        Image(
            painter = painterResource(id = drawable.img),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            LazyColumn(
                modifier = modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
            ) {
                items(messages) { message ->
                    var expanded by remember { mutableStateOf(false) }
                    val isCurrentUserSender = message.sender == user

                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
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
                                    onClick = {
                                        when (message.type) {
                                            "gif", "jpg" -> onOpenImage(message.message)
                                            "mp4" -> onOpenVideo(message.message)
                                        }
                                    },
                                )
                        ) {
                            when (message.type) {
                                "txt" -> {
                                    Text(
                                        text = message.message,
                                        modifier = modifier.padding(horizontal = 4.dp),
                                        lineHeight = 16.sp,
                                        style = TextStyle(color = Color.White)
                                    )
                                }

                                "gif", "jpg" -> {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(message.message)
                                            .decoderFactory(GifDecoder.Factory())
                                            .build(),
                                        contentDescription = null
                                    )
                                }

                                "mp4" -> {
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(message.message)
                                                .decoderFactory(VideoFrameDecoder.Factory())
                                                .build(),
                                            contentDescription = null
                                        )
                                        Icon(
                                            imageVector = Icons.Outlined.PlayArrow,
                                            contentDescription = null,
                                            modifier = modifier.size(64.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = DateFormat.getDateTimeInstance()
                                    .format(message.timeStamp.toDate()),
                                modifier = modifier
                                    .align(Alignment.End)
                                    .padding(horizontal = 4.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                            if (isCurrentUserSender) {
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }) {
                                    if (message.type == "txt") {
                                        DropdownMenuItem(
                                            text = { Text(text = "Edit") },
                                            onClick = {
                                                expanded = false
                                                messageToEdit = message
                                                openEditMessageDialog = true
                                            }
                                        )
                                    }
                                    DropdownMenuItem(
                                        text = { Text(text = "Delete") },
                                        onClick = {
                                            expanded = false
                                            onDeleteMessage(message.id)
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
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia()
                ) { if (it != null) onSendFile(it) }

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = modifier.weight(weight = 1f),
                    label = { Text(text = "Write your message here") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black
                    )
                )
                IconButton(
                    onClick = { launcher.launch(PickVisualMediaRequest()) },
                    modifier = modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Select Media",
                        tint = Color.Black
                    )
                }
                IconButton(
                    onClick = {
                        onSendMessage(message)
                        message = ""
                    },
                    modifier = modifier.padding(top = 8.dp),
                    enabled = message.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = "Send Message",
                        tint = Color.Black
                    )
                }
            }
        }
    }

    if (openEditMessageDialog) {
        EditMessageDialog(
            currentMessage = messageToEdit!!.message,
            onDismissRequest = { openEditMessageDialog = false },
            onConfirm = {
                openEditMessageDialog = false
                onEditMessage(messageToEdit!!.id, it)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatPreview() {
    ChatAppTheme {
        ChatScreen(
            user = "Suhaila",
            messages = listOf(
                Message(
                    id = "dsf",
                    sender = "Suhaila",
                    receiver = "Nizam",
                    message = "Long message to test. even long message to test width of the element according to display width",
                    type = "txt",
                    timeStamp = Timestamp(1711699999, 0)
                ),
                Message(
                    id = "dsf",
                    sender = "Nizam",
                    receiver = "Suhaila",
                    message = "another long message to test",
                    type = "txt",
                    timeStamp = Timestamp(1711699999, 0)
                ),
                Message(
                    id = "dsf",
                    sender = "Nizam",
                    receiver = "Suhaila",
                    message = "bye",
                    type = "txt",
                    timeStamp = Timestamp(1711699999, 0)
                ),
                Message(
                    id = "dsf",
                    sender = "Suhaila",
                    receiver = "Nizam",
                    message = "https://firebasestorage.googleapis.com/v0/b/whatsdown-f2c47.appspot.com/o/1000051524?alt=media&token=f7abc9d0-12c5-4f92-97ae-c77cfaaca21b",
                    type = "gif",
                    timeStamp = Timestamp(1711699999, 0)
                ),
            ),
            onSendMessage = {},
            onEditMessage = { a, b -> listOf(a, b) },
            onDeleteMessage = {},
            onSendFile = {},
            onOpenImage = {},
            onOpenVideo = {}
        )
    }
}
