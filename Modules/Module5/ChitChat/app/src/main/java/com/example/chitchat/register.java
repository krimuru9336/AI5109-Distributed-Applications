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

public class register extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference reference;
    EditText email,password,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        setContentView(R.layout.register);

        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        name = findViewById(R.id.name);


    }

    public  void  Register(View view){
        String emailtext = email.getText().toString();
        String passwordtext = password.getText().toString();
        String nametext = name.getText().toString();

        if(!emailtext.equals("") && !passwordtext.equals("") && !nametext.equals("")){
            auth.createUserWithEmailAndPassword(emailtext,passwordtext).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = auth.getCurrentUser();
                                User u = new User();
                                u.setEmail(emailtext);
                                u.setName(nametext);
                                reference.child(user.getUid()).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            Toast.makeText(getApplicationContext(),"User registered",Toast.LENGTH_SHORT);
                                            AllMethods.uId = user.getUid();
                                            AllMethods.name = nametext;
                                            AllMethods.preferences.saveCredentials(emailtext,passwordtext);
                                            finish();

                                            Intent i = new Intent(register.this,ChatRoomActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            register.this.finish();
                                        }else {
                                            Toast.makeText(getApplicationContext(),"User could not be created",Toast.LENGTH_SHORT);
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
        Intent i = new Intent(register.this,MainActivity.class);
        startActivity(i);
    }
}