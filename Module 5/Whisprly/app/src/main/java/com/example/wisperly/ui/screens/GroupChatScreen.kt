package com.example.wisperly.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import com.example.wisperly.ui.Message
import com.example.wisperly.ui.dialogs.EditMessageDialog
import com.example.wisperly.ui.theme.WhatsDownTheme
import com.google.firebase.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupChatScreen(
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

    Column(
        modifier = modifier.fillMaxHeight()
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
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrentUserSender) Color(0xFF474648) else Color(0xFFc2c6d6)
                        ),
                        shape = RoundedCornerShape(15.dp),
                        modifier = modifier
                            .fillMaxWidth(0.7f)
                            .wrapContentWidth(
                                if (isCurrentUserSender) Alignment.End
                                else Alignment.Start
                            )
                            .width(IntrinsicSize.Max)
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
                        if (!isCurrentUserSender)
                            Text(
                                text = message.sender,

                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                color = if (isCurrentUserSender) Color(0xFFF5F5DC) else Color(0xFF383838)

                            )
                        when (message.type) {
                            "txt" -> {
                                Text(
                                    text = message.message,
                                    modifier = modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                                    lineHeight = 16.sp,
                                    color = if (isCurrentUserSender) Color(0xFFF5F5DC) else Color(0xFF383838)
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
                            text = SimpleDateFormat("hh:mm a | MMM dd", Locale.getDefault())
                                .format(message.timeStamp.toDate()),
                            modifier = modifier
                                .align(Alignment.End)
                                .padding(horizontal = 15.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                color = if (isCurrentUserSender) Color(0xFFF5F5DC) else Color(0xFF383838)
                            )
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

            IconButton(
                onClick = { launcher.launch(PickVisualMediaRequest()) },
                modifier = modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddCircleOutline,
                    contentDescription = "Select Media",
                    modifier = Modifier.size(40.dp)
                )
            }

            OutlinedTextField(
                shape = RoundedCornerShape(36.dp),
                value = message,
                onValueChange = { message = it },
                modifier = modifier.weight(weight = 1f).height(60.dp),
                label = { Text(text = "Type your message") }
            )
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
                    modifier = Modifier.size(40.dp)
                )
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
