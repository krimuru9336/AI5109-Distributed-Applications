/// Author: Felix Stumpf
/// Matriculation ID: 1165939
/// HS Fulda / Distributed Applications

package com.felixstumpf.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {

    // region Firebase Auth

    // Initialize Firebase Auth:
    private val auth: FirebaseAuth = Firebase.auth

    // endregion

    //region LiveData

    // Data for User Email:
    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    // Data for User Password:
    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    // Data that shows if auth is still loading:
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    //endregion

    //region Functions

    //region Setters

    //  Function to set/update Email value
    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    // Function to set/update Password value
    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    //endregion

    //region Getters

    // Function to get current user
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    //endregion



    // Function to login user
    fun loginUser(home: () -> Unit) {
        if (_loading.value == false) {
            val email: String = _email.value ?: throw IllegalArgumentException("Email expected!!")
            val password: String =
                _password.value ?: throw IllegalArgumentException("Password expected!!")

            _loading.value = true
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
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