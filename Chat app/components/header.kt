package com.example.disapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.disapp.data.OnGroupSelection
import com.example.disapp.data.OnSelection
import com.example.disapp.data.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(onSelection: OnSelection, currentUser: User, groupSelectionHandler: OnGroupSelection) {
    TopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                Text("Iman Emadi", textAlign = TextAlign.Center, fontSize = 18.sp)
                Text("Matric.No: 1452312", textAlign = TextAlign.Center, fontSize = 16.sp)
                }
                UserMenu(onSelection, currentUser)
                GroupMenu(groupSetter = groupSelectionHandler)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            scrolledContainerColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}