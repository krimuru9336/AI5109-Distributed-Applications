package com.example.myapplication5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication5.utils.FirebaseUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

//Signup a new user
public class SignupActivity extends AppCompatActivity {

    EditText signupUsername, signupEmail, signupPassword;
    FirebaseAuth mAuth;
    HelperClass userModel;

    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupUsername = findViewById(R.id.signup_username);

        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        mAuth = FirebaseAuth.getInstance();

        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");


                String username = signupUsername.getText().toString();

                String email = signupEmail.getText().toString();
                String password = signupPassword.getText().toString();


                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    signupEmail.setError("Invalid Email");
                    signupEmail.setFocusable(true);
                } else if (signupPassword.length() < 6) {
                    signupPassword.setError("Length Must be greater than 6 character");
                    signupPassword.setFocusable(true);
                }

                else {
                    registerUser(email, password, username);
                }

            }
        });


        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void registerUser(String email, final String password, final String username) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser user = mAuth.getCurrentUser();
                    String email = user.getEmail();
                    String uid = user.getUid();
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("email", email);
                    hashMap.put("uid", uid);
                    hashMap.put("username", username);

                    if(userModel!=null){
                        userModel.setUsername(username);
                    }else{
                        userModel = new HelperClass(username, email , FirebaseUtil.currentUserId());
                    }

                    FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                } else {

                    Toast.makeText(SignupActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(SignupActivity.this, "Error Occurred", Toast.LENGTH_LONG).show();
            }
        });
    }



}