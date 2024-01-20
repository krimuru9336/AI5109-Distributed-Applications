/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

package com.felixstumpf.chatapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterViewModel : ViewModel() {

    //region Firebase Auth

    private val auth: FirebaseAuth = Firebase.auth

    //endregion

    //region LiveData

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    //endregion


    //region Functions

    //region Setters

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    //endregion

    fun registerUser(home: () -> Unit) {
        if (_loading.value == false) {
            val email: String = _email.value ?: throw IllegalArgumentException("Email expected!!")
            val password: String =
                _password.value ?: throw IllegalArgumentException("Password expected!!")

            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    home()
                } else {
                    _loading.value = false
                }
            }

        }
    }

    //endregion
}