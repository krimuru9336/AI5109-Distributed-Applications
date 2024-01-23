package com.example.mysheetchatda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mysheetchatda.Models.User;
import com.example.mysheetchatda.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/*
- Name: Adrianus Jonathan Engelbracht
-  Matriculation number: 1151826
- Date: 21.01.2024
*/
public class Signup extends AppCompatActivity {

    ActivitySignupBinding binding;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(Signup.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Your account is being created");

        binding.btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!binding.txtUsername.getText().toString().isEmpty()
                        && !binding.txtEmail.getText().toString().isEmpty()
                        && !binding.txtPassword.getText().toString().isEmpty()
                ){
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(binding.txtEmail.getText().toString(), binding.txtPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful()){
                                        User user = new User(binding.txtUsername.getText().toString(), binding.txtEmail.getText().toString(), binding.txtPassword.getText().toString());
                                        //Toast.makeText(signup.this, "u: " + user.getUserName() + "e: " + user.geteMail() + "p: " + user.getPassword(), Toast.LENGTH_SHORT).show();
                                        String id = task.getResult().getUser().getUid();
                                        //Toast.makeText(signup.this, "Id" + id, Toast.LENGTH_SHORT).show();
                                        database.getReference().child("User").child(id).setValue(user);
                                        Toast.makeText(Signup.this, "Success!", Toast.LENGTH_SHORT).show();

                                        if(mAuth.getCurrentUser() != null){
                                            Intent intent = new Intent(Signup.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                    else{
                                        Toast.makeText(Signup.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else{
                    Toast.makeText(Signup.this, "Else Ente", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.txtAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Signup.this, Signin.class);
                startActivity(intent);
            }
        });

    }
}