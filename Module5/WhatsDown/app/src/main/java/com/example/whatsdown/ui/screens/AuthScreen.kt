package com.example.whatsdown.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.whatsdown.ui.MyDetails
import com.example.whatsdown.ui.theme.WhatsDownTheme

@Composable
fun AuthScreen(
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    MyDetails()
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) { Button(onClick = onSignIn) { Text(text = "Sign In With Google") } }
}

@Preview(showBackground = true)
@Composable
fun AuthPreview() {
    WhatsDownTheme {
        AuthScreen(onSignIn = {})
    }
}
