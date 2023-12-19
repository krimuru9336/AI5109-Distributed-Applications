package com.example.rahil_android_app_uni

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.rahil_android_app_uni.databinding.ActivityMainBinding

class SecondActivity : AppCompatActivity() {

//    private lateinit var binding: ActivityMainBinding
    private lateinit var textViewUserEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        textViewUserEmail = findViewById(R.id.etUserEmail)

        val userEmail = intent.getStringExtra("userEmail")
        userEmail?.let {
            textViewUserEmail.text = "Logged in as: $userEmail"
        }
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        binding.btnLogin.setOnClickListener {
//            logoutUser()
//        }
    }
}