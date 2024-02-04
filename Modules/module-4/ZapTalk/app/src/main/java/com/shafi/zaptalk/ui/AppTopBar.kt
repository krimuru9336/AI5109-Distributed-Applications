package com.shafi.zaptalk.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.shafi.zaptalk.R
import com.shafi.zaptalk.ui.theme.ZapTalkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentScreen: String?,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(
                    id = when (currentScreen) {
                        Destination.JoinChat.name -> R.string.join_chat
                        Destination.Chat.name -> R.string.chat
                        else -> R.string.app_name
                    }
                ),
                color = Color.Black // Set text color to black
            )
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back Button",
                        tint = Color.Black // Set icon color to black
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Yellow, // Set background color to yellow
            titleContentColor = Color.Black // Set text color to black
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AppTopBarPreview() {
    ZapTalkTheme {
        AppTopBar(
            currentScreen = "",
            canNavigateBack = true,
            navigateUp = {}
        )
    }
}