package com.example.chitchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.launch
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    MaterialTheme {
        Column() {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = "ChitChat",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }
            )

            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.secondary,
                ),
                title = {
                    Text(
                        text = "Lucas Immanuel Nickel ∙ 1318441",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
        ) {


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = { newText ->
                        text = newText
                    },
                    label = { Text("Name") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null)
                    },
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    if (text.isEmpty()) {
                                        snackbarHostState.showSnackbar(
                                            message = "Please insert text",
                                            actionLabel = "Close",
                                            duration = SnackbarDuration.Short
                                        )
                                    } else {
                                        val temporaryText = text
                                        text = ""
                                        focusManager.clearFocus()
                                        sendTextToServer(temporaryText, snackbarHostState)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text("Insert")
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                focusManager.clearFocus()
                                withContext(Dispatchers.IO) {
                                    retrieveTextFromServer(snackbarHostState)
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text("Retrieve")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

suspend fun sendTextToServer(text: String, snackbarHostState: SnackbarHostState) {
    val client = HttpClient {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }

    val serverUrl = "http://10.0.0.4:8000/save-text"

    println("Text: $text")

    println("[STARTED] Send Client")

    try {
        val response: String

        withTimeout(5000) { // Add a timeout for the send operation
            response = client.post(serverUrl) {
                contentType(ContentType.Application.Json)
                body = mapOf("text" to text)
            }
        }

            println("Server response: $response")

            withContext(Dispatchers.Main) {
                snackbarHostState.showSnackbar(
                    message = "Sent: $text",
                    actionLabel = "Close",
                    duration = SnackbarDuration.Short
                )
        }
    } catch (e: TimeoutCancellationException) {
        e.printStackTrace()
        withContext(Dispatchers.Main) {
            snackbarHostState.showSnackbar(
                message = "Timeout: Server not responding",
                actionLabel = "Close",
                duration = SnackbarDuration.Short
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        withContext(Dispatchers.Main) {
            snackbarHostState.showSnackbar(
                message = "Error sending text: ${e.localizedMessage}",
                actionLabel = "Close",
                duration = SnackbarDuration.Short
            )
        }
    } finally {
        println("[CLOSED] Send Client")
        client.close()
    }
}

suspend fun retrieveTextFromServer(snackbarHostState: SnackbarHostState) {
    withContext(Dispatchers.IO) {
        val client = HttpClient {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }

        val serverUrl = "http://10.0.0.4:8000/retrieve-text"

        println("[STARTED] Retrieve Client")

        try {
            var retrievedText: String
            withTimeout(5000) {
                val response: Map<String, String> =
                    client.get(serverUrl)

                retrievedText = response["text"] ?: ""
            }

                withContext(Dispatchers.Main) {
                    if (retrievedText.isNotEmpty()) {
                        snackbarHostState.showSnackbar(
                            message = "Hello Dear $retrievedText!",
                            actionLabel = "Close",
                            duration = SnackbarDuration.Short
                        )
                    } else {
                        snackbarHostState.showSnackbar(
                            message = "Nothing is retrieved",
                            actionLabel = "Close",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
        } catch (e: TimeoutCancellationException) {
            println("Timeout Exception")
            withContext(Dispatchers.Main) {
                snackbarHostState.showSnackbar(
                    message = "Timeout: Server not responding",
                    actionLabel = "Close",
                    duration = SnackbarDuration.Short
                )
            }
        } catch (e: Exception) {
            println("Normal Exception")
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                snackbarHostState.showSnackbar(
                    message = "Error retrieving text: ${e.localizedMessage}",
                    actionLabel = "Close",
                    duration = SnackbarDuration.Short
                )
            }
        } finally {
            println("[CLOSED] Retrieve Client")
            client.close()
        }
    }
}
