package com.example.easychat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.HashMap;

public class LoginOtpActivity extends AppCompatActivity {

    String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

    Map<String,String> data = new HashMap<>();
    FirebaseFirestore.getInstance().collection("test").add(data);

    }
}