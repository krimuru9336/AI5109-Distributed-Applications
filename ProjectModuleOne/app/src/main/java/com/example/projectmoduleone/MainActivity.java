package com.example.projectmoduleone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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

    private EditText editText;
    private Button btnSave, btnRetrieve;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        btnSave = findViewById(R.id.btnSave);
        btnRetrieve = findViewById(R.id.btnRetrieve);

        database = openOrCreateDatabase("MyDatabase", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS UserData (id INTEGER PRIMARY KEY AUTOINCREMENT, textValue TEXT);");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveData();
            }
        });
    }

    private void saveData() {
        String inputText = editText.getText().toString();
        ContentValues values = new ContentValues();
        values.put("textValue", inputText);
        long result = database.insert("UserData", null, values);

        if (result != -1) {
            showToast("Data saved successfully!");
            editText.setText(""); // Clear the EditText after saving
        } else {
            showToast("Failed to save data");
        }
    }

    private void retrieveData() {
        Cursor cursor = database.query("UserData", null, null, null, null, null, "id DESC", "1");

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("textValue");

            if (columnIndex != -1) {
                String textValue = cursor.getString(columnIndex);
                showToast("Retrieved data: " + textValue);
            } else {
                showToast("Column 'textValue' not found in the result set");
            }
        } else {
            showToast("No data found");
        }

        cursor.close();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        // Close the database when the activity is destroyed
        if (database != null) {
            database.close();
        }
        super.onDestroy();
    }
}
