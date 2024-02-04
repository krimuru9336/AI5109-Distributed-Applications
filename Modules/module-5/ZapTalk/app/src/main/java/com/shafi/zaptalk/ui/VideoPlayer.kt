import android.net.Uri
import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun VideoPlayer(uri: String) {
    val context = LocalContext.current
    val videoUri = remember { Uri.parse(uri) }

    AndroidView(
        factory = { context ->
            VideoView(context).apply {
                setVideoURI(videoUri)
                start()
            }
        },
        update = { videoView ->
            // Update the video player view if needed
        }
    )
}

