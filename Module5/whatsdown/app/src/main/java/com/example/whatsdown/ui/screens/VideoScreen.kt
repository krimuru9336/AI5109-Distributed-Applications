package com.example.whatsdown.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoScreen(
    link: String,
    player: ExoPlayer,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mediaItem = MediaItem.fromUri(Uri.parse(link))
    player.setMediaItem(mediaItem)
    player.prepare()
    player.playWhenReady = false

    AndroidView(
        factory = { PlayerView(context).apply { this.player = player } },
        modifier = modifier.fillMaxSize()
    )
}