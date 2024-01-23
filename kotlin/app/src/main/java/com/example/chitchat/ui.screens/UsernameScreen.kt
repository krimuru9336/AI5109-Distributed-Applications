package com.example.chitchat.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chitchat.Screen
import com.example.chitchat.data.FirebaseViewModel
import com.example.chitchat.data.User
import com.example.chitchat.data.UserViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UsernameScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    firebaseViewModel: FirebaseViewModel,
) {
    var text by remember { mutableStateOf("Azamat") }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun onTextInputSubmitted(text: String) {
        if (text.isNotBlank()) {
            userViewModel.changeUser(text)
            navController.navigate(Screen.ChatsListScreen.withArgs(text))
            val users = firebaseViewModel.users
            users.orderByChild("username").equalTo(text)
                .addListenerForSingleValueEvent( object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // User exists
                            val userSnapshot = snapshot.children.first()
                            val userHashMap = userSnapshot.value

                            if (userHashMap is HashMap<*, *>) {
                                val user = User(
                                    username = userHashMap["username"].toString(),
                                    id = userHashMap["id"].toString(),
                                )
                                userViewModel.changeUserObj(user)
                            }
                        } else {
                            // Create new user
                            val newUserId = users.push().key
                            newUserId?.let {id ->
                                val newUser = User(username = text, id = id)
                                // Insert the new user into the "users" node with the generated ID
                                users.child(newUserId).setValue(newUser)
                                    .addOnSuccessListener {
                                        // Insertion successful
                                        userViewModel.changeUserObj(newUser)
                                        println("User inserted successfully with ID: $newUserId")
                                    }
                                    .addOnFailureListener { e ->
                                        // Handle the error
                                        println("Error inserting user: $e")
                                    }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text input
            OutlinedTextField(
                value = text,
                label = {Text(text = "Username")},
                onValueChange = { newText -> text = newText },
                modifier = Modifier
                    .weight(1f)
                    .height(IntrinsicSize.Max)
                    .fillMaxHeight()
                    .padding(end = 8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // Handle submit action here
                        onTextInputSubmitted(text)
                        keyboardController?.hide()
                    }
                ),
                placeholder = { Text("Type username") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            // Handle submit action here
                            onTextInputSubmitted(text)
                            keyboardController?.hide()
                        },
                        modifier = Modifier
                            .height(IntrinsicSize.Max)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send"
                        )
                    }
                }
            )
        }
    }
}