package com.example.wisperly.ui.screens

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
import androidx.compose.material.icons.outlined.Group
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
import com.example.wisperly.ui.Group
import com.example.wisperly.ui.theme.WhatsDownTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupsScreen(
    groups: List<Group>,
    onLeaveGroup: (String) -> Unit,
    onStartChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = groups,
            key = { it.id }
        ) { group ->
            var expanded by remember { mutableStateOf(false) }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onLongClick = { expanded = true },
                        onClick = { onStartChat(group.name) }
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Group,
                    contentDescription = null,
                    modifier = modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp).size(42.dp)
                )
                Text(
                    text = group.name,
                    modifier = modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp),
                    fontSize = 24.sp
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text(text = "Leave Group") },
                        onClick = {
                            expanded = false
                            onLeaveGroup(group.id)
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
fun GroupsPreview() {
    WhatsDownTheme {
        GroupsScreen(
            groups = listOf(
                Group("1", "Group 1"),
                Group("2", "Group 2")
            ),
            onLeaveGroup = {},
            onStartChat = {}
        )
    }
}