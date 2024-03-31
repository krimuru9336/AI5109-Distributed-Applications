package com.example.whatsdown.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.whatsdown.ui.theme.WhatsDownTheme

@Composable
fun AddNewFriendDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var friendName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = { onConfirm(friendName) },
                enabled = friendName.isNotEmpty()
            ) { Text(text = "Confirm") }
        },
        title = { Text(text = "Add New Friend") },
        text = {
            OutlinedTextField(
                value = friendName,
                onValueChange = { friendName = it },
                label = { Text(text = "Enter name of friend") }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddNewFriendPreview() {
    WhatsDownTheme {
        AddNewFriendDialog(
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}