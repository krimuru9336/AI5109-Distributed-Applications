package com.example.buddyapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText emailet,passet;
    Button register_btn,login_btn;
    CheckBox checkBox;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailet=findViewById(R.id.login_email);
        passet=findViewById(R.id.login_pass);
        register_btn=findViewById(R.id.login_to_signup);
        login_btn=findViewById(R.id.button_login);
        checkBox=findViewById(R.id.login_checkbox);
        progressBar=findViewById(R.id.progressbar_login);
        mAuth=FirebaseAuth.getInstance();

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    passet.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    passet.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailet.getText().toString();
                String pass=passet.getText().toString();
                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass) ){
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendtoMain();
                                progressBar.setVisibility(View.INVISIBLE);
                            }else{
                                String error= task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error="+error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this,"Please Fill all the Fields",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendtoMain() {
        Intent intent= new Intent(LoginActivity.this,SplashActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user !=null){
            sendtoMain();
        }
    }
}
