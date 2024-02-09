/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

package com.felixstumpf.chatapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.felixstumpf.chatapp.R
import com.felixstumpf.chatapp.viewmodels.ChatOverviewViewModel
import com.felixstumpf.chatapp.viewmodels.ChatViewModel
import com.felixstumpf.chatapp.viewmodels.LoginViewModel
import com.felixstumpf.chatapp.viewmodels.RegisterViewModel

// Start screen to select between login and register screen
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

// Register screen to register a new user
@Composable
fun RegisterScreen(navController: NavController, registerViewModel: RegisterViewModel) {
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

// Login screen to login an existing user
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    chatViewModel: ChatViewModel,
    chatOverviewViewModel: ChatOverviewViewModel
) {
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
                chatOverviewViewModel.getChatsForUser()
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
