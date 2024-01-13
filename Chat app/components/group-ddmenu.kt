package com.example.disapp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.disapp.data.OnGroupSelection

@Composable
fun GroupMenu(groupSetter: OnGroupSelection) {

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier) {
        Button(onClick = { expanded = true }) {
            Text(text = "Group")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text(text = "Group 1") }, onClick = { groupSetter(1) })
            DropdownMenuItem(text = { Text(text = "Group 2") }, onClick = { groupSetter(2) })
            DropdownMenuItem(text = { Text(text = "Group 3") }, onClick = { groupSetter(3) })
        }
    }
}