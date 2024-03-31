package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Models.AllMethods;
import Models.User;

public class registerActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference reference;
    EditText email,password,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        setContentView(R.layout.activity_register);

        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        name = findViewById(R.id.name);


    }
    public  void  Register(View view){
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        String userName = name.getText().toString();

        if(!userEmail.equals("") && !userPassword.equals("") && !userName.equals("")){
            auth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = auth.getCurrentUser();
                                User u = new User();
                                u.setEmail(userEmail);
                                u.setName(userName);
                                reference.child(user.getUid()).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"User registration Successful",Toast.LENGTH_SHORT);
                                            AllMethods.uId = user.getUid();
                                            AllMethods.name = userName;
                                            AllMethods.preferences.saveCredentials(userEmail,userPassword);
                                            finish();
                                            Intent intent = new Intent(registerActivity.this,ChatRoomActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            registerActivity.this.finish();
                                        }else {
                                            Toast.makeText(getApplicationContext(),"Registration Unsuccessful",Toast.LENGTH_SHORT);
                                        }
                                    }
                                });

                            }
                        }
                    }
            );
        }
    }
    public  void  goToLogin(View view){
        Intent intent = new Intent(registerActivity.this,MainActivity.class);
        startActivity(intent);
    }
}