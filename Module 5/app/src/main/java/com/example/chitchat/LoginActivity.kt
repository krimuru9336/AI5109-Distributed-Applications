package com.example.chitchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.chitchat.models.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    var usernameInput: EditText? = null
    lateinit var okBtn: Button
    var userModel: UserModel? = null
    var mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        usernameInput = findViewById(R.id.login_username)
        okBtn = findViewById(R.id.login_let_me_in_btn)
        mAuth.signInAnonymously().addOnCompleteListener { task: Task<AuthResult?>? ->
            okBtn.setOnClickListener(
                View.OnClickListener { v: View? -> setUsername() }
            )
        }
    }

    fun setUsername() {
        val username = usernameInput!!.text.toString()
        if (username.isEmpty()) {
            usernameInput!!.error = "At least 1 character!"
            return
        }
        if (userModel != null) {
            userModel?.username =username
        } else {
            userModel = UserModel(username, Utils.currentUserId())
        }
        Utils.currentUserDetails().set(userModel!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(
                    this@LoginActivity,
                    MainActivity::class.java
                )
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }
    }
}