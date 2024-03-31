package com.example.wisperly.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wisperly.R
import com.example.wisperly.ui.theme.WhatsDownTheme
import com.airbnb.lottie.compose.*


@Composable
fun AuthScreen(
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Remember the Lottie animation state
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Lottie animation at the top
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )

        Spacer(modifier = Modifier.height(0.dp))


        Text(
            text = "Whisprly",
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )


        Text(
            text = "Connect with your friends and family",
            modifier = Modifier.padding( bottom = 10.dp)
        )
        Button(onClick = onSignIn) {

            Text(text = "Sign In With Google"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthPreview() {
    WhatsDownTheme {
        AuthScreen(onSignIn = {})
    }
}
