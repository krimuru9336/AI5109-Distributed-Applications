import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shafi.zaptalk.ui.Chat
import java.text.SimpleDateFormat
import java.util.Date
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.size
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter

@Composable
fun ChatScreen(
    currentUser: String,
    currentReceiver: String,
    messages: List<Chat>,
    sendMessage: (String, String?, Uri?) -> Unit,
    editMessage: (String, String) -> Unit,
    deleteMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf("") }
    var editMessageText by remember { mutableStateOf("") }
    var selectedMessageId by remember { mutableStateOf<String?>(null) }
    var isEditActive by remember { mutableStateOf(false) }
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val pickImageContract = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedMediaUri = it
            println("Selected media URI: $uri")
        }
    }

    Column(
        modifier = modifier.fillMaxHeight()
    ) {
        Text(
            text = currentReceiver,
            modifier = modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(all = 4.dp),
        ) {
            items(messages) { chat ->
                val isCurrentUserSender = chat.sender == currentUser
                val isSelected = chat.messageId == selectedMessageId

                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .wrapContentWidth(align = if (isCurrentUserSender) Alignment.End else Alignment.Start)
                ) {
                    if (!isCurrentUserSender) {
                        Spacer(modifier = modifier.width(48.dp)) // Spacer for alignment
                    }
                    Column(
                        modifier = modifier
                            .background(if (isCurrentUserSender) Color.Yellow else Color.Gray, MaterialTheme.shapes.medium)
                            .padding(all = 12.dp)
                            .clickable {
                                if (isEditActive) {
                                    isEditActive = false
                                } else {
                                    selectedMessageId = chat.messageId
                                }
                            },
                        horizontalAlignment = if (isCurrentUserSender) Alignment.End else Alignment.Start
                    ) {
                        Text(
                            text = chat.message,
                            style = TextStyle(color = if (isCurrentUserSender) Color.Black else Color.White),
                            modifier = modifier.padding(4.dp)
                        )
                        Text(
                            text = SimpleDateFormat.getDateTimeInstance().format(Date(chat.timeStamp)),
                            style = TextStyle(color = if (isCurrentUserSender) Color.Black else Color.White, fontStyle = FontStyle.Italic),
                            modifier = modifier.padding(4.dp)
                        )
                        // Display media if available
                        chat.mediaUrl?.let { mediaUrl ->
                            Image(
                                painter = rememberImagePainter(data = mediaUrl),
                                contentDescription = "Chat Media",
                                modifier = Modifier.size(100.dp), // Adjust size as needed
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    if (isCurrentUserSender) {
                        Spacer(modifier = modifier.width(48.dp)) // Spacer for alignment
                    }
                }
                Spacer(modifier = modifier.height(4.dp))
            }
        }
        Row(
            modifier = modifier.padding(all = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = if (isEditActive) editMessageText else message,
                onValueChange = {
                    if (isEditActive) {
                        editMessageText = it
                    } else {
                        message = it
                    }
                },
                modifier = modifier.weight(1f),
                label = {
                    Text(text = if (isEditActive) "Edit your message" else "Write your message here")
                }
            )
            Spacer(modifier = modifier.width(4.dp))
            Button(
                onClick = {
                    if (isEditActive) {
                        editMessage(editMessageText, selectedMessageId!!)
                        editMessageText = ""
                        isEditActive = false
                        selectedMessageId = null
                    } else {
                        sendMessage(message, null, selectedMediaUri)
                        message = ""
                        selectedMediaUri = null
                    }
                },
                modifier = modifier.padding(top = 8.dp),
                enabled = if (isEditActive) editMessageText.isNotEmpty() else message.isNotEmpty() || selectedMediaUri != null
            ) {
                Text(text = if (isEditActive) "Save" else "Send")
            }
            Spacer(modifier = modifier.width(4.dp))
            Button(onClick = { pickImageContract.launch("image/*") }) {
                Text("Select Media")
            }
        }
    }
}