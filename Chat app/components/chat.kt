package com.example.disapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.disapp.data.FBMessage
import com.example.disapp.data.Message
import com.example.disapp.data.User
import com.example.disapp.data.time_stamp_pattern
import com.example.disapp.fireDbInstance
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ChatComponent(modifier: Modifier = Modifier, currentUser: User, currentGroupNum: Int) {

    val chatData = remember { mutableStateListOf<FBMessage>() }
    val messageRef = fireDbInstance.getMessagesRef()

    messageRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val chatD = snapshot.value
            if (chatD !== null) {
                if (chatD is Map<*, *>) {
                    val newD: MutableList<FBMessage> = mutableListOf()
                    @Suppress("UNCHECKED_CAST")
                    for ((key, value) in (chatD as Map<String, Map<*, *>>).entries) {
                        val groupId = (value["group"] as Long).toInt()
                        if (currentGroupNum == groupId)
                            newD.add(
                                FBMessage(
                                    key = key,
                                    value["sender"] as String,
                                    value["message"] as String,
                                    value["date"] as String,
                                    groupId
                                )
                            )
                    }
                    chatData.clear()
                    chatData.addAll(newD.sortedBy {
                        LocalDateTime.parse(
                            it.date, DateTimeFormatter.ofPattern(time_stamp_pattern)
                        )
                    })
                } else println("ChatD is not a Map $chatD")

            } else println("ChatD is Null!")

        }

        override fun onCancelled(error: DatabaseError) {
            println(error)
        }
    })

    Surface(
        modifier = modifier
            .padding(vertical = 5.dp, horizontal = 5.dp)
            .fillMaxSize(),
        color = Color(0xFFCCCCCC)
    ) {
        LazyColumn {
            items(chatData) { message ->
                ChatMessage(
                    sender = message.sender,
                    message = message.message,
                    date = message.date,
                    key = message.key,
                    currentUser,
                    message.group
                )
            }
        }
    }
}

@Composable
fun ChatMessage(
    sender: String, message: String, date: String, key: String, currentUser: User, groupNumber: Int
) {

    var editorOpen by remember { mutableStateOf(false) }
    var newContent by remember { mutableStateOf("") }

    fun handleEdit() {
        editorOpen = if (editorOpen) {
            if (newContent.trim().isNotEmpty()) fireDbInstance.updateMessage(
                fireDbInstance.getMessagesRef(), key, Message(
                    sender, newContent, date, groupNumber
                )
            )
            false
        } else {
            sender == currentUser.name
        }
    }

    fun handleDelete() {
        if (sender == currentUser.name) fireDbInstance.deleteMessage(
            fireDbInstance.getMessagesRef(),
            key
        )
    }

    val buttonsModifiers = Modifier.padding(horizontal = 5.dp)
    val colorGroup =
        if (sender == currentUser.name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    Surface(
        color = colorGroup, modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = sender, fontSize = 15.sp)
            Text(text = message, fontSize = 20.sp, modifier = Modifier.padding(vertical = 10.dp))
            Text(text = date, fontSize = 15.sp)
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                Button(
                    onClick = { handleEdit() },
                    modifier = buttonsModifiers,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80AAFF))
                ) {
                    Text(text = "Edit")
                }

                Button(
                    onClick = { handleDelete() },
                    modifier = buttonsModifiers,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80AAFF))
                ) {
                    Text(text = "Delete")
                }
            }
            if (editorOpen) Row(
                Modifier.padding(vertical = 10.dp)
            ) {
                TextField(value = newContent,
                    onValueChange = { nv -> newContent = nv },
                    placeholder = {
                        Text(text = "Type new message here...")
                    })
            }
        }
    }
}