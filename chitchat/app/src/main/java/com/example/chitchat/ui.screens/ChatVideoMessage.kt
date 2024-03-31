
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView

@Composable
fun VideoMessage(modifier: Modifier = Modifier, videoUrl: String) {
    val ctx = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(ctx).build()
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            StyledPlayerView(context).apply {
                player = exoPlayer
            }
        },
        update = { view ->
            val mediaItem = MediaItem.fromUri(videoUrl)
            exoPlayer.setMediaItem(
                mediaItem
            )
            exoPlayer.prepare()
        },
        onRelease = { exoPlayer.release() }
    )
}
