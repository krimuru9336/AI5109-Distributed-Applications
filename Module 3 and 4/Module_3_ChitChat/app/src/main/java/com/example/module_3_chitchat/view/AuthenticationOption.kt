package com.example.module_3_chitchat.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.module_3_chitchat.ui.theme.ChitChatTheme

@Composable
fun AuthenticationView(register: () -> Unit, login: () -> Unit) {
    ChitChatTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Title(title = "ChitChat")

                Buttons(
                    title = "Register",
                    onClick = register,
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .fillMaxWidth(0.66f)
                        .padding(bottom = 4.dp)
                )
                Buttons(
                    title = "Login",
                    onClick = login,
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                    textColor = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .fillMaxWidth(0.66f)
                        .padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Info()
            }
        }
    }
}
