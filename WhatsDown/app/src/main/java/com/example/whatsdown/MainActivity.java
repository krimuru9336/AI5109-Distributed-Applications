package com.example.whatsdown;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText inputText;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.input_text);
        dbHelper = new DatabaseHelper(this);

        dbHelper.clearData();
    }

    public void saveData(View data) {
        String userInput = inputText.getText().toString();
        dbHelper.insertData(userInput);
        Toast.makeText(MainActivity.this, "Data saved to DB", Toast.LENGTH_SHORT).show();
        inputText.setText("");
    }

    public void retrieveData(View view) {
        String retrievedData = dbHelper.retrieveMostRecentData();
        if (!retrievedData.isEmpty()) {
            Toast.makeText(MainActivity.this, "Retrieved Data: " + retrievedData, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
        }
    }

}