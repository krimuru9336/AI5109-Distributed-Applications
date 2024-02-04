package com.example.chatapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun JoinChatScreen(
    usersList: List<String>,
    onJoinChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedUser by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier= modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(usersList) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedUser == it,
                        onClick = { selectedUser = it }
                    )
                    Text(text = it)
                }
            }
        }
        Button(
            onClick = { onJoinChat(selectedUser) },
            enabled = selectedUser.isNotEmpty()
        ) {
            Text(text = "Join Chat")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JoinChatPreview() {
    ChatAppTheme {
        JoinChatScreen(
            usersList = listOf("Suhaila", "Nizam"),
            onJoinChat = {}
        )
    }
}