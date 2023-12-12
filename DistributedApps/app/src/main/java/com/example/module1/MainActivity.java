package com.example.module1;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;
    private TextView displayText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.nameInput);
        Button saveButton = findViewById(R.id.saveButton);
        Button retrieveButton = findViewById(R.id.retrieveButton);
        displayText = findViewById(R.id.displayText);

        dbHelper = new DatabaseHelper(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase();
            }
        });

        retrieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveFromDatabase();
            }
        });
    }

    private void saveToDatabase() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("input_value", inputText.getText().toString());

        long newRowId = db.insert("m1", null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Value saved to database", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error saving to database", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("Range")
    private void retrieveFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("m1", null, null, null, null, null, "id DESC", "1");

        if (cursor != null && cursor.moveToFirst()) {
            String value = cursor.getString(cursor.getColumnIndex("input_value"));
            displayText.setText("Hello dear " + value);
            cursor.close();
        } else {
            displayText.setText("No data in the database");
        }
    }
}
