package com.example.myapplication6;

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

    private EditText editText;  // userinput
    private Button insertButton, retrieveButton;  //buttons
    private databaseService dbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//initialize layout
        editText = findViewById(R.id.editText);
        insertButton = findViewById(R.id.button1);
        retrieveButton = findViewById(R.id.button2);


        dbService = new databaseService(this);

        //button actions
        insertButton.setOnClickListener(v -> insertData());

        retrieveButton.setOnClickListener(v -> retrieveData());
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


    private void retrieveData() { // retrieve the latest data based on id
        SQLiteDatabase db = dbService.getReadableDatabase();
        Cursor cursor = db.query("mytable", null, null, null, null, null, "id DESC", "1");

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String data = cursor.getString(cursor.getColumnIndex("data"));
            Toast.makeText(this, "Hello dear " + data, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }


}




