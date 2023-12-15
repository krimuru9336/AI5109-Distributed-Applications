package com.example.studentsinfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText name_input;
    Button add_button;
    Button retrieve_button;

    private EditText id;
    private Button insert;
    private TextView info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name_input = findViewById(R.id.name_input);
        add_button = findViewById(R.id.add_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(MainActivity.this);
                myDB.addInfo(name_input.getText().toString().trim());
            }
        });

//        id = (EditText) findViewById(R.id.name_input);
//        insert = (Button) findViewById(R.id.add_button);
////        info = (TextView) findViewById(R.id.textView4);
//
//        insert.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int ID = Integer.parseInt(id.getText().toString());
//                info.setText("The id is " + String.valueOf(ID));
//            }
//        });

//        recyclerView = findViewById(R.id.recyclerView);
        retrieve_button = (Button) findViewById(R.id.retrieve_button);
        retrieve_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
    }
}