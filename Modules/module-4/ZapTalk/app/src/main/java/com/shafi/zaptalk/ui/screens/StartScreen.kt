package com.shafi.zaptalk.ui.screens

import android.view.Surface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shafi.zaptalk.ui.theme.ZapTalkTheme

@Composable
fun StartScreen(
    onEnterName: (String) -> Unit,
) {
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.LightGray, shape = RoundedCornerShape(percent = 50)), // Apply background color to make it oval
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            textStyle = TextStyle(
                color = Color.Black,
                fontWeight = FontWeight.Bold // Making entered text bold
            ),
            label = {
                Text(
                    text = "Enter Your Name",
                    color = Color.Gray,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onEnterName(name) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(Color.Yellow),
            enabled = name.isNotEmpty(),
        ) {
            Text(text = "Start Zapping", color = Color.Black)
        }
    }
}