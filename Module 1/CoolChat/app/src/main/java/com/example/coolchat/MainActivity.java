package com.example.coolchat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.coolchat.DatabaseHelper;
import com.example.coolchat.R;

public class MainActivity extends AppCompatActivity {

    private EditText inputEditText;
    private Button saveButton, retrieveButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);


        inputEditText = findViewById(R.id.input_editText);
        saveButton = findViewById(R.id.saveBtn);
        retrieveButton = findViewById(R.id.retrieveBtn);
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
        String inputValue = inputEditText.getText().toString().trim();

        if (!inputValue.isEmpty()) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.COLUMN_TEXT, inputValue);
            db.insert(DatabaseHelper.TABLE_NAME, null, values);
            db.close();

            // Clear the input field after saving
            inputEditText.setText("");
            showToast("Value saved to database");
        } else {
            showToast("Please enter a value before saving");
        }
    }

    private void retrieveFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {DatabaseHelper.COLUMN_TEXT};

        // Order the query by the primary key in descending order
        String sortOrder = DatabaseHelper.COLUMN_ID + " DESC";

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                "1" // Limit the result to 1 row
        );


        if (cursor != null && cursor.moveToFirst()) {
            String lastSavedValue = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT));
            showToast("Last saved value from database: " + lastSavedValue);
            cursor.close();
        } else {
            showToast("No values found in the database");
        }

        db.close();
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}