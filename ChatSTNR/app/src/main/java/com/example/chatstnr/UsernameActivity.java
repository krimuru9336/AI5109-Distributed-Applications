package com.example.chatstnr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.chatstnr.models.UserModel;
import com.example.chatstnr.utils.AndroidUtil;
import com.example.chatstnr.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class UsernameActivity extends AppCompatActivity {

    EditText usernameInput;
    Button registerBtn;
    ProgressBar progressBar;
    String phone_number;
    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        usernameInput = findViewById(R.id.username_name);
        registerBtn = findViewById(R.id.username_register);
        progressBar = findViewById(R.id.login_progressbar);

        phone_number = getIntent().getExtras().getString("phone");
        getUsername();

        registerBtn.setOnClickListener(v -> {
          setUsername();
        });
    }

    void setUsername(){
        setInProgress(false);
        String username = usernameInput.getText().toString();
        if(username.isEmpty() || username.length() < 3){
            usernameInput.setError("Username should be at least 3 characters");
            return;
        }

        if(userModel != null){
            userModel.setUsername(username);
        }else{
            userModel = new UserModel(phone_number, username, Timestamp.now(), FirebaseUtil.currentUserid());
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(UsernameActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }
    void getUsername(){
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    userModel = task.getResult().toObject(UserModel.class);
                    if(userModel != null){
                        usernameInput.setText(userModel.getUsername());
                    }
                }else{
                    AndroidUtil.showToast(getApplicationContext(), "Error in getUsername.");
                }
            }
        });

    }

    void setInProgress(boolean inProgress){

        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            registerBtn.setVisibility(View.VISIBLE);

        }
    }

}