package com.felixstumpf.chatapp.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.felixstumpf.chatapp.R

// Text to display developer info for the lecturer for evaluation
@Composable
fun DevInfoForLecturer() {
    Text(
        color = MaterialTheme.colorScheme.tertiary,
        style = MaterialTheme.typography.labelMedium,
        text = stringResource(id = R.string.developer_info_for_lecturer)
    )
}