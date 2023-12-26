package com.example.myfirstapp;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private NoteDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSource = new NoteDataSource(this);
        dataSource.open();

        final EditText inputEditText = findViewById(R.id.inputEditText);
        Button saveButton = findViewById(R.id.saveButton);
        Button retrieveButton = findViewById(R.id.retrieveButton); // Assuming you have a button named retrieveButton

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputValue = inputEditText.getText().toString().trim();

                if (!inputValue.isEmpty()) {
                    long id = dataSource.insertNote(inputValue);

                    if (id != -1) {
                        showToast("Note saved: " + inputValue);
                    } else {
                        showToast("Error saving note");
                    }
                } else {
                    showToast("Input is empty");
                }
            }
        });

        retrieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve data from the database and display in a toast
                String data = retrieveData();
                showToast("Retrieved data: " + data);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String retrieveData() {
        StringBuilder dataStringBuilder = new StringBuilder();

        // Retrieve all notes from the database
        Cursor cursor = dataSource.getAllNotes();

        if (cursor.moveToFirst()) {
            int contentIndex = cursor.getColumnIndex("content");

            if (contentIndex != -1) {
                do {
                    // Assuming the content is stored in the 'content' column
                    String content = cursor.getString(contentIndex);
                    dataStringBuilder.append(content).append("\n");
                } while (cursor.moveToNext());
            } else {
                showToast("Column 'content' not found in the database.");
            }
        }

        cursor.close();
        return dataStringBuilder.toString();
    }
}