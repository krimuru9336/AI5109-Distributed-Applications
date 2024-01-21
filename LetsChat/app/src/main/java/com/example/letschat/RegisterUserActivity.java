package com.example.letschat;

import android.content.Intent;
import android.os.Bundle;

import com.example.letschat.model.User;
import com.example.letschat.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.letschat.databinding.ActivityRegisterUserBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

public class RegisterUserActivity extends AppCompatActivity {

    EditText usernameInput;
    Button registerBtn;
    ProgressBar progressBar;
    String phoneNumber;

    User userModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        usernameInput = findViewById(R.id.register_username);
        registerBtn = findViewById(R.id.register_user_submit_btn);
        progressBar = findViewById(R.id.register_username_progress);

        phoneNumber = Objects.requireNonNull(getIntent().getExtras()).getString("phone");
        getUsername();
        registerBtn.setOnClickListener((v -> {
            setUsername();
        }));


    }

    void getUsername(){
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    userModel = task.getResult().toObject(User.class);
                    if(userModel !=null){
                        usernameInput.setText(userModel.getUsername());
                    }
                }
            }
        });
    }

    void setUsername(){

        String username = usernameInput.getText().toString();
        if(username.isEmpty() || username.length() < 3){
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        setInProgress(true);
        if(userModel !=null){
            userModel.setUsername(username);
        }else{
            userModel = new User(phoneNumber,username, Timestamp.now(), FirebaseUtil.currentUserId());
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(RegisterUserActivity.this, MainActivity2.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            registerBtn.setVisibility(View.VISIBLE);
        }
    }
}