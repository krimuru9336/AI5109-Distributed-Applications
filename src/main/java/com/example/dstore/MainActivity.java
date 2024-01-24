package com.example.dstore;

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
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button btnStore, btnRetrieve;
    TextView textView;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        editText = findViewById(R.id.editText);
        btnStore = findViewById(R.id.btnStore);
        btnRetrieve = findViewById(R.id.btnRetrieve);
        textView = findViewById(R.id.textView);

        // Create or open the database
        db = openOrCreateDatabase("MyDatabase", MODE_PRIVATE, null);

        // Create the table if not exists
        db.execSQL("CREATE TABLE IF NOT EXISTS UserData (id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT);");

        btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputData = editText.getText().toString().trim();

                // Insert data into the table
                ContentValues values = new ContentValues();
                values.put("data", inputData);
                db.insert("UserData", null, values);

                Toast.makeText(MainActivity.this, "Data stored successfully", Toast.LENGTH_SHORT).show();
            }
        });

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve data from the table
                Cursor cursor = db.rawQuery("SELECT * FROM UserData ORDER BY id DESC LIMIT 1", null);

                if (cursor.moveToFirst()) {
                    @SuppressLint("Range") String retrievedData = cursor.getString(cursor.getColumnIndex("data"));
                    Toast.makeText(MainActivity.this, "Retrieved Data: " + retrievedData, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }

                cursor.close();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database when the activity is destroyed
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
