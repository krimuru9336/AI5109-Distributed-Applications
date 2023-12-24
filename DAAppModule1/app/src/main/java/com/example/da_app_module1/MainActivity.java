package com.example.da_app_module1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    DatabaseHelper mDatabaseHelper;
    private Button btnSave, btnGetData;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Show status bar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        editText = (EditText) findViewById(R.id.editText);
        btnSave = (Button) findViewById(R.id.button);
        btnGetData = (Button) findViewById(R.id.button2);
        mDatabaseHelper = new DatabaseHelper(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEntry = editText.getText().toString();
                if (editText.length() != 0) {
                    AddData(newEntry);
                    editText.setText("");
                } else {
                    toastMessage("You must put something in the text field!");
                }

            }
        });

        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TEST","Inside Get Data");
                Cursor data = GetData();
                if (data.moveToNext()) {
                    toastMessage(data.getString(1));
                }

            }
        });
    }

    public void AddData(String newEntry) {
        boolean insertData = mDatabaseHelper.addData(newEntry);

        if (insertData) {
            toastMessage("Data Successfully Inserted!");
        } else {
            toastMessage("Something went wrong");
        }
    }

    public Cursor GetData() {
        Cursor fetchedData = mDatabaseHelper.getData();

        if(fetchedData != null) {
            return fetchedData;
        } else {
            return null;
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this, "Toast Message: " + message, Toast.LENGTH_SHORT).show();
    }
}