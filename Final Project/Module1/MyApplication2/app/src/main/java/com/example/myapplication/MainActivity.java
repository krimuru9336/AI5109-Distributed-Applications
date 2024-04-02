package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button insertButton, retrieveButton;
    private TextView toast;
    private databaseService dbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        insertButton = findViewById(R.id.button1);
        retrieveButton = findViewById(R.id.button2);
        toast = findViewById(R.id.toast);

        dbService = new databaseService(this);

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
            }
        });

        retrieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveData();
            }
        });
    }

    private void insertData() {
        SQLiteDatabase db = dbService.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("data", editText.getText().toString());

        long newRowId = db.insert("mytable", null, values);
        if (newRowId != -1) {
            Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
            editText.setText("");
        } else {
            Toast.makeText(this, "Error inserting data", Toast.LENGTH_SHORT).show();
        }
    }

    private void retrieveData() {
        SQLiteDatabase db = dbService.getReadableDatabase();
        Cursor cursor = db.query("mytable", null, null, null, null, null, null);

        StringBuilder result = new StringBuilder();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String data = cursor.getString(cursor.getColumnIndex("data"));
            result.append("Hello dear ").append(data).append("\n");
        }

        if (result.length() > 0) {
            toast.setText(result.toString());
        } else {
            toast.setText("No data found");
        }

        cursor.close();
    }

}




