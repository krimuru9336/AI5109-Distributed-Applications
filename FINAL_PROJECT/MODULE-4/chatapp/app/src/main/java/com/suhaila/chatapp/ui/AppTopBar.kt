package com.suhaila.chatapp.ui

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.suhaila.chatapp.R
import com.suhaila.chatapp.ui.theme.ChatAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    titleText: String,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(text = titleText) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateUp) {
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
    ChatAppTheme {
        AppTopBar(
            titleText = stringResource(id = R.string.app_name),
            canNavigateBack = true,
            onNavigateUp = {}
        )
    }
}