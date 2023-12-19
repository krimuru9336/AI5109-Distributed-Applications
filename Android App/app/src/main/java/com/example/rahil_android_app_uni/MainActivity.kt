package com.example.rahil_android_app_uni

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rahil_android_app_uni.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var user: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = FirebaseAuth.getInstance()
        binding.btnLogin.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser(){
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()) {
            user.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity()) { task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,
                            "User created and logged in",
                            Toast.LENGTH_LONG).show()
                        val intent = Intent(this@MainActivity, SecondActivity::class.java)
                        intent.putExtra("userEmail", email)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT).show()
                    }
                }

        }else{
            Toast.makeText(this,
                "Email and password cannot be empty",
                Toast.LENGTH_SHORT).show()
        }

    }


}