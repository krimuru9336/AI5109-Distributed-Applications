package com.example.chitchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    lateinit var searchButton: ImageButton
    lateinit var headerTextView: TextView
    var chatFragment: ChatFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Utils.isLoggedIn()) {
            chatFragment = ChatFragment()
            searchButton = findViewById(R.id.main_search_btn)
            searchButton = findViewById(R.id.main_search_btn)
            headerTextView = findViewById(R.id.main_header_text_view)
            searchButton.setOnClickListener(View.OnClickListener { v: View? ->
                startActivity(
                    Intent(
                        this@MainActivity,
                        SearchActivity::class.java
                    )
                )
            })
            Utils.currentUserDetails().get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    val username = document.getString("username")
                    headerTextView.append(" ($username)")
                }
            }
            supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout,
                chatFragment!!
            )
                .commit()
        } else {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }
}
