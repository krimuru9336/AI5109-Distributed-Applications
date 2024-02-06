package com.example.chatstnr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatstnr.utils.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    String phone_number;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;

    EditText otpInput;
    Button verifyBtn;
    ProgressBar progressBar;
    TextView resendOtp;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyotp);

        otpInput = findViewById(R.id.verifyotp_otp);
        verifyBtn = findViewById(R.id.verifyotp_btn);
        progressBar = findViewById(R.id.login_progressbar);
        resendOtp = findViewById(R.id.verifyotp_resend);

        phone_number = getIntent().getExtras().getString("phone");

        sendOTP(phone_number, false);

        verifyBtn.setOnClickListener(v -> {
            String otpUser = otpInput.getText().toString();
            PhoneAuthCredential credentials = PhoneAuthProvider.getCredential(verificationCode, otpUser);
            signin(credentials);
            setInProgress(true);
        });

        resendOtp.setOnClickListener(v -> {
            sendOTP(phone_number, true);
        });
    }

    void sendOTP(String phonenumber, boolean isResend){
        setInProgress(true);

        startResendTimer();

        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phonenumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signin(phoneAuthCredential);
                                setInProgress(false);

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showToast(getApplicationContext(), "OTP Verification Failed");
                                setInProgress(false);

                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                setInProgress(false);

                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndroidUtil.showToast(getApplicationContext(), "OTP Sent Successfully");

                            }
                        });

        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());

        }

    }

    void setInProgress(boolean inProgress){

        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            verifyBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            verifyBtn.setVisibility(View.VISIBLE);

        }
    }

    void signin(PhoneAuthCredential phoneAuthCredential){

        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(VerifyOTPActivity.this, UsernameActivity.class);
                    intent.putExtra("phone", phone_number);
                    startActivity(intent);
                }else{
                    AndroidUtil.showToast(getApplicationContext(), "Invalid OTP.");
                    progressBar.setVisibility(View.GONE);
                    verifyBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    void startResendTimer(){
        resendOtp.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                timeoutSeconds--;
                resendOtp.setText("Resend OTP in " + timeoutSeconds + " seconds");
                if(timeoutSeconds<=0){
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        resendOtp.setEnabled(true);
                    });
                }

            }
        }, 0, 1000);
    }
}