package com.example.disapp

import android.graphics.Rect
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.example.disapp.components.ChatComponent
import com.example.disapp.components.ChatTextInput
import com.example.disapp.components.Header
import com.example.disapp.data.User
import com.example.disapp.db.FireBaseDB


val fireDbInstance = FireBaseDB()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppWrapper()
        }

    }
}

@Composable
fun AppWrapper() {
    val vh = remember { mutableStateOf(550.dp) }
    val density = LocalDensity.current.density
    var currentUser by remember { mutableStateOf(User.Iman) }
    var currentGroup by remember { mutableStateOf(1) }
    val rootView = LocalView.current.rootView

    rootView.viewTreeObserver.addOnGlobalLayoutListener {
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)

        val calc = r.bottom / density
        vh.value = calc.dp

    }

    fun setUser(u: User) {
        currentUser = u
    }

    fun setGroup(v: Int) {
        currentGroup = v
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(vh.value),
        Arrangement.SpaceBetween
    ) {
        Header(::setUser, currentUser, ::setGroup)
        ChatComponent(modifier = Modifier.weight(1f), currentUser, currentGroup)
        ChatTextInput(currentUser, currentGroup)
    }

}
