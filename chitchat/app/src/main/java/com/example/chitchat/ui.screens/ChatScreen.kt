package com.example.chitchat.ui.screens

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.chitchat.data.ChatMessage
import com.example.chitchat.data.FirebaseViewModel
import com.example.chitchat.data.MediaType
import com.example.chitchat.data.UserViewModel
import com.example.chitchat.ui.composables.MainAppBar
import io.sanghun.compose.video.VideoPlayer
import io.sanghun.compose.video.controller.VideoPlayerControllerConfig
import io.sanghun.compose.video.uri.VideoPlayerMediaItem
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    val chatMessages by firebaseViewModel.chatMessages.observeAsState(emptyList())

    var messageInput by remember { mutableStateOf("") }
    var mediaInput by remember { mutableStateOf("") }
    var mediaType by remember { mutableStateOf<MediaType>(MediaType.Text) }

    var editingMessageId by remember { mutableStateOf("") }
    var isEditMode by remember {
        mutableStateOf(false)
    }

    var scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        firebaseViewModel.getChats(roomId)
        scrollState.scrollTo(-1)
    }

    fun onCancel() {
        isEditMode = false
        messageInput = ""
        editingMessageId = ""
    }

    fun onSendClick(): Unit {
        if (isEditMode && editingMessageId.isNotEmpty()) {
            Log.d("IS EDIT MODE", messageInput)
            firebaseViewModel.editMessage(
                roomId = roomId,
                messageId = editingMessageId,
                newText = messageInput
            )
            onCancel()
            return
        }
        if (userViewModel.userObj != null) {

            val message = ChatMessage(
                id = "",
                text = if (mediaInput.isNotEmpty()) mediaInput else messageInput,
                sender = userViewModel.userObj!!.id,
                timestamp =  System.currentTimeMillis(),
                type = mediaType,
            )

            // Cleanup inputs
            if (mediaType == MediaType.Text) {
                messageInput = ""
            } else {
                mediaInput = ""
                mediaType = MediaType.Text
            }


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

    fun onDeleteMessage(messageId: String) {
        firebaseViewModel.deleteMessage(messageId, roomId)
        Log.d("DELETE MESSAGE", messageId)
    }

    fun onEditMessage(messageId: String, text: String) {
        isEditMode = true
        messageInput = text
        editingMessageId = messageId
        Log.d("EDIT MESSAGE", messageId)
    }

    fun onMediaUpload(uri: Uri, type: MediaType) {

        GlobalScope.launch {
            val path = firebaseViewModel.onMediaUpload(uri, type)
            if (path != null) {
                mediaInput = path
                mediaType = type

            }
            onSendClick()
        }
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            MainAppBar(
                title = "Azamat Afzalov - 1492864",
                scrollBehavior = scrollBehavior,
                navController,
            )
            LazyColumn(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                items(chatMessages){message ->
                    Message(
                        message = message,
                        userId = userViewModel.userId,
                        onDeleteMessage = {
                            onDeleteMessage(it)
                        },
                        onEditMessage = { id, text ->
                            onEditMessage(id, text)
                        },
                    )
                }
                item {

                }
            }
            MessageInput(
                message = messageInput,
                isEditMode = isEditMode,
                onMessageChange = {message ->
                    onMessageChange(message)
                },
                onCancel = {
                    onCancel()
                },
                onSendClick = {
                    onSendClick()
                },
                onMediaUpload = { uri: Uri, type: MediaType ->
                    onMediaUpload(uri, type)
                }
            )
        }
}


@OptIn(ExperimentalComposeUiApi::class, DelicateCoroutinesApi::class)
@Composable
fun MessageInput(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onCancel: () -> Unit,
    onMediaUpload: (uri: Uri, type: MediaType) -> Unit,
    isEditMode: Boolean,
) {
    var context = LocalContext.current

    var keyboardController by remember {
        mutableStateOf<SoftwareKeyboardController?>(null)
    }

    fun detectMediaType(uri: Uri): MediaType {
        val contentResolver: ContentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri)
        mimeType?.let {
            val mediaType = if (it.startsWith("image/gif")) {
                MediaType.Gif
            }
            else if (it.startsWith("image/")) {
                MediaType.Image
            } else if (it.startsWith("video/")) {
                MediaType.Video
            } else {
                MediaType.Text
            }
            return mediaType
            // Handle the detected media type here (e.g., display a toast message)
        }
        return MediaType.Text
    }


    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {uri: Uri? ->
        if (uri != null) {
            onMediaUpload(uri, detectMediaType(uri))
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(8.dp)
            .height(56.dp)
    ) {
        // Media upload button
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.CenterVertically)
        ) {
            IconButton(
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    )

                },
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.secondary)
                    .fillMaxHeight()

            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Media",
                    tint = Color.White,
                    modifier = Modifier
                )
            }
        }

        // Message Input
        OutlinedTextField(
            value = message,
            onValueChange = { onMessageChange(it) },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
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

        if (isEditMode) {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.CenterVertically)
            ) {
                IconButton(
                    onClick = {
                        onCancel()
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.error)
                        .fillMaxHeight()

                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Cancel",
                        tint = Color.White,
                        modifier = Modifier
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

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
                if (isEditMode) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier
                    )
                } else {
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
}

@androidx.annotation.OptIn(UnstableApi::class) @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Message(
    message: ChatMessage,
    userId: String?,
    onDeleteMessage: (messageId: String) -> Unit,
    onEditMessage: (messageId: String, text: String) -> Unit,
) {
    var selectedMessage by rememberSaveable { mutableStateOf<String?>(null)}
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    fun convertTimestamp(timestampMillis: Long): String {
        val date = Date(timestampMillis)
        val sdf = SimpleDateFormat("HH:mm")
        return sdf.format(date)
    }

    fun onDeleteMessage() {
        onDeleteMessage(message.id)
        selectedMessage = null
    }

    fun onEditMessage() {
        onEditMessage(message.id, message.text)
        selectedMessage = null
    }

    if (userId == message.sender) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
            ,
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
                    .combinedClickable(
                        onClick = {

                        },
                        onLongClick = {
                            Log.d("long click", "LONG CLICK")
                            selectedMessage = message.id
                        }
                    )
            ) {
                Box(
                    modifier = Modifier.align(Alignment.End),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if ( message.type == MediaType.Text) {
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
                    } else if (message.type == MediaType.Image) {
                        AsyncImage(
                            model = message.text,
                            contentDescription = null, // Set content description if needed
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .width(300.dp)
                                .height(300.dp)

                        )
                    } else if (message.type == MediaType.Video) {
                        VideoPlayer(
                            modifier = Modifier
                                .width(300.dp)
                                .height(300.dp),
                            autoPlay = false,
                            controllerConfig = VideoPlayerControllerConfig(
                                showSpeedAndPitchOverlay = false,
                                showSubtitleButton = false,
                                showCurrentTimeAndTotalTime = false,
                                showBufferingProgress = false,
                                showForwardIncrementButton = false,
                                showBackwardIncrementButton = false,
                                showBackTrackButton = false,
                                showNextTrackButton = false,
                                showRepeatModeButton = false,
                                showFullScreenButton = false,
                                controllerShowTimeMilliSeconds = 100, // Hide after 0 milliseconds
                                controllerAutoShow = false
                            ),
                            mediaItems = listOf(
                                VideoPlayerMediaItem.NetworkMediaItem(
                                    url = message.text,
                                    mimeType = MimeTypes.APPLICATION_MP4,
                                ))
                        )
                    } else if (message.type === MediaType.Gif) {
                        ChatGifMessage(message = message)
                    }
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
                    if ( message.type == MediaType.Text) {
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
                    } else if (message.type == MediaType.Image) {
                        AsyncImage(
                            model = message.text,
                            contentDescription = null, // Set content description if needed
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .width(250.dp)
                                .height(250.dp)

                        )
                    } else if (message.type == MediaType.Video) {
                        VideoPlayer(
                            modifier = Modifier
                                .width(250.dp)
                                .height(250.dp),
                            autoPlay = false,
                            controllerConfig = VideoPlayerControllerConfig(
                                showSpeedAndPitchOverlay = false,
                                showSubtitleButton = false,
                                showCurrentTimeAndTotalTime = false,
                                showBufferingProgress = false,
                                showForwardIncrementButton = false,
                                showBackwardIncrementButton = false,
                                showBackTrackButton = false,
                                showNextTrackButton = false,
                                showRepeatModeButton = false,
                                showFullScreenButton = false,
                                controllerShowTimeMilliSeconds = 100, // Hide after 0 milliseconds
                                controllerAutoShow = false
                            ),
                            mediaItems = listOf(
                                VideoPlayerMediaItem.NetworkMediaItem(
                                    url = message.text,
                                    mimeType = MimeTypes.APPLICATION_MP4,
                                ))
                        )
                    } else if (message.type === MediaType.Gif) {
                        ChatGifMessage(message = message)
                    }
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

    if (selectedMessage != null) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                selectedMessage = null
            },
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {


                Row(
                    modifier = Modifier
                        .clickable {
                            onDeleteMessage()
                        }
                        .padding(vertical = 16.dp, horizontal = 8.dp)
                ) {
                    Text("Delete", modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "",

                    )
                }
                Divider()
                Row(
                    modifier = Modifier
                        .clickable {
                            onEditMessage()
                        }
                        .padding(vertical = 16.dp, horizontal = 8.dp)
                ) {
                    Text("Edit", modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "",
                    )
                }
                Divider()
                Row(
                    modifier = Modifier
                        .clickable {
                            scope
                                .launch {
                                    sheetState.hide()
                                }
                                .invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        selectedMessage = null
                                    }
                                }
                        }
                        .padding(vertical = 16.dp, horizontal = 8.dp)
                ) {
                    Text("Close", modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "",
                    )
                }
            }

        }
    }
}