package com.example.wisperly.ui.dialogs

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
import com.example.wisperly.ui.theme.WhatsDownTheme

@Composable
fun CreateNewGroupDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var groupName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = { onConfirm(groupName) },
                enabled = groupName.isNotEmpty()
            ) { Text(text = "Confirm") }
        },
        title = { Text(text = "Create New Group") },
        text = {
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text(text = "Enter name of group") }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CreateNewGroupPreview() {
    WhatsDownTheme {
        CreateNewGroupDialog(
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}