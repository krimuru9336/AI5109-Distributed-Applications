package com.example.chatapplication;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends BaseActivity {

    private EditText mTxtEmail;
    private EditText mTxtPassword;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTxtEmail = findViewById(R.id.txtEmail);
        mTxtPassword = findViewById(R.id.txtPassword);
        final Button mBtnSignUp = findViewById(R.id.btnSignUp);
        final TextView mTxtNewUser = findViewById(R.id.txtNewUser);
        final TextView txtForgetPassword = findViewById(R.id.txtForgetPassword);


        auth = FirebaseAuth.getInstance();

        Utils.setHTMLMessage(mTxtNewUser, getString(R.string.strNewSignUp));


        mBtnSignUp.setOnClickListener(new SingleClickListener() {
            @Override
            public void onClickView(View v) {
                String strEmail = mTxtEmail.getText().toString().trim();
                String strPassword = mTxtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPassword)) {
                    screens.showToast(R.string.strAllFieldsRequired);
                } else {
                    login(strEmail, strPassword);
                }
            }
        });
        mTxtNewUser.setOnClickListener(new SingleClickListener() {
            @Override
            public void onClickView(View v) {
                screens.showCustomScreen(RegisterActivity.class);
            }
        });

    }

    private void login(String email, String password) {
        showProgress();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            hideProgress();
            if (task.isSuccessful()) {
                screens.showClearTopScreen(MainActivity.class);
            } else {
                screens.showToast(R.string.strInvalidEmailPassword);
            }
        }).addOnFailureListener(e -> hideProgress()).addOnCanceledListener(this::hideProgress);
    }

}
