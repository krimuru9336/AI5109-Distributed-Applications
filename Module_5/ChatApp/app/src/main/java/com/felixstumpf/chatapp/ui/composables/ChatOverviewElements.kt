/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

@file:OptIn(ExperimentalMaterialApi::class)

package com.felixstumpf.chatapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.felixstumpf.chatapp.R
import com.felixstumpf.chatapp.models.MessageFeed
import com.felixstumpf.chatapp.viewmodels.ChatOverviewViewModel
import com.felixstumpf.chatapp.viewmodels.ChatViewModel
import kotlinx.coroutines.launch

// Chat Overview Element to show a list of ChatButtons for each MessageFeed
@ExperimentalMaterial3Api
@Composable
fun ChatOverview(navController: NavController, chatOverviewViewModel: ChatOverviewViewModel) {
    val feeds by chatOverviewViewModel.messageFeeds.observeAsState(listOf())
    val refreshing by chatOverviewViewModel.refreshing.observeAsState()
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing!!, onRefresh = {
        chatOverviewViewModel.getChatsForUser()
    })
    Column(
        modifier = Modifier
            .pullRefresh(pullRefreshState)
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .defaultMinSize(minHeight = 250.dp)


    ) {
        if (refreshing == true) {
            PullRefreshIndicator(
                refreshing = refreshing!!,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        DevInfoForLecturer()
        feeds.forEach { messageFeed ->
            ChatButton(navController, chatOverviewViewModel, messageFeed)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}

// Chat Button element to link a certain MessageFeed
// On button click, it navigates to selected MessageFeed
@Composable
fun ChatButton(
    navController: NavController,
    chatOverviewViewModel: ChatOverviewViewModel,
    messageFeed: MessageFeed
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),

        ) {
        TextButton(
            onClick = {
                chatOverviewViewModel.setSelectedMessageFeed(messageFeed)
                navController.navigate("chatMessageFeed")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(RoundedCornerShape(8.dp))

        ) {
            Text(messageFeed.participants.toString().replace("[", "").replace("]", ""))
        }

    }
}

// Add Chat Dialog to open / add a new fully new chat
// Designed for a Modal Bottom Sheet
@Composable
fun AddChatDialog(navController: NavController, chatViewModel: ChatViewModel) {
    val scope = rememberCoroutineScope()
    var textFieldValue by remember { mutableStateOf("") }
    val multipleParticipantsValue = remember { mutableStateOf(mutableListOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.add_chat))
        TextField(
            value = textFieldValue,
            onValueChange =
            {
                textFieldValue = it
            },
            label = { Text(stringResource(id = R.string.email)) },
            trailingIcon = {
                IconButton(onClick = {
                    multipleParticipantsValue.value.add(textFieldValue)
                    textFieldValue = ""
                }) {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = stringResource(id = R.string.add)
                    )
                }
            }

        )
        if (multipleParticipantsValue.value.isNotEmpty()) {
            Column {
                Text(text = stringResource(id = R.string.participants))

                Text(
                    text = multipleParticipantsValue.value.toString().replace("[", "")
                        .replace("]", ""),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
        Button(onClick = {
            if (multipleParticipantsValue.value.isEmpty()) {
                chatViewModel.getChatMessagesWithParticipant(textFieldValue)
            } else {
                chatViewModel.getGroupChatMessagesByParticipants(multipleParticipantsValue.value)
            }
            textFieldValue = ""
            multipleParticipantsValue.value = mutableListOf()
            scope.launch {
                navController.navigate("chatMessageFeed")
            }
        }) {
            Text(text = stringResource(id = R.string.add))
        }
        TextButton(onClick = {
            scope.launch {
                navController.navigate("chats")
            }
        }) {
            Text(text = stringResource(id = R.string.back))
        }


    }

}