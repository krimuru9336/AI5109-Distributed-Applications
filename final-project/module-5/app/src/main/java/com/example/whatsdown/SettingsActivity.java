package com.example.whatsdown;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.HashMap;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView settingsImage;
    private EditText settingsName, settingsStatus;
    private Button settingsButton;
    private String userId;
    private FirebaseAuth mAuth;
    private DatabaseReference root;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Settings");

        InitializeFields();
        settingsName.setVisibility(View.INVISIBLE);

        settingsButton.setOnClickListener(settingsButtonOnClick);

        GetUserProfile();
    }

    private void InitializeFields() {
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        root = FirebaseDatabase
                .getInstance("https://whatsdown-7baba-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference();

        settingsImage = (CircleImageView) findViewById(R.id.settings_image);
        settingsName = (EditText) findViewById(R.id.settings_name);
        settingsStatus = (EditText) findViewById(R.id.settings_status);
        settingsButton = (Button) findViewById(R.id.settings_button);
    }

    private final View.OnClickListener settingsButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = settingsName.getText().toString();
            String status = settingsStatus.getText().toString();

            if (TextUtils.isEmpty(name)){
                Toast.makeText(SettingsActivity.this, "A profile name is required", Toast.LENGTH_SHORT).show();
            }

            if (TextUtils.isEmpty(status)){
                Toast.makeText(SettingsActivity.this, "A profile status is required", Toast.LENGTH_SHORT).show();
            }
            else{
                HashMap<String, String> profileMap = new HashMap<>();
                profileMap.put("uid", userId);
                profileMap.put("name", name);
                profileMap.put("status", status);
                root.child("Users").child(userId).setValue(profileMap).addOnCompleteListener(updateSettingsOnComplete);
            }
        }
    };

    private final OnCompleteListener<Void> updateSettingsOnComplete = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()){
                SendUserToMainActivity();
                Toast.makeText(SettingsActivity.this, "Updated", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(SettingsActivity.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final ValueEventListener GetUserProfileListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if ((snapshot.exists()) && (snapshot.hasChild("name") && (snapshot.hasChild("image"))))
            {
                String name = snapshot.child("name").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String image = snapshot.child("image").getValue().toString();

                settingsName.setText(name);
                settingsStatus.setText(status);
            }
            else if ((snapshot.exists()) && (snapshot.hasChild("name")))
            {
                String name = snapshot.child("name").getValue().toString();
                String status = snapshot.child("status").getValue().toString();

                settingsName.setText(name);
                settingsStatus.setText(status);
            }
            else
            {
                settingsName.setVisibility(View.VISIBLE);
                Toast.makeText(SettingsActivity.this, "Please set your profile information", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


    private void GetUserProfile(){
        root.child("Users").child(userId).addValueEventListener(GetUserProfileListener);
    }

    private void SendUserToMainActivity() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}