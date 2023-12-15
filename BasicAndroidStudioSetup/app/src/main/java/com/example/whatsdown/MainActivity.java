package com.example.whatsdown;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.example.whatsdown.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        dbHelper = new DBHelper(this);
    }

    public void onStoreClick(View v) {
        storeData();
    }

    public void onRetrieveClick(View v) {
        retrieveData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void storeData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        EditText editText = findViewById(R.id.editText);

        values.put("value", editText.getText().toString());

        long newRowId = db.insert("mytable", null, values);

        db.close();

        Toast.makeText(this, "Data stored successfully", Toast.LENGTH_SHORT).show();
    }

    private void retrieveData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM mytable ORDER BY id DESC LIMIT 1", null);

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String value = cursor.getString(cursor.getColumnIndex("value"));
            Toast.makeText(this, "Retrieved value: " + value, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }

}