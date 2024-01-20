package com.example.distributedapplicationsproject.activities;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import com.example.distributedapplicationsproject.R;
import com.example.distributedapplicationsproject.firebase.DatabaseService;
import com.example.distributedapplicationsproject.fragments.ChatFragment;
import com.example.distributedapplicationsproject.utils.DataShare;
import com.example.distributedapplicationsproject.utils.Utils;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {

    TextView textAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textAccount = findViewById(R.id.text_account);

        textAccount.setText(DataShare.getInstance().getCurrentUser().getName());

        ChatFragment chatFragment = new ChatFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_chat, chatFragment);
        transaction.commit();
    }
}
