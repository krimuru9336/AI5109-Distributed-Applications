package com.example.whatsdown;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView forgotPasswordLink, registerHereLink;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitializeFields();

        registerHereLink.setOnClickListener(registerHereOnClick);
        loginButton.setOnClickListener(loginButtonOnClick);
    }

    private void InitializeFields() {
        mAuth = FirebaseAuth.getInstance();

        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.login_button);
        forgotPasswordLink = (TextView) findViewById(R.id.forgot_password_link);
        registerHereLink = (TextView) findViewById(R.id.register_here_link);
        progressBar = new ProgressDialog(this);
    }

    private void SendUserToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private final View.OnClickListener registerHereOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SendUserToRegisterActivity();
        }
    };

    private final View.OnClickListener loginButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = loginEmail.getText().toString();
            String password = loginPassword.getText().toString();

            if (TextUtils.isEmpty(email)){
                Toast.makeText(LoginActivity.this, "An email is required", Toast.LENGTH_SHORT).show();
            }

            if (TextUtils.isEmpty(password)){
                Toast.makeText(LoginActivity.this, "A password is required", Toast.LENGTH_SHORT).show();
            }

            else {
                progressBar.setTitle("Logging you in");
                progressBar.setMessage("This won't take long");
                progressBar.setCanceledOnTouchOutside(true);
                progressBar.show();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(loginOnComplete);
            }
        }
    };

    private final OnCompleteListener<AuthResult> loginOnComplete = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()){
                SendUserToMainActivity();
                Toast.makeText(LoginActivity.this, "You're logged in!", Toast.LENGTH_SHORT).show();
                progressBar.dismiss();
            }
            else {
                Toast.makeText(LoginActivity.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.dismiss();
            }
        }
    };
}