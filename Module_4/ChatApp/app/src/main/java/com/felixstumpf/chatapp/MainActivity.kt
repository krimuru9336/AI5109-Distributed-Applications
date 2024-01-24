/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

@file:OptIn(ExperimentalMaterial3Api::class)

package com.felixstumpf.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.felixstumpf.chatapp.ui.theme.ChatAppTheme
import com.felixstumpf.chatapp.ui.theme.dark_CustomColor1
import com.felixstumpf.chatapp.ui.theme.dark_CustomColor2
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    //region ViewModels

    private val registerViewModel = RegisterViewModel()
    private val loginViewModel = LoginViewModel()
    private val chatViewModel = ChatViewModel()
    private val chatOverviewViewModel = ChatOverviewViewModel(chatViewModel)

    //endregion


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {


            ChatAppTheme(isSystemInDarkTheme()) {
                MainUI()
            }
        }
    }

    //region Composables

    @Preview(showBackground = true)
    @Composable
    fun MainUI() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "start") {
            composable("start") {
                StartScreen(navController)
            }
            composable("register") {
                RegisterScreen(navController)
            }
            composable("login") {
                LoginScreen(navController)
            }
            composable("chats") {
                MainScaffold(navController)
            }
            composable("chatMessageFeed") {
                ChatScaffold(
                    navController,
                    chatViewModel = chatViewModel //TODO
                )
            }
        }
    }


    @Composable
    fun MainScaffold(navController: NavController) {
        val route = remember { mutableStateOf(Routes.CHATS) }
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val showBottomSheet = remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(false)
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Divider()
                    Text(
                        stringResource(id = R.string.app_menu),
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        stringResource(id = R.string.developer_info_for_lecturer),
                        modifier = Modifier.padding(8.dp)
                    )
                    Divider()

                    NavigationDrawerItem(
                        label = {
                            Text(stringResource(id = R.string.chats))
                        },
                        selected = false,
                        onClick = {
                            route.value = Routes.CHATS
                            scope.launch { drawerState.close() }
                        },
                        icon = {
                            Icon(
                                Icons.Outlined.Favorite,
                                contentDescription = stringResource(id = R.string.chats)
                            )
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text(stringResource(id = R.string.users)) },
                        selected = false,
                        onClick = {
                            route.value = Routes.USER
                            scope.launch { drawerState.close() }
                        },
                        icon = {
                            Icon(
                                Icons.Outlined.Favorite,
                                contentDescription = stringResource(id = R.string.users)
                            )
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text(stringResource(id = R.string.settings)) },
                        selected = false,
                        onClick = {
                            route.value = Routes.SETTINGS
                            scope.launch { drawerState.close() }
                        },
                        icon = {
                            Icon(
                                Icons.Outlined.Settings,
                                contentDescription = stringResource(id = R.string.settings)
                            )
                        }
                    )
                }
            },
            gesturesEnabled = true
        ) {


            Scaffold(
                topBar = {
                    TopAppBar(

                        title = {
                            Text(stringResource(id = R.string.app_name))
                        }

                    )
                },

                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        scope.launch {
                            showBottomSheet.value = true
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            ) { innerPadding ->

                Column(
                    modifier = Modifier
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    when (route.value) {
                        Routes.CHATS -> ChatOverview(
                            navController = navController,
                            chatOverviewViewModel = chatOverviewViewModel //TODO
                        )

                        Routes.USER -> Text("User")
                        Routes.SETTINGS -> Text("Settings")
                    }
                    if (showBottomSheet.value) {
                        ModalBottomSheet(
                            onDismissRequest = { showBottomSheet.value = false },
                            sheetState = sheetState
                        ) {
                            AddChatDialog(navController)
                        }

                    }

                }
            }

        }
    }


    object Routes {
        const val CHATS = "Chats"
        const val USER = "User"
        const val SETTINGS = "Settings"
    }

    @Composable
    fun AddChatDialog(navController: NavController) {
        val scope = rememberCoroutineScope()
        var textFieldValue by remember { mutableStateOf("") }
        ModalDrawerSheet {
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
                    label = { Text(stringResource(id = R.string.email)) })
                Button(onClick = {
                    chatViewModel.getChatMessagesWithParticipant(textFieldValue) //TODO
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

    }

    @Composable
    fun ChatOverview(navController: NavController, chatOverviewViewModel: ChatOverviewViewModel) {
        val feeds by chatOverviewViewModel.messageFeeds.observeAsState(listOf())
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            DevInfoForLecturer()
            feeds.forEach { messageFeed ->
                ChatButton(navController, messageFeed)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

    }

    @Composable
    fun ChatButton(navController: NavController, messageFeed: MessageFeed) {
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
                Text(messageFeed.participant)
            }

        }
    }

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
                        Text(feed!!.participant)
                    },

                    )
            },
            bottomBar = {
                BottomAppBar {
                    MessageInputField()
                }
            },
//            floatingActionButton = {
//                FloatingActionButton(onClick = {
//                    scope.launch {
//                        drawerState.apply{
//                            if(isClosed){
//                                open()
//                            }else{
//                                close()
//                            }
//                        }
//                    }
//                }) {
//                    Icon(Icons.Default.Menu, contentDescription = "Add")
//                }
//            }
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


    @Composable
    fun MessageFeedDisplay(chatViewModel: ChatViewModel) {
        val messageFeed by chatViewModel.messageFeed.observeAsState(MessageFeed("", listOf()))

        val lazyListState = rememberLazyListState()

        LaunchedEffect(messageFeed.messages) {

            if (messageFeed.messages.isNotEmpty()) {
                // Scroll to the latest item when the message list changes
                lazyListState.scrollToItem(messageFeed.messages.size - 1)
            }
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize(),
        ) {
            itemsIndexed(messageFeed.messages) { index, message ->
                MessageBubble(message = message, chatViewModel)

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }


    @Composable
    fun MessageBubble(message: MessageEntity, chatViewModel: ChatViewModel) {
        val isFromCurrentUser by remember { mutableStateOf(message.senderEmail == chatViewModel.currentUser.value?.email) }
        val selectedMessage by chatViewModel.selectedMessage.observeAsState()
        val modalBottomSheetVisible = remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 48f,
                        topEnd = 48f,
                        bottomStart = if (isFromCurrentUser) 48f else 0f,
                        bottomEnd = if (isFromCurrentUser) 0f else 48f
                    )
                )
                .background(if (isFromCurrentUser) dark_CustomColor1 else dark_CustomColor2)
                .padding(16.dp)
                .fillMaxWidth()
                .clickable {
                    chatViewModel.setSelectedMessage(message)
                    modalBottomSheetVisible.value = true
                }
        ) {
            Column {
                Row {
                    Text(text = message.senderEmail, style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = message.timestamp.toDate().toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }


                Text(text = message.message)

            }
            if (modalBottomSheetVisible.value && isFromCurrentUser) {
                ModalBottomSheet(onDismissRequest = { modalBottomSheetVisible.value = false }) {
                    Text(
                        text = stringResource(id = R.string.message_menu),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            chatViewModel.deleteMessage()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.delete_message))

                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    ){
                        TextField(
                            modifier = Modifier.height(120.dp),
                            value = selectedMessage!!.message,
                            onValueChange = {
                                chatViewModel.setSelectedMessage(
                                    selectedMessage!!.copy(
                                        message = it
                                    ))
                            },
                            label = { Text(stringResource(id = R.string.edit_message)) })
                        Button(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .fillMaxWidth()
                                .padding(8.dp),

                            onClick = {
                                chatViewModel.updateMessage()
                            }
                        ) {
                            Text(text = stringResource(id = R.string.edit))

                        }
                    }


                    Spacer(modifier = Modifier.height(96.dp))

                }
            }
        }
    }


    @Composable
    fun MessageInputField() {
        val message = remember { mutableStateOf("") }
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = message.value,
                onValueChange = {
                    message.value = it
                },
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(id = R.string.message)) })
            Button(
                onClick = {
                    chatViewModel.setMessageToSendByText(
                        message.value
                    )
                    chatViewModel.sendMessage()
                    message.value = "" // TODO: observe viewmodel?


                },
                modifier = Modifier
                    .padding(start = 8.dp)
            )

            {
                Icon(
                    Icons.Outlined.KeyboardArrowRight,
                    contentDescription = stringResource(id = R.string.send)
                )
            }


        }
    }

    @Composable
    fun StartScreen(navController: NavController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(id = R.string.app_name))
            TextButton(onClick = {
                navController.navigate("login")
            }) {
                Text(text = stringResource(id = R.string.login))
            }
            TextButton(onClick = {
                navController.navigate("register")
            }) {
                Text(text = stringResource(id = R.string.register))
            }
            DevInfoForLecturer()
        }
    }

    @Composable
    fun RegisterScreen(navController: NavController) {
        val emailState = remember { mutableStateOf(registerViewModel.email.value) }
        val passwordState = remember { mutableStateOf(registerViewModel.password.value) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(id = R.string.register))
            TextField(value = emailState.value!!, onValueChange = {
                emailState.value = it
            }, label = { Text(stringResource(id = R.string.email)) })
            TextField(value = passwordState.value!!, onValueChange = {
                passwordState.value = it
            }, label = { Text(stringResource(id = R.string.password)) },
                visualTransformation = PasswordVisualTransformation()
            )
            Button(onClick = {
                registerViewModel.updateEmail(emailState.value!!)
                registerViewModel.updatePassword(passwordState.value!!)
                registerViewModel.registerUser { navController.navigate("login") }


            }) {
                Text(text = stringResource(id = R.string.register))
            }
            TextButton(onClick = {
                navController.navigate("start")
            }) {
                Text(text = stringResource(id = R.string.back))
            }
            DevInfoForLecturer()

        }
    }


    @Composable
    fun LoginScreen(navController: NavController) {
        val emailState = remember { mutableStateOf(loginViewModel.email.value) }
        val passwordState = remember { mutableStateOf(loginViewModel.password.value) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(id = R.string.login))
            TextField(value = emailState.value!!, onValueChange = {
                emailState.value = it
            }, label = { Text(stringResource(id = R.string.email)) })
            TextField(value = passwordState.value!!, onValueChange = {
                passwordState.value = it
            }, label = { Text(stringResource(id = R.string.password)) },
                visualTransformation = PasswordVisualTransformation()
            )
            Button(onClick = {
                loginViewModel.updateEmail(emailState.value!!)
                loginViewModel.updatePassword(passwordState.value!!)
                loginViewModel.loginUser {
                    chatViewModel.currentUser.value = loginViewModel.getCurrentUser()!!
                    chatOverviewViewModel.currentUser.value = loginViewModel.getCurrentUser()!!
                    chatOverviewViewModel.getChatsForUser() //TODO
                    navController.navigate("chats")

                }


            }) {
                Text(text = stringResource(id = R.string.login))
            }
            TextButton(onClick = {
                navController.navigate("start")
            }) {
                Text(text = stringResource(id = R.string.back))
            }
            DevInfoForLecturer()

        }
    }


    @Composable
    fun DevInfoForLecturer() {
        Text(
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.labelMedium,
            text = stringResource(id = R.string.developer_info_for_lecturer)
        )
    }

    //endregion

}