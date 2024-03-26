package com.example.module_3_chitchat.view

import android.view.MotionEvent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.module_3_chitchat.Constants
import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.unit.Dp
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlin.math.abs


@Composable
fun Title(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun Buttons(
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color? = null,
    fontSize: TextUnit? = null
) {
    val textStyle = TextStyle(
        color = textColor ?: Color.White,
        fontSize = fontSize ?: MaterialTheme.typography.bodyLarge.fontSize
    )

    Button(
        onClick = onClick, colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor, contentColor = textColor ?: Color.White
        ), modifier = modifier, shape = RoundedCornerShape(16.dp)
    ) {
        if (icon != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon, contentDescription = null, tint = textColor ?: Color.White
                )
                if (title != null) {
                    Text(text = title, style = textStyle)
                }
            }
        } else if (title != null) {
            Text(text = title, style = textStyle)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Appbar(
    title: String,
    action: (() -> Unit)? = null,
    icon: ImageVector? = Icons.AutoMirrored.Filled.ArrowBack,
    onAction: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(text = title, color = MaterialTheme.colorScheme.onSurface)
        },
        navigationIcon = {
            if (action != null && icon != null) {
                IconButton(onClick = {
                    action()
                    onAction?.invoke()
                }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Action Icon",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
fun Info(
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Lucas Immanuel Nickel · 1318441",
            modifier = Modifier.padding(8.dp),
            style = LocalTextStyle.current.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun TextFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(color = Color.Transparent),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = label
            )
        },
        visualTransformation = visualTransformation
    )
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SingleMessage(
    messageData: Map<String, Any>, isCurrentUser: Boolean, onHold: (String) -> Unit
) {
    val message = messageData[Constants.MESSAGE] as String
    val senderId = messageData[Constants.SENT_BY] as String
    val sentOn = messageData[Constants.SENT_ON] as Long
    val messageType = Constants.MessageType.valueOf(messageData[Constants.MEDIA_TYPE].toString())

    var touchStartTime by remember { mutableStateOf(0L) }

    val bubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isCurrentUser) 16.dp else 0.dp,
        bottomEnd = if (isCurrentUser) 0.dp else 16.dp
    )

    val backgroundColor =
        if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val contentColor =
        if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
    val horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = horizontalArrangement
        ) {
            Card(shape = bubbleShape,
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                modifier = Modifier
                    .fillMaxWidth(0.66f)
                    .pointerInteropFilter { event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                val currentTime = System.currentTimeMillis()
                                touchStartTime = if (currentTime - touchStartTime < 500) {
                                    onHold(messageData[Constants.MESSAGE_ID] as String)
                                    0L
                                } else {
                                    currentTime
                                }
                                false
                            }

                            else -> {
                                false
                            }
                        }
                    }


            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = if (isCurrentUser) "You" else mapUidToCuteAustralianName(senderId),
                            style = TextStyle(
                                color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(0.5f)
                        )

                        Text(
                            text = DateFormat.format("dd.MM.yy, HH:mm", sentOn).toString(),
                            style = TextStyle(color = contentColor, fontSize = 12.sp),
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(0.5f)
                        )
                    }

                    when (messageType) {
                        Constants.MessageType.TEXT -> {
                            Text(
                                text = message,
                                textAlign = TextAlign.Start,
                                style = TextStyle(color = contentColor, fontSize = 16.sp),
                                color = contentColor,
                                modifier = Modifier.padding(5.dp)
                            )
                        }

                        Constants.MessageType.IMAGE -> {
                            ImageMessage(
                                imageUrl = message
                            )
                        }

                        Constants.MessageType.VIDEO -> {
                            VideoMessage(
                                videoUrl = message
                            )
                        }

                        Constants.MessageType.GIF -> {
                            GifMessage(
                                gifUrl = message
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ImageMessage(
    imageUrl: String
) {
    Image(
        painter = rememberImagePainter(imageUrl),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun VideoMessage(
    videoUrl: String
) {
    val context = LocalContext.current
    val player = remember(videoUrl) {
        SimpleExoPlayer.Builder(context).build().also { exoPlayer ->
            val mediaItem = MediaItem.fromUri(videoUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
    }

    DisposableEffect(videoUrl) {
        onDispose {
            player.release()
        }
    }

    val playerView = remember {
        PlayerView(context).apply {
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }
    }

    playerView.player = player

    AndroidView(factory = { playerView },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .padding(5.dp)
            .clip(RoundedCornerShape(8.dp)),
        update = { view ->
            if (view.player != player) {
                view.player = player
            }
        })
}


@Composable
fun GifMessage(
    gifUrl: String
) {
    GlideImage(
        imageModel = { gifUrl },
        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}


@Composable
fun RoundedIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    contentDescription: String,
    size: Dp = 50.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() }, contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon, contentDescription = contentDescription, tint = iconColor
        )
    }
}


fun mapUidToCuteAustralianName(uid: String): String {
    val cuteAdjectives = listOf(
        "Cuddly",
        "Snuggly",
        "Adorable",
        "Cheerful",
        "Sweet",
        "Lovely",
        "Charming",
        "Playful",
        "Bubbly",
        "Joyful"
    )
    val australianAnimals = listOf(
        "Kangaroo",
        "Koala",
        "Wombat",
        "Quokka",
        "Platypus",
        "Wallaby",
        "Echidna",
        "Bilby",
        "Possum"
    )

    var hash = 0
    uid.forEach { char ->
        hash = 31 * hash + char.code
    }

    hash = abs(hash)

    val randomAdjectiveIndex = hash % cuteAdjectives.size
    val randomAnimalIndex = hash % australianAnimals.size

    return "${cuteAdjectives[randomAdjectiveIndex]} ${australianAnimals[randomAnimalIndex]}"
}











