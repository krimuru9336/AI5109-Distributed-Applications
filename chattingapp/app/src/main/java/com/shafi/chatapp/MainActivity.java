package com.shafi.chatapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText inputField;
    private TextView displayText;
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputField = findViewById(R.id.inputField);
        displayText = findViewById(R.id.displayText);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        Button storeButton = findViewById(R.id.storeButton);
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = inputField.getText().toString().trim();

                if (!userInput.isEmpty()) {
                    dbHelper.getWritableDatabase().execSQL("INSERT INTO UserInput (textValue) VALUES ('" + userInput + "')");

                    // Fetch the last inserted value from the database
                    String lastInsertedValue = dbHelper.getLastInsertedValue();

                    // Display the fetched value in the toast
                    if (lastInsertedValue != null) {
                        Toast.makeText(MainActivity.this, "Message stored in DB: " + lastInsertedValue, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Error retrieving stored value", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a value", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button retrieveButton = findViewById(R.id.retrieveButton);
        retrieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latestEntry = dbHelper.getLatestEntry();

                if (latestEntry != null) {
                    // Display the latest entry as a toast message
                    Toast.makeText(MainActivity.this,  latestEntry, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No entries found in DB", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
