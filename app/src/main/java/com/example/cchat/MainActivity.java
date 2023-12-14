package com.example.cchat;
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

    private EditText nameEditText;
    private Button saveButton;
    private Button retrieveButton;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameEditText = findViewById(R.id.nameEditText);
        saveButton = findViewById(R.id.saveButton);
        retrieveButton = findViewById(R.id.retrieveButton);

        // Open or create the database
        database = openOrCreateDatabase("MyDatabase", MODE_PRIVATE, null);

        // Create the table if not exists
        database.execSQL("CREATE TABLE IF NOT EXISTS Names (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                if (!name.isEmpty()) {
                    // Save the name to the database
                    saveToDatabase(name);
                    Toast.makeText(MainActivity.this, "Name saved to database", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        retrieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve and display the name from the database
                String savedName = retrieveFromDatabase();
                if (savedName != null) {
                    Toast.makeText(MainActivity.this, "Name from database: " + savedName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No name found in the database", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveToDatabase(String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        database.insert("Names", null, values);
    }

    private String retrieveFromDatabase() {
        Cursor cursor = database.rawQuery("SELECT * FROM Names", null);
        String savedName = null;

        if (cursor.moveToLast()) {
            savedName = cursor.getString(cursor.getColumnIndex("name"));
        }

        cursor.close();
        return savedName;
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
