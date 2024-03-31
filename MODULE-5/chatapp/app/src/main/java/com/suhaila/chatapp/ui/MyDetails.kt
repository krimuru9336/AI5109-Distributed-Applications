package com.suhaila.chatapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.suhaila.chatapp.ui.theme.ChatAppTheme

@Composable
fun MyDetails(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Name: Suhaila Kondappilly Aliyar")
        Text(text = "Matriculation Number: 1492822")
    }
}

@Preview(showBackground = true)
@Composable
fun MyDetailsPreview() {
    ChatAppTheme {
        MyDetails()
    }
}