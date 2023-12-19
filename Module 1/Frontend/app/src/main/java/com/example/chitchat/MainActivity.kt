package com.example.chitchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnInsert: Button = findViewById(R.id.btnInsert)
        btnInsert.setOnClickListener { view ->
            lifecycleScope.launch {
                btnInsertOnClick(view)
            }
        }
    }

    fun btnInsertOnClick(view: View) {
        val etName: TextView = findViewById(R.id.etName)
        val name: String = etName.text.toString()

        if (name.isBlank()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val postRequest = NameData(name = name)
            val call: Call<ResponseBody> = apiService.postName(postRequest)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val statusCode = response.code()
                    val errorMessage = response.message()
                    if (response.isSuccessful) {
                        println("Sent name. Status: $statusCode")
                        showToast("Name successfully sent")
                    } else {
                        println("Error sending name. Status: $statusCode, Error: $errorMessage")
                        showToast("Error sending name")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    handleException("Error sending name", t)
                }
            })
        } catch (e: Exception) {
            handleException("Error sending name", e)
        }
    }

    fun btnRetrieveOnClick(view: View) {
        try {
            val call: Call<NameData> = apiService.getName()
            call.enqueue(object : Callback<NameData> {
                override fun onResponse(
                    call: Call<NameData>,
                    response: Response<NameData>
                ) {
                    val statusCode = response.code()
                    val errorMessage = response.message().ifEmpty {
                        response.errorBody()?.string() ?: "Unknown error"
                    }

                    if (response.isSuccessful) {
                        println("Retrieved name. Status: $statusCode")
                        showToast("Hello Dear " + response.body()?.name)
                    } else {
                        println("Error retrieving name. Status: $statusCode, Error: $errorMessage")
                        showToast("Error retrieving name")
                    }
                }

                override fun onFailure(call: Call<NameData>, t: Throwable) {
                    handleException("Error retrieving name", t)
                }
            })
        } catch (e: Exception) {
            handleException("Error retrieving name", e)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleException(message: String, t: Throwable) {
        println(message)
        t.printStackTrace(System.out)
        showToast(message)
    }
}