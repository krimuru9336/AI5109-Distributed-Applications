package com.example.damodule

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        val editText = findViewById<EditText>(R.id.editTextInput)
        val btnSave = findViewById<Button>(R.id.buttonSave)
        val btnRetrieve = findViewById<Button>(R.id.buttonRetrieve)
        val dbHelper = DatabaseHelper(this)

        // Set up the button to save data
        btnSave.setOnClickListener {
            val text = editText.text.toString()
            if (text.isNotEmpty()) {
                dbHelper.insertData(text)
                editText.text.clear() // Clear the input field after saving
            }
        }

        // Set up the button to retrieve data and show it in a dialog
        btnRetrieve.setOnClickListener {
            val data = dbHelper.getData()
            showDialogWithText(data)
        }
    }

    private fun showDialogWithText(text: String) {
        // Using AlertDialog to show the retrieved data
        AlertDialog.Builder(this)
            .setTitle("Retrieved Message")
            .setMessage(text)
            .setPositiveButton("OK", null)
            .show()
    }
}



