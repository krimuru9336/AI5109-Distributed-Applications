package com.example.whatsdown.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.whatsdown.ui.theme.WhatsDownTheme

@Composable
fun DeleteMessageDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = { Button(onClick = onConfirm) { Text(text = "Confirm") } },
        title = { Text(text = "Delete Message") },
        text = {
            Text(
                text = "Are you sure you want to delete this message?",
                textAlign = TextAlign.Justify
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DeleteMessageDialogPreview() {
    WhatsDownTheme {
        DeleteMessageDialog(
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}