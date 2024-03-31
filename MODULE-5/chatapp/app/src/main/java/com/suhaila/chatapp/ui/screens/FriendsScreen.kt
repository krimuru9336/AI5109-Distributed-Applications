package com.suhaila.chatapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suhaila.chatapp.R
import com.suhaila.chatapp.ui.Friend
import com.suhaila.chatapp.ui.theme.ChatAppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FriendsScreen(
    friends: List<Friend>,
    onUnfriend: (String) -> Unit,
    onStartChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.img), // Replace img with your image resource
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = friends,
                key = { it.id }
            ) { friend ->
                var expanded by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(color = Color.White)
                        .combinedClickable(
                            onLongClick = { expanded = true },
                            onClick = { onStartChat(friend.name) }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = friend.name,
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 24.sp,
                        style = TextStyle(color = Color.Black)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text(text = "Unfriend") },
                            onClick = {
                                expanded = false
                                onUnfriend(friend.id)
                            }
                        )
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FriendsPreview() {
    ChatAppTheme {
        FriendsScreen(
            friends = listOf(
                Friend("1", "Friend 1"),
                Friend("2", "Friend 2")
            ),
            onUnfriend = {},
            onStartChat = {}
        )
    }
}
