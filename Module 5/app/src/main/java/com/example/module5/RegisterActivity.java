package com.example.module5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    public static final String user_field = "usr";
    public static final String pwd_field = "pwd";
    public static final String MY_PREFS_NAME = "MY_PREFS_NAME";
    Button btnSignIn,btnRegister;
    RelativeLayout rootLayout;
    FirebaseDatabase db;
    DatabaseReference users;

    TextView txtForgotPass;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initial direbase

        db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();



        btnSignIn = findViewById(R.id.btnSignIn);
        btnRegister = findViewById(R.id.btnRegister);
        rootLayout = findViewById(R.id.root_layout);
        txtForgotPass = findViewById(R.id.txt_forgot_password);


        //Event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDilog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDilog();
            }
        });

        //Auto Login System

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String user = prefs.getString(user_field, null);
        String pwd = prefs.getString(pwd_field, null);



        if (user != null && pwd != null)
        {
            if (!TextUtils.isEmpty(user) &&
                    !TextUtils.isEmpty(pwd))
            {
                autoLogin(user,pwd);
            }
        }


    }

    private void autoLogin(String user, String pwd) {
        final SpotsDialog waitingDilog = new SpotsDialog(RegisterActivity.this);
        waitingDilog.show();

        mAuth.signInWithEmailAndPassword(user,pwd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        waitingDilog.dismiss();

                        startActivity(new Intent(RegisterActivity.this,UserListActivity.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDilog.dismiss();
                        Snackbar.make(rootLayout,"Failed: "+e.getMessage(),Snackbar.LENGTH_LONG)
                                .show();
                        btnSignIn.setEnabled(true);
                    }
                });
    }

    private void showLoginDilog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use email to sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login,null);

        final EditText editEmail = login_layout.findViewById(R.id.editEmail);
        final EditText editPassword = login_layout.findViewById(R.id.editPassword);

        dialog.setView(login_layout);

        //set button
        dialog.setPositiveButton("SING IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //set disabale sing in button if it processisng
                btnSignIn.setEnabled(false);

                //check validition
                if (TextUtils.isEmpty(editEmail.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter email address",Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(editPassword.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter password",Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                if (editPassword.getText().toString().length()<6){
                    Snackbar.make(rootLayout,"Password to short..!!",Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                //log in

                final SpotsDialog waitingDilog = new SpotsDialog(RegisterActivity.this);
                waitingDilog.show();

                mAuth.signInWithEmailAndPassword(editEmail.getText().toString(),editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                waitingDilog.dismiss();

                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString(user_field,editEmail.getText().toString() );
                                editor.putString(pwd_field,editPassword.getText().toString() );
                                editor.apply();

                                startActivity(new Intent(RegisterActivity.this,UserListActivity.class));
                                finish();



                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                waitingDilog.dismiss();
                                Snackbar.make(rootLayout,"Failed: "+e.getMessage(),Snackbar.LENGTH_LONG)
                                        .show();
                                btnSignIn.setEnabled(true);
                            }
                        });
            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }



    private void showRegisterDilog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register,null);

        final EditText editEmail = register_layout.findViewById(R.id.editEmail);
        final EditText editPassword = register_layout.findViewById(R.id.editPassword);
        final EditText editName = register_layout.findViewById(R.id.editName);

        dialog.setView(register_layout);
        //set button
        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final SpotsDialog waitingDilog = new SpotsDialog(RegisterActivity.this);
                waitingDilog.show();

                //check validition
                if (TextUtils.isEmpty(editName.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter Name",Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(editEmail.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter email address",Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(editPassword.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter password",Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                if (editPassword.getText().toString().length()<6){
                    Snackbar.make(rootLayout,"Password to short..!!",Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                //register new user

                mAuth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                //save user to db

                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                // Now store the user name and initialize groups
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("userName", editName.getText().toString());
                                userData.put("userId", userId);
                                userData.put("userType", "user");
                                userData.put("userEmail", editEmail.getText().toString());
                                userData.put("userPassword", editPassword.getText().toString());
                                userData.put("groups", new HashMap<>()); // Initialize with an empty map
                                userRef.setValue(userData);

                                dialog.dismiss();
                                waitingDilog.dismiss();

                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString(user_field,editEmail.getText().toString() );
                                editor.putString(pwd_field,editPassword.getText().toString() );
                                editor.apply();

                                Toast.makeText(RegisterActivity.this, "Register Successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this,UserListActivity.class));
                                finish();


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                waitingDilog.dismiss();
                                Snackbar.make(rootLayout,"Failed: "+e.getMessage(),Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        });
            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}