/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

@file:OptIn(ExperimentalMaterial3Api::class)

package com.felixstumpf.chatapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.felixstumpf.chatapp.R
import com.felixstumpf.chatapp.viewmodels.ChatViewModel

// Scaffold to organize the entire chat screen
@Composable
fun ChatScaffold(navController: NavController, chatViewModel: ChatViewModel) {
    var scope = rememberCoroutineScope()
    val feed by chatViewModel.messageFeed.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }, content = {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    })
                },

                title = {
                    Text(feed!!.participants.toString().replace("[", "").replace("]", ""))
                },

                )
        },
        bottomBar = {
            BottomAppBar {
                MessageInputField(chatViewModel)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DevInfoForLecturer()
            MessageFeedDisplay(chatViewModel = chatViewModel)
        }

    }
}



