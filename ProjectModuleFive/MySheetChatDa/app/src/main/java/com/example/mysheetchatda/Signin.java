package com.example.mysheetchatda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mysheetchatda.databinding.ActivitySigninBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/*
- Name: Adrianus Jonathan Engelbracht
- Matriculation number: 1151826
- Date: 02.02.2024
*/

public class Signin extends AppCompatActivity {


    ActivitySigninBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(Signin.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Validation in Progress");

        binding.btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){

                if(!binding.txtEmail.getText().toString().isEmpty()
                && !binding.txtPassword.getText().toString().isEmpty()){
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(binding.txtEmail.getText().toString(), binding.txtPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(Signin.this, MainActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(Signin.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(Signin.this, "Enter username and password", Toast.LENGTH_SHORT).show();
                }



            }
        });

        // remember signed in user, when removing app from recent apps
        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(Signin.this, MainActivity.class);
            startActivity(intent);
        }

        binding.txtClickSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Signin.this, Signup.class);
                startActivity(intent);
            }
        });

    }
}