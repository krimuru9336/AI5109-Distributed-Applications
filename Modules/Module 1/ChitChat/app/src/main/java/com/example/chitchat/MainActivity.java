package com.example.chitchat;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText email = findViewById(R.id.editTextEmail);
        EditText password = findViewById(R.id.editTextPassword);
        Button login = findViewById(R.id.buttonLogin);
        Button retrieve = findViewById(R.id.buttonRetrieveValue);

        DbConnection dbCon = new DbConnection(this);
        login.setOnClickListener((v)->{
            dbCon.insertUser(email.getText().toString(),password.getText().toString());
        });

        retrieve.setOnClickListener((v)->{
            Cursor userCursor =  dbCon.getUserByEmail(email.getText().toString());

            if (userCursor != null && userCursor.moveToFirst()) {

                int emailColumnIndex = userCursor.getColumnIndex(DbConnection.COLUMN_EMAIL);
                int passwordColumnIndex = userCursor.getColumnIndex(DbConnection.COLUMN_PASSWORD);

                String email_res = userCursor.getString(emailColumnIndex);
                String password_res = userCursor.getString(passwordColumnIndex);

                Toast.makeText(getApplicationContext(), "Email: " + email_res + "\nPassword: " + password_res, Toast.LENGTH_SHORT).show();
            }


            if (userCursor != null) {
                userCursor.close();
            }

        });



    }

    public void showToastFn(View view){
        Toast.makeText(this,"Hallo!! clicked",Toast.LENGTH_SHORT).show();
    }




}