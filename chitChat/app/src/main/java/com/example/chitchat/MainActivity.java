package com.example.chitchat;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        dbHelper = new DbHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Azamat Afzalov - 1492864");
        toolbar.setTitleTextColor(Color.WHITE);

    }

    public void sendData(View view) {
        String inputText = editText.getText().toString();
        long result = dbHelper.insertText(inputText);
        if (result != -1) {
            Toast.makeText(MainActivity.this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Error inserting data", Toast.LENGTH_SHORT).show();
        }

    }

    public void getData(View view) {
        Cursor cursor = dbHelper.getLastInsertedData();

        if (cursor != null && cursor.getCount() > 0) {
            int columnIndex = cursor.getColumnIndex(DbHelper.COLUMN_TEXT);
            String lastInsertedText = cursor.getString(columnIndex);
            Toast.makeText(MainActivity.this, "Last Inserted Data: " + lastInsertedText, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}