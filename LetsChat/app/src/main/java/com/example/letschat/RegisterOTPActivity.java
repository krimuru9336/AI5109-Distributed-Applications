package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letschat.util.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class RegisterOTPActivity extends AppCompatActivity {

    String phoneNumber;
    EditText otpInput;
    Button nextBtn;
    ProgressBar progressBar;
    FirebaseAuth auth;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    TextView resendOTPText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);

        otpInput = findViewById(R.id.register_otp);
        nextBtn = findViewById(R.id.register_otp_btn_next);
        progressBar = findViewById(R.id.register_otp_progress);
        resendOTPText = findViewById(R.id.register_resend_otp);
        auth = FirebaseAuth.getInstance();


        phoneNumber = Objects.requireNonNull(getIntent().getExtras()).getString("phone");
        sendOTP(phoneNumber, false);

        nextBtn.setOnClickListener( v->{
            String enteredOtp = otpInput.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
            signIn(credential);
            setInProgress(true);
                });
        resendOTPText.setOnClickListener((v)->{
            sendOTP(phoneNumber, true);
        });

    }

    void sendOTP(String phoneNumber, boolean isResend) {
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        AndroidUtil.showToast(getApplicationContext(), "OTP verification success");
                        signIn(phoneAuthCredential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        System.out.println(e.getLocalizedMessage());
                        AndroidUtil.showToast(getApplicationContext(), "OTP verification failed");
                        setInProgress(false);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        resendingToken = forceResendingToken;
                        AndroidUtil.showToast(getApplicationContext(), "OTP sent successfully");
                        setInProgress(false);
                    }
                });

        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential authCredential) {
        setInProgress(true);
        auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(RegisterOTPActivity.this, RegisterUserActivity.class);
                    intent.putExtra("phone", phoneNumber);
                    startActivity(intent);
                }else{
                    AndroidUtil.showToast(getApplicationContext(), "OTP Verification failed");
                }
            }
        });

    }

    void startResendTimer(){
        resendOTPText.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds --;
                resendOTPText.setText("Resend OTP in "+ timeoutSeconds + " seconds");
                if(timeoutSeconds <= 0){
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(()->{
                        resendOTPText.setEnabled(true);
                    });
                }
            }
        },0,1000);

    }
}