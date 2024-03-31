package com.suhaila.chatapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.suhaila.chatapp.R
import com.suhaila.chatapp.ui.theme.ChatAppTheme

@Composable
fun BottomBar(
    enabled: Boolean,
    selectedIndex: Int,
    onChats: () -> Unit,
    onGroups: () -> Unit,
) {
    val isChatsSelected = selectedIndex == 0
    val isGroupsSelected = selectedIndex == 1

    NavigationBar {
        NavigationBarItem(
            selected = isChatsSelected,
            onClick = onChats,
            icon = {
                Icon(
                    imageVector = if (isChatsSelected) Icons.AutoMirrored.Filled.Chat
                    else Icons.AutoMirrored.Outlined.Chat,
                    contentDescription = stringResource(id = R.string.chats)
                )
            },
            enabled = enabled,
            label = { Text(text = stringResource(id = R.string.chats)) }
        )
        NavigationBarItem(
            selected = isGroupsSelected,
            onClick = onGroups,
            icon = {
                Icon(
                    imageVector = if (isGroupsSelected) Icons.Filled.Groups
                    else Icons.Outlined.Groups,
                    contentDescription = stringResource(id = R.string.groups)
                )
            },
            enabled = enabled,
            label = { Text(text = stringResource(id = R.string.groups)) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    ChatAppTheme {
        BottomBar(
            enabled = false,
            selectedIndex = 0,
            onChats = {},
            onGroups = {}
        )
    }
}