package com.example.assignment1;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        dbHelper = new DatabaseHelper(this);

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDatabase();
            }
        });

        Button retrieveButton = findViewById(R.id.retrieveButton);
        retrieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveFromDatabase();
            }
        });
    }

    private void saveToDatabase() {
        String inputText = editText.getText().toString();

        if (!inputText.isEmpty()) {
            long rowId = dbHelper.insertData(inputText);

            if (rowId != -1) {
                showToast("Data saved to DB with ID: " + rowId);
            } else {
                showToast("Failed to save data to DB");
            }
        } else {
            showToast("Please enter text");
        }
    }

    private void retrieveFromDatabase() {
        Cursor cursor = dbHelper.retrieveData();

        if (cursor.moveToFirst()) {
            do {
                String textValue = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT_VALUE));
                showToast("Retrieved from DB: " + textValue);
            } while (cursor.moveToNext());
        } else {
            showToast("No data in DB");
        }

        cursor.close();
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
