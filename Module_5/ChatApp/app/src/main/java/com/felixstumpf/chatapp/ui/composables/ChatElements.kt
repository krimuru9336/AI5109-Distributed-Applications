/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

@file:OptIn(ExperimentalMaterial3Api::class)

package com.felixstumpf.chatapp.ui.composables

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import com.felixstumpf.chatapp.R
import com.felixstumpf.chatapp.models.MessageEntity
import com.felixstumpf.chatapp.models.MessageFeed
import com.felixstumpf.chatapp.ui.theme.dark_CustomColor1
import com.felixstumpf.chatapp.ui.theme.dark_CustomColor2
import com.felixstumpf.chatapp.viewmodels.ChatViewModel

// Display element that shows a scrollable lazylist of MessageBubbles from ChatViewModel MessageFeed LiveData
@Composable
fun MessageFeedDisplay(chatViewModel: ChatViewModel) {
    val messageFeed by chatViewModel.messageFeed.observeAsState(MessageFeed(listOf(), listOf()))

    val lazyListState = rememberLazyListState()

    LaunchedEffect(messageFeed.messages) {

        if (messageFeed.messages.isNotEmpty()) {
            // Scroll to the latest item when the message list changes
            lazyListState.scrollToItem(messageFeed.messages.size - 1)
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize(),
    ) {
        itemsIndexed(messageFeed.messages) { index, message ->
            MessageBubble(message = message, chatViewModel)

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// Display element that shows a MessageBubble
// On click, it opens a ModalBottomSheet to edit or delete the message
// If the message contains an image or GIF, it will be displayed with Coil
// If the message contains a video, it will be displayed with ExoPlayer
@androidx.annotation.OptIn(UnstableApi::class)
@ExperimentalCoilApi
@Composable
fun MessageBubble(message: MessageEntity, chatViewModel: ChatViewModel) {
    val context = LocalContext.current
    val isFromCurrentUser by remember { mutableStateOf(message.senderEmail == chatViewModel.currentUser.value?.email) }
    val selectedMessage by chatViewModel.selectedMessage.observeAsState()
    val modalBottomSheetVisible = remember { mutableStateOf(false) }
    val imgLoader =
        ImageLoader.Builder(LocalContext.current).components { add(GifDecoder.Factory()) }
            .build()

    val mediaItem = MediaItem.Builder().setUri(message.mediaUrl).build()
    val exoPlayer = remember(context, mediaItem) {
        ExoPlayer.Builder(context).build().also { exoPlayer ->
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = false
            exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_OFF
            exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT

        }
    }

    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    topStart = 48f,
                    topEnd = 48f,
                    bottomStart = if (isFromCurrentUser) 48f else 0f,
                    bottomEnd = if (isFromCurrentUser) 0f else 48f
                )
            )
            .background(if (isFromCurrentUser) dark_CustomColor1 else dark_CustomColor2)
            .padding(16.dp)
            .fillMaxWidth()

    ) {
        Column {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clickable {
                        chatViewModel.setSelectedMessage(message)
                        modalBottomSheetVisible.value = true
                    }

            ) {
                Text(text = message.senderEmail, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    modifier = Modifier.align(Alignment.End),
                    text = message.timestamp.toDate().toString(),
                    style = MaterialTheme.typography.labelSmall

                )
                Divider(color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (message.mediaType != null) {
                if (message.mediaType.startsWith("image")) {
                    Image(

                        painter = rememberAsyncImagePainter(
                            model = message.mediaUrl,
                            imageLoader = imgLoader
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp), // Set an appropriate height
                        contentScale = ContentScale.Crop // Adjust content scale as needed
                    )
                } else if (message.mediaType.startsWith("video")) {
                    DisposableEffect(
                        AndroidView(factory = {
                            PlayerView(context).apply {
                                player = exoPlayer


                            }
                        }, Modifier.height(300.dp))
                    ) {
                        onDispose {
                            //exoPlayer.release() //commented because it should not be released
                        }
                    }
                }

            }


            Text(text = message.message)

        }
        if (modalBottomSheetVisible.value && isFromCurrentUser) {

            // ModalBottomSheet for editing and deleting messages:

            ModalBottomSheet(onDismissRequest = { modalBottomSheetVisible.value = false }) {
                Text(
                    text = stringResource(id = R.string.message_menu),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        chatViewModel.deleteMessage()
                    }
                ) {
                    Text(text = stringResource(id = R.string.delete_message))

                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                ) {
                    TextField(
                        modifier = Modifier.height(120.dp),
                        value = selectedMessage!!.message,
                        onValueChange = {
                            chatViewModel.setSelectedMessage(
                                selectedMessage!!.copy(
                                    message = it
                                )
                            )
                        },
                        label = { Text(stringResource(id = R.string.edit_message)) })
                    Button(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .defaultMinSize(minWidth = 12.dp)
                            .fillMaxWidth()
                            .padding(8.dp),


                        onClick = {
                            chatViewModel.updateMessage()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.edit))

                    }
                }


                Spacer(modifier = Modifier.height(96.dp))

            }
        }
    }
}

// Message input field element for Bottom Bar to write messages, pick media and to send it
@Composable
fun MessageInputField(chatViewModel: ChatViewModel) {
    val message = remember { mutableStateOf("") }
    val context = LocalContext.current
    val pickedImage = chatViewModel.mediaToSendUri.observeAsState()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            chatViewModel.setMediaToSendUri(it)
        }

    if (pickedImage.value != null) {
        context.contentResolver.openInputStream(pickedImage.value!!)?.use { input ->
            input.readBytes().let {
                chatViewModel.setMediaAsByteArray(it)
            }
        }
    }

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = message.value,
            onValueChange = {
                message.value = it
            },
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface),
            label = { Text(stringResource(id = R.string.message)) })
        Button(
            onClick = {
                if (pickedImage.value != null) {
                    chatViewModel.setMediaToSendUri(null)
                    chatViewModel.setMediaAsByteArray(null)
                } else {
                    launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo))


                }


            },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.inversePrimary),
                containerColor = MaterialTheme.colorScheme.inversePrimary
            ),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .padding(start = 8.dp)
        ) {
            Icon(
                if (pickedImage.value != null)
                    Icons.Outlined.Delete
                else
                    Icons.Outlined.Share,

                contentDescription = stringResource(id = R.string.send),


                )
        }
        Button(
            onClick = {
                chatViewModel.setMessageToSendByText(
                    message.value
                )
                chatViewModel.sendMessage()
                message.value = ""


            },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.tertiaryContainer),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .padding(start = 8.dp)
        )

        {
            Icon(
                Icons.Outlined.KeyboardArrowRight,
                contentDescription = stringResource(id = R.string.send)
            )
        }


    }
}