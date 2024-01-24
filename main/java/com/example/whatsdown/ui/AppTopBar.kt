package com.example.whatsdown.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.whatsdown.R
import com.example.whatsdown.ui.theme.WhatsDownTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentScreen: String?,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
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
                )
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back Button",
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun AppTopBarPreview() {
    WhatsDownTheme {
        AppTopBar(
            currentScreen = "",
            canNavigateBack = true,
            navigateUp = {}
        )
    }
}