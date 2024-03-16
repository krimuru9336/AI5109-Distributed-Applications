package com.example.disapp.components

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.disapp.data.Message
import com.example.disapp.data.User
import com.example.disapp.data.generateRandomString
import com.example.disapp.fireDbInstance
import com.google.firebase.storage.StorageReference

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatTextInput(currentUser: User, groupNumber: Int) {
    var fieldValue by remember { mutableStateOf("") }
    val controller = LocalSoftwareKeyboardController.current
    val ref = fireDbInstance.getMessagesRef()
    var mediaUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val contentResolver: ContentResolver = context.contentResolver
    val mediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> mediaUri = uri })

    fun sendMessage(downloadUrl: String = "") {
        if (fieldValue.isNotEmpty()) {
            fireDbInstance.pushMessage(
                ref,
                Message(currentUser.name, fieldValue, group = groupNumber, mediaUrl = downloadUrl)
            )
            fieldValue = ""
            mediaUri = null
        }
        controller?.hide()
    }

    fun handleClick() {
        lateinit var storageRef: StorageReference;
        if (mediaUri === null)
            sendMessage()
        else {
            val type = contentResolver.getType(mediaUri!!)
            if (type === null) return
            if (type.startsWith("video/"))
                storageRef = fireDbInstance.getStorageVideoRef()
            else if (type.startsWith("image/"))
                storageRef = fireDbInstance.getStorageImageRef()
            else return; //TODO: Toast message
            val childRef = storageRef.child(generateRandomString(15))
            val uploadTask = childRef.putFile(mediaUri!!)
            val taskChain = uploadTask.continueWithTask { task ->
                if (task.isSuccessful)
                    childRef.downloadUrl
                else null
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val download_url = task.result.toString()
                    sendMessage(downloadUrl = download_url)
                }
            }
        }
    }

    fun handlePicker() {
        mediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }

    Surface(
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .wrapContentHeight()
            .padding(vertical = 20.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            TextField(
                value = fieldValue,
                onValueChange = { value -> fieldValue = value },
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .fillMaxWidth()
            )
            // Buttons Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(horizontal = 3.dp, vertical = 10.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = { handleClick() },
                    modifier = Modifier.padding(horizontal = 5.dp)
                ) {
                    Text(
                        text = "Send",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
                Button(onClick = { handlePicker() }) {
                    Text(
                        text = "Pick File", fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}
