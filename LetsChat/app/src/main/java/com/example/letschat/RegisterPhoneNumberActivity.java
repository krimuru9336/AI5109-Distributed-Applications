package com.example.letschat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class RegisterPhoneNumberActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button sendOTPBtn;

    EditText phoneInput;
    CountryCodePicker countryCodePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressBar = findViewById(R.id.register_phone_number_progress);
        phoneInput = findViewById(R.id.register_phone_number);
        sendOTPBtn = findViewById(R.id.register_otp_btn);
        countryCodePicker = findViewById(R.id.register_country_code);

        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        progressBar.setVisibility(View.GONE);

        sendOTPBtn.setOnClickListener((v)->{
            if(!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Phone number is invalid");
                return;
            }
            Intent intent = new Intent(RegisterPhoneNumberActivity.this, RegisterOTPActivity.class);
            intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });
    }
}