package com.example.chitchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import Models.AllMethods;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;

    UserPreferences preferences;

    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        auth = FirebaseAuth.getInstance();
        preferences = new UserPreferences(getApplicationContext());
        AllMethods.preferences = preferences;



        if(auth.getCurrentUser() != null){
            Intent i = new Intent(MainActivity.this,ChatRoomActivity.class);
            startActivity(i);
        }else {
            if(!preferences.getEmail().equals("")){
                auth.signInWithEmailAndPassword(preferences.getEmail(), preferences.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent i = new Intent(MainActivity.this,ChatRoomActivity.class);
                            startActivity(i);
                        }else {
                            setContentView(R.layout.activity_main);
                            email = findViewById(R.id.editTextEmail);
                            password = findViewById(R.id.editTextPassword);
                        }

                    }
                });
            }else {
                setContentView(R.layout.activity_main);
                email = findViewById(R.id.editTextEmail);
                password = findViewById(R.id.editTextPassword);
            }
        }
    }

    public  void  Login(View view){
            String emailtext = email.getText().toString();
            String passwordtext = password.getText().toString();
        if(!emailtext.equals("") && !passwordtext.equals("")){
            auth.signInWithEmailAndPassword(emailtext,passwordtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        preferences.saveCredentials(emailtext,passwordtext);
                        Toast.makeText(getApplicationContext(),"Logged in",Toast.LENGTH_SHORT);
                        Intent i = new Intent(MainActivity.this,ChatRoomActivity.class);
                        startActivity(i);
                    }else {
                        Toast.makeText(getApplicationContext(),"Wrong Email/Password",Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    public  void  goToRegister(View view){
        Intent i = new Intent(MainActivity.this,register.class);
        startActivity(i);
    }




}