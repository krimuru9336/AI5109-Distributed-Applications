package com.example.myapplication5;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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


//username auth check new
import com.google.firebase.auth.*;



//Log in
public class LoginActivity extends AppCompatActivity {

//    EditText loginUsername;

    EditText loginEmail, loginPassword;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Button loginButton;
    TextView signupRedirectText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        loginUsername = findViewById(R.id.login_username);
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        mAuth = FirebaseAuth.getInstance();

        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        if (mAuth != null) {
            System.out.println("Auth test" + mAuth.getCurrentUser());
        }


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if (!validateUseremail(email)) {
                    loginEmail.setError("Username cannot be empty");

                }
                else {
                    System.out.println("Clicked login");
                    checkUser(email, password);

                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public Boolean validateUseremail(String email) {


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError("Invalid Email");
            loginEmail.setFocusable(true);
            return false;

        } else {
            loginEmail.setError(null);
            return true;

        }


    }
    public void checkUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

//                    loadingBar.dismiss();
                    FirebaseUser user = mAuth.getCurrentUser();

                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                } else {
//                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_LONG).show();
            }
        });


    }

//
//    void setUsername(){
//
//        String email = loginEmail.getText().toString();
//        if(username.isEmpty() || username.length()<3){
//            usernameInput.setError("Username length should be at least 3 chars");
//            return;
//        }
////        setInProgress(true);
//        if(userModel!=null){
//            userModel.setUsername(username);
//        }else{
//            userModel = new UserModel(email, Timestamp.now(),FirebaseUtil.currentUserId());
//        }
//
//        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
////                setInProgress(false);
//                if(task.isSuccessful()){
//                    Intent intent = new Intent(LoginUsernameActivity.this,MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
//                    startActivity(intent);
//                }
//            }
//        });
//
//    }
//
//    void getUsername(){
////        setInProgress(true);
//        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
////                setInProgress(false);
//                if(task.isSuccessful()){
//                    userModel =    task.getResult().toObject(UserModel.class);
//                    if(userModel!=null){
//                        usernameInput.setText(userModel.getUsername());
//                    }
//                }
//            }
//        });
//    }



//
//    public void checkUser() {
//
//        //get user entered username from UI
//        String userUsername = loginUsername.getText().toString().trim();
//
//        System.out.println("Usercheck in progress-" + userUsername);
//
//        //connect with db to check if entered username exists in db or not
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
//        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);
//
//        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if (snapshot.exists()) {
//
//                    loginUsername.setError(null);
//                    String usernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
//                    System.out.println("Logged in user data from db-" + usernameFromDB);
//                    if (usernameFromDB.equals(userUsername)) {
//                        loginUsername.setError(null);
//
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//
//                        startActivity(intent);
//                    }
//
//                } else {
//                    loginUsername.setError("User does not exist");
//                    loginUsername.requestFocus();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }



}