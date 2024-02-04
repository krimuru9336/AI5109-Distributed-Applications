// Import necessary classes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.suhaila.chatapp.R
import com.suhaila.chatapp.ui.theme.ChatAppTheme

@Composable
fun StartScreen(
    onEnterName: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .wrapContentHeight(align = Alignment.CenterVertically)
                .fillMaxWidth()
                .wrapContentSize(align = Alignment.Center),
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = {
                    Text(text = "Type Your Name")
                }
            )
            Spacer(modifier = modifier.height(8.dp))
            Button(
                onClick = { onEnterName(name) },
                enabled = name.isNotEmpty()
            ) {
                Text(text = "Happy Chatting")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartPreview() {
    ChatAppTheme {
        StartScreen(
            onEnterName = {}
        )
    }
}
