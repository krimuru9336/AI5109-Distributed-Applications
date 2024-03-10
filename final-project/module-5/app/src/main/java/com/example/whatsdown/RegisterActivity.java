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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference root;
    private EditText registerEmail, registerPassword;
    private Button registerButton;
    private TextView alreadyRegisteredLink;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InitializeFields();

        registerButton.setOnClickListener(registerButtonOnClick);
        alreadyRegisteredLink.setOnClickListener(alreadyRegisteredOnClick);
    }

    private void InitializeFields() {
        mAuth = FirebaseAuth.getInstance();
        root = FirebaseDatabase
                .getInstance("https://whatsdown-7baba-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference();

        registerEmail = (EditText) findViewById(R.id.register_email);
        registerPassword = (EditText) findViewById(R.id.register_password);
        registerButton = (Button) findViewById(R.id.register_button);
        alreadyRegisteredLink = (TextView) findViewById(R.id.already_registered_link);
        progressBar = new ProgressDialog(this);
    }

    private final View.OnClickListener alreadyRegisteredOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SendUserToLoginActivity();
        }
    };

    private void SendUserToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private final View.OnClickListener registerButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Register();
        }
    };

    private void Register(){
        String email = registerEmail.getText().toString();
        String password = registerPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "An email is required", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "A password is required", Toast.LENGTH_SHORT).show();
        }

        else {
            progressBar.setTitle("Registering you");
            progressBar.setMessage("This won't take long");
            progressBar.setCanceledOnTouchOutside(true);
            progressBar.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(registerOnComplete);
        }
    }

    private final OnCompleteListener<AuthResult> registerOnComplete = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()){
                String userId = mAuth.getCurrentUser().getUid();
                root.child("Users").child(userId).setValue("");

                SendUserToMainActivity();
                Toast.makeText(RegisterActivity.this, "You're registered!", Toast.LENGTH_SHORT).show();
                progressBar.dismiss();
            }
            else {
                Toast.makeText(RegisterActivity.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.dismiss();
            }
        }
    };


}