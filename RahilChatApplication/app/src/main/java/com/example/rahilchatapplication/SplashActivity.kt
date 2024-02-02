package com.example.rahilchatapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Set the layout for your splash screen
        supportActionBar?.hide()
        // Use a Handler to delay the transition to the main activity
        Handler().postDelayed({
            // Start your main activity
            val intent = Intent(this@SplashActivity, LogIn::class.java)
            startActivity(intent)
            finish() // close the splash screen activity
        }, 2000) // 2000 milliseconds (2 seconds) delay
    }
}
