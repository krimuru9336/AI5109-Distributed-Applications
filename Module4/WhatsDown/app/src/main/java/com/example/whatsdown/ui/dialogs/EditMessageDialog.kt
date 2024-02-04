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
fun EditMessageDialog(
    currentMessage: String,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var editedMessage by remember { mutableStateOf(currentMessage) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = { onConfirm(editedMessage) }) {
                Text(text = "Confirm")
            }
        },
        title = { Text(text = "Edit Message") },
        text = {
            OutlinedTextField(
                value = editedMessage,
                onValueChange = { editedMessage = it },
                label = { Text(text = "Edit your message here") }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EditMessageDialogPreview() {
    WhatsDownTheme {
        EditMessageDialog(
            currentMessage = "Hi",
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}