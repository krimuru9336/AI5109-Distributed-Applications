package com.example.mychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import java.net.PasswordAuthentication

class Login : AppCompatActivity() {

 private lateinit var edtEmail: EditText
 private lateinit var edtPasswordAuthentication: EditText
 private lateinit var btnLogin : Button
 private lateinit var btnSignup: Button
 private lateinit var mAuth : FirebaseAuth





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        edtEmail = findViewById(R.id.edt_email)
        edtPasswordAuthentication = findViewById(R.id.edt_password)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignup = findViewById(R.id.btnSignup)

        btnSignup.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
btnLogin.setOnClickListener {

    val email = edtEmail.text.toString()
    val passwordAuthentication = edtPasswordAuthentication.text.toString()

    login(email,passwordAuthentication);
}
    }

private fun login(email: String, passwordAuthentication:String){
    //logic logging user
    mAuth.signInWithEmailAndPassword(email, passwordAuthentication)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // code for logging in user
                val intent = Intent(this@Login, MainActivity::class.java)
                finish()
                startActivity(intent)

            } else {
                Toast.makeText(this@Login,"User doest not exist",Toast.LENGTH_SHORT).show()
            }
        }

}




}


