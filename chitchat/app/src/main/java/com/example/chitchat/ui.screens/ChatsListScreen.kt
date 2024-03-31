package com.example.chitchat.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chitchat.Screen
import com.example.chitchat.data.FirebaseViewModel
import com.example.chitchat.data.RoomType
import com.example.chitchat.data.UserViewModel
import com.example.chitchat.ui.composables.MainAppBar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsListScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    name: String?,
    firebaseViewModel: FirebaseViewModel,
) {
    var checkedUsers by remember {
       mutableStateOf<List<String>>(emptyList())
    }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }
    var groupName by remember {
        mutableStateOf("")
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        print("USER ID: ${userViewModel.userId}")
        if (userViewModel.userObj != null) {
            firebaseViewModel.getChatRooms(userViewModel.userObj!!.id)
        }
        snapshotFlow { sheetState.isVisible }.collect { isVisible ->
            if (isVisible) {
                firebaseViewModel.getUsers()
                // Sheet is visible
            } else {
                checkedUsers = emptyList()
                // Not visible
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    fun navigateToChat(chatId: String) {
        navController.navigate(Screen.ChatItemScreen.withArgs(chatId))
    }

    fun onGroupChat() {
        if (userViewModel.userObj != null && groupName.isNotEmpty()) {
            firebaseViewModel.createGroupChat(
                currentUserId = userViewModel.userObj!!.id,
                newChatUserIds = checkedUsers,
                roomName = groupName,
            )
        }
        print(checkedUsers)
    }

    fun onPrivateChat() {
        if (userViewModel.userObj != null) {
            firebaseViewModel.createPrivateChat(
                currentUserId = userViewModel.userObj!!.id,
                newChatUserIds = checkedUsers,
            )
        }
        print(checkedUsers)
    }

    fun onSubmit() {
        if (checkedUsers.size > 1) {
            // Group
            onGroupChat()
            showBottomSheet = false
        } else if (checkedUsers.size == 1) {
            // Private
            onPrivateChat()
            showBottomSheet = false
        }
    }

    @Composable
    fun BottomSheet() {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
        ) {
            // Sheet content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Create new chats",
                        style = MaterialTheme.typography.headlineSmall.copy(),
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                if (checkedUsers.size > 1) {
                    TextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text("Group name") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn {
                    items(firebaseViewModel.usersList) { user ->
                        if (user.id != userViewModel.userObj?.id) {
                            Log.d("user", userViewModel.userObj.toString())
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                            ) {
                                Text(text = user.username)
                                Checkbox(checked = checkedUsers.contains(user.id), onCheckedChange = {
                                    if (it) {
                                        checkedUsers += user.id
                                    } else {
                                        checkedUsers = checkedUsers.filter { it != user.id }
                                    }
                                })
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxSize()
                ){
                    Button(onClick = {
                        onSubmit()
                    }) {
                        Text(text = "Submit")
                    }
                }
            }
        }
    }


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainAppBar(
                title = "Azamat Afzalov - 1492864",
                scrollBehavior = scrollBehavior,
                navController,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                content = { Icon(Icons.Filled.Add, contentDescription = "") },
                onClick = {
                    showBottomSheet = true
                }
            )
        }
    ) { innerPadding ->
        if (showBottomSheet) {
            BottomSheet()
        }

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            items(firebaseViewModel.roomsList) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = {
                            navigateToChat(it.roomId.toString())
                        })
                        .height(72.dp)
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column {
                        Text(text = it.roomName, fontWeight = FontWeight.Bold)
                        Text(
                            text =  if (it.roomType == RoomType.GROUP) {
                                "Group"
                            } else {
                                "Private"
                            }
                            , fontWeight = FontWeight.Light
                        )
                    }
                }
                Divider(
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
                Spacer(modifier = Modifier.height(2.dp))

            }
        }

    }
}