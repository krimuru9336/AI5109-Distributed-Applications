package com.example.whatsdown.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.whatsdown.ui.Chat
import com.example.whatsdown.ui.theme.WhatsDownTheme
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ChatScreen(
    currentUser: String,
    currentReceiver: String,
    messages: List<Chat>,
    sendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf("") }

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
            items(messages) {
                Box(
                    modifier = modifier
                        .fillMaxWidth(),
                    contentAlignment = if (it.sender == currentUser) Alignment.CenterEnd
                    else Alignment.CenterStart
                ) {
                    Card {
                        Text(
                            text = SimpleDateFormat
                                .getDateTimeInstance()
                                .format(Date(it.timeStamp)),
                            modifier = modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary
                                )
                                .padding(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                        Text(
                            text = it.message,
                            modifier = modifier.padding(4.dp)
                        )
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
                value = message,
                onValueChange = { message = it },
                modifier = modifier.weight(1f),
                label = {
                    Text(text = "Write your message here")
                }
            )
            Spacer(modifier = modifier.width(4.dp))
            Button(
                onClick = {
                    sendMessage(message)
                    message = ""
                },
                modifier = modifier.padding(top = 8.dp),
                enabled = message.isNotEmpty()
            ) {
                Text(text = "Send")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatPreview() {
    WhatsDownTheme {
        ChatScreen(
            currentUser = "rahul",
            currentReceiver = "shyam",
            messages = listOf(
                Chat("rahul", "shyam", "hi", 1705862035019),
                Chat("rahuls", "shyam", "hello", 1705862035019),
                Chat("rahulas", "shyam", "bye", 1705862035019)
            ),
            sendMessage = {}
        )
    }
}