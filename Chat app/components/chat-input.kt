package com.example.disapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.disapp.data.Message
import com.example.disapp.data.User
import com.example.disapp.fireDbInstance

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatTextInput(currentUser: User, groupNumber:Int) {
    var fieldValue by remember { mutableStateOf("") }
    val controller = LocalSoftwareKeyboardController.current
    val ref = fireDbInstance.getMessagesRef()
    fun handleClick() {
        if (fieldValue.isNotEmpty()){
            fireDbInstance.pushMessage(ref, Message(currentUser.name, fieldValue, group = groupNumber))
            fieldValue = ""
        }
        controller?.hide()
    }

    Surface(
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .wrapContentHeight()
            .padding(vertical = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 3.dp)
        ) {
            Button(
                onClick = { handleClick() },
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                Text(
                    text = "Send",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
            }
            TextField(
                value = fieldValue,
                onValueChange = { value -> fieldValue = value },
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        }
    }
}
