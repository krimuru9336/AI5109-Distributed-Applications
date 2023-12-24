package com.da.module1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.da.module1.ui.theme.Module1Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val db by lazy {
        NameDatabase.getDatabase(this)
    }
    private val viewModel by viewModels<DatabaseViewModel>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST") return DatabaseViewModel(db.dao) as T
            }
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Module1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state by viewModel.state.collectAsState()
                    Greeting(state = state, onEvent = viewModel::onEvent)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(state: NameState, onEvent: (NameEvent) -> Unit, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Box(contentAlignment = Alignment.BottomCenter) {
        Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            TopAppBar(
                title = { Text(text = "Module 1") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
            Text(text = "Name: Rahul Patil")
            Text(text = "Matriculation Number: 1478745")
            Column(modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                TextField(value = state.name, onValueChange = {
                    onEvent(NameEvent.SetName(it))
                }, placeholder = {
                    Text(text = "Your Name")
                })
                Row {
                    Button(onClick = {
                        onEvent(NameEvent.StoreName)
                    }) {
                        Text(text = "Store")
                    }
                    Button(onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(state.lastStoredName)
                        }
                    }) {
                        Text(text = "Retrieve")
                    }
                }
            }
        }
        SnackbarHost(hostState = snackbarHostState)
    }
}