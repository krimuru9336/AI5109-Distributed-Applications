package com.example.prototype;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText inputEditText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputEditText = findViewById(R.id.inputEditText);
        Button saveButton = findViewById(R.id.saveButton);
        Button retrieveButton = findViewById(R.id.retrieveButton);

        dbHelper = new DatabaseHelper(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase(inputEditText.getText().toString());
                inputEditText.getText().clear();
            }
        });

        retrieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveFromDatabase();
            }
        });
    }

    private void saveToDatabase(String text) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, text);
        db.insert(DatabaseHelper.TABLE_NAME, null, values);
        Toast.makeText(this, "Name saved", Toast.LENGTH_SHORT).show();
        db.close();
    }

    private void retrieveFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                new String[]{DatabaseHelper.COLUMN_NAME},
                null,
                null,
                null,
                null,
                "timestamp DESC", // Order by timestamp in descending order (most recent first)
                "1" // Limit to one row
        );

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);

            if (columnIndex != -1) {
                String text = cursor.getString(columnIndex);
                Toast.makeText(this, "Hello " + text, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No names in DB", Toast.LENGTH_SHORT).show();
            }

            cursor.close();
        } else {
            Toast.makeText(this, "No names in DB", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
}
