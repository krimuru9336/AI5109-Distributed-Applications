/*
* Rishabh Goswami
* fdai7680
* 1455991
* */

package com.example.whatsdown;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.whatsdown.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
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
    private Button saveButton;
    private Button retrieveButton;
    private DBHelper dbHelper;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        saveButton = findViewById(R.id.saveButton);
        retrieveButton = findViewById(R.id.retrieveButton);

        dbHelper = new DBHelper(this);

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
        String text = editText.getText().toString();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TEXT, text);

        long newRowId = db.insert(DBHelper.TABLE_NAME, null, values);

        Toast.makeText(this, "Data saved to DB with ID " + newRowId, Toast.LENGTH_SHORT).show();
    }

    private void retrieveFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {DBHelper.COLUMN_TEXT};
        Cursor cursor = db.query(DBHelper.TABLE_NAME, projection, null, null, null, null,  DBHelper.COLUMN_ID + " DESC", "1");

        if (cursor.moveToFirst()) {
            String retrievedText = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TEXT));
            Toast.makeText(this, "Hello, Dear " + retrievedText, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Database is empty", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }
}






