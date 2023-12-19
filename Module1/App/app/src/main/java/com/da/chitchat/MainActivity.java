package com.da.chitchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText nameInput;
    NameRepository nameRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameInput = findViewById(R.id.name_input);
        Button buttonSave = findViewById(R.id.insert_button);
        Button buttonRead = findViewById(R.id.get_button);

        nameRepo = new NameRepository(this);

        /*buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNameToDatabase();
            }
        });

        buttonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readAndDisplayFromDatabase();
            }
        });*/
    }

    public void saveNameToDatabase(View view) {
        String name = nameInput.getText().toString();
        if (!name.isEmpty()) {
            nameRepo.saveName(name);
            nameInput.setText(""); // Clear the input field after saving
            Toast.makeText(this, "Name saved to database", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
        }
    }

    public void readAndDisplayFromDatabase(View view) {
        String name = nameRepo.readName();
        if (name != null && !name.equals("")) {
            Toast.makeText(this, "Name from Database:\n" + name, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No names in Database", Toast.LENGTH_SHORT).show();
        }
    }
}