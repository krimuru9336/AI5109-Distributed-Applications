package com.example.easychat;//package com.example.easychat;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//}
//

// MainActivity.java
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button saveButton, loadButton;
    private TextView displayTextView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        saveButton = findViewById(R.id.saveButton);
        loadButton = findViewById(R.id.loadButton);
        displayTextView = findViewById(R.id.displayTextView);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDataToFirebase();
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDataFromFirebase();
            }
        });
    }

    private void saveDataToFirebase() {
        String data = editText.getText().toString().trim();
        if (!data.isEmpty()) {
            databaseReference.child("data").setValue(data)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                editText.setText("");
                                displayTextView.setText("Data saved to Firebase");
                            } else {
                                displayTextView.setText("Failed to save data");
                            }
                        }
                    });
        }
    }

    private void loadDataFromFirebase() {
        databaseReference.child("data").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        String data = dataSnapshot.getValue(String.class);
                        displayTextView.setText("Data from Firebase: " + data);
                    } else {
                        displayTextView.setText("No data found in Firebase");
                    }
                } else {
                    displayTextView.setText("Failed to load data");
                }
            }
        });
    }
}

