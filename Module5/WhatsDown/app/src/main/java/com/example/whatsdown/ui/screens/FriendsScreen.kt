package com.example.whatsdown.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsdown.ui.Friend
import com.example.whatsdown.ui.MyDetails
import com.example.whatsdown.ui.theme.WhatsDownTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FriendsScreen(
    friends: List<Friend>,
    onUnfriend: (String) -> Unit,
    onStartChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item { MyDetails() }
        items(
            items = friends,
            key = { it.id }
        ) { friend ->
            var expanded by remember { mutableStateOf(false) }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onLongClick = { expanded = true },
                        onClick = { onStartChat(friend.name) }
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = null,
                    modifier = modifier.size(48.dp)
                )
                Text(
                    text = friend.name,
                    modifier = modifier.padding(start = 8.dp),
                    fontSize = 24.sp
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

@Preview(showBackground = true)
@Composable
fun FriendsPreview() {
    WhatsDownTheme {
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