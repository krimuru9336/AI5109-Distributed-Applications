package com.example.studentsinfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    MyDatabaseHelper myDB;
    ArrayList<String> student_id, student_name;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recyclerView);

        myDB = new MyDatabaseHelper(ListActivity.this);
        student_id = new ArrayList<>();
        student_name = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new CustomAdapter(ListActivity.this, student_id, student_name);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ListActivity.this));
    }

    void storeDataInArrays() {
        Cursor cursor = myDB.getAllInfo();
        if(cursor.getCount() == 0) {
            Toast.makeText(this, "No data available", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                student_id.add(cursor.getString(0));
                student_name.add(cursor.getString(1));
            }
        }
    }
}