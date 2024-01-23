package com.example.chitchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import com.example.chitchat.ui.theme.ChitChatTheme
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val database = Firebase.database("https://chitchat-a099f-default-rtdb.europe-west1.firebasedatabase.app/")

        setContent {
            ChitChatTheme {
                // A surface container using the 'background' color from the theme
                val firebaseInstances = compositionLocalOf<FirebaseInstances> {
                    error("No FirebaseInstances provided")
                }
                CompositionLocalProvider(
                    firebaseInstances provides FirebaseInstances(
                       Firebase.database("https://chitchat-a099f-default-rtdb.europe-west1.firebasedatabase.app/")
                    )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Navigation()
                    }
                }

            }
        }
    }
}

data class FirebaseInstances(
    val database: FirebaseDatabase
)

val LocalFirebaseInstances = compositionLocalOf<FirebaseInstances> {
    error("No FirebaseInstances provided")
}
