package com.shafi.zaptalk.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun JoinChatScreen(
    usersList: List<String>,
    onJoinChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedUser by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(usersList) { user ->
                    Text(
                        text = user,
                        modifier = Modifier
                            .clickable {
                                selectedUser = user
                            }
                            .padding(vertical = 8.dp),
                        fontSize = 20.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp)) // Adding some space between the list and the button
            Button(
                onClick = { selectedUser?.let { onJoinChat(it) } },
                enabled = selectedUser != null,
                colors = ButtonDefaults.buttonColors(Color.Yellow, contentColor = Color.Black)
            ) {
                Text(text = "Choose a User")
            }
        }
    }
}
