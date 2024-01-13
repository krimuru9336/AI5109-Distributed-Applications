package com.example.disapp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.disapp.data.OnSelection
import com.example.disapp.data.User

@Composable
fun UserMenu(onSelection: OnSelection, currentUser: User) {

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
    ) {
        Button(onClick = { expanded = true }) {
            Text(
                text = "User: ${currentUser.name}"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            for (u in User.entries)
                DropdownMenuItem(
                    text = { Text(text = u.name) },
                    onClick = { onSelection(u); expanded = false; })
        }
    }
}