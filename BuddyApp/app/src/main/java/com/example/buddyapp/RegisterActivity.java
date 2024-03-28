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

public class RegisterActivity extends AppCompatActivity {
    EditText emailet,passet,confirmpasset;
    Button register_btn,login_btn;
    CheckBox checkBox;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailet=findViewById(R.id.register_email);
        passet=findViewById(R.id.register_pass);
        confirmpasset=findViewById(R.id.register_confirm_pass);
        register_btn=findViewById(R.id.button_register);
        login_btn=findViewById(R.id.signup_to_login);
        checkBox=findViewById(R.id.register_checkbox);
        progressBar=findViewById(R.id.progressbar_register);
        mAuth=FirebaseAuth.getInstance();

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    passet.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmpasset.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    passet.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmpasset.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailet.getText().toString();
                String pass=passet.getText().toString();
                String confirmPassword=confirmpasset.getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass) || !TextUtils.isEmpty(confirmPassword) ){
                    if(pass.equals(confirmPassword)){
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    sendtoMain();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }else{
                                    String error= task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this,"Error="+error,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                            }else{
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this,"Password and Confirm Password is not matching",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this,"Please Fill all the Fields",Toast.LENGTH_SHORT).show();
                }
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sendtoMain() {
        Intent intent= new Intent(RegisterActivity.this,SplashActivity.class);
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
