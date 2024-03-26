package com.example.module_3_chitchat.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.IllegalArgumentException

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun loginUser(chats: () -> Unit) {
        if (_loading.value == false) {
            val email: String = _email.value ?: throw IllegalArgumentException("email expected")
            val password: String =
                _password.value ?: throw IllegalArgumentException("password expected")

            _loading.value = true
            println("Starte Anmeldung...")

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    _loading.value = false

                    if (task.isSuccessful) {
                        println("Anmeldung erfolgreich!")
                        chats()
                    } else {
                        println("Fehler bei der Anmeldung: ${task.exception?.message}")
                    }
                }
        } else {
            println("Anmeldevorgang bereits im Gange.")
        }
    }

}