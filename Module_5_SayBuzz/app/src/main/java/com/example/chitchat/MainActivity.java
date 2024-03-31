package com.example.chitchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import Models.AllMethods;
import Models.User;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    UserPreferences preferences;

    EditText email, password;

    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        preferences = new UserPreferences(getApplicationContext());
        AllMethods.preferences = preferences;
        database = FirebaseDatabase.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            Intent i = new Intent(MainActivity.this,ChatRoomActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            MainActivity.this.finish();
        }else {
            if(!preferences.getEmail().equals("")){
                firebaseAuth.signInWithEmailAndPassword(preferences.getEmail(), preferences.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent i = new Intent(MainActivity.this,ChatRoomActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            MainActivity.this.finish();
                        }else {
                            setContentView(R.layout.activity_main);
                            email = findViewById(R.id.loginEmail);
                            password = findViewById(R.id.loginPassword);
                        }

                    }
                });
            }else {
                setContentView(R.layout.activity_main);
                email = findViewById(R.id.loginEmail);
                password = findViewById(R.id.loginPassword);
            }
        }
    }
    public  void  goToRegister(View view){
        Intent i = new Intent(MainActivity.this, registerActivity.class);
        startActivity(i);
    }
    public  void  Login(View view){
            String userEmail = email.getText().toString();
            String userPassword = password.getText().toString();
        if(!userEmail.equals("") && !userPassword.equals("")){
            firebaseAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser u = firebaseAuth.getCurrentUser();
                        preferences.saveCredentials(userEmail,userPassword);
                        Toast.makeText(getApplicationContext(),"Log in Successful",Toast.LENGTH_SHORT);
                        database.getReference("Users").child(u.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User us = snapshot.getValue(User.class);
                                us.setUid(u.getUid());
                                AllMethods.name = us.getName();
                                AllMethods.uId = u.getUid();
                                Intent i = new Intent(MainActivity.this,ChatRoomActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                MainActivity.this.finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else {
                        Toast.makeText(getApplicationContext(),"Wrong Credentials",Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    




}