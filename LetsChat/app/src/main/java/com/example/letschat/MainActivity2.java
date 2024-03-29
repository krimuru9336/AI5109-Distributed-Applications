package com.example.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.letschat.model.ChatGroup;
import com.example.letschat.util.AndroidUtil;
import com.example.letschat.util.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.Timestamp;

import java.util.Arrays;

public class MainActivity2 extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton searchBtn;
    ImageButton newGroupBtn;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;
    GroupFragment groupFragment;

    ChatGroup chatGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();
        groupFragment = new GroupFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchBtn = findViewById(R.id.search_btn);
        newGroupBtn = findViewById(R.id.plus_btn);

        searchBtn.setOnClickListener((v) -> {
            startActivity(new Intent(MainActivity2.this, SearchUserActivity.class));
        });

        newGroupBtn.setOnClickListener((v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
            View dialogView = LayoutInflater.from(MainActivity2.this).inflate(R.layout.dialog_create_group, null);
            builder.setView(dialogView);

            EditText input = dialogView.findViewById(R.id.group_name_input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String groupName = input.getText().toString().trim();
                if (!groupName.isEmpty()) {
                    createGroup(groupName);
                } else {
                    AndroidUtil.showToast(MainActivity2.this, "Please enter a group name");
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            AlertDialog dialog = builder.create();
            dialog.show();
        });


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.chat_menu) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
                }
                if (item.getItemId() == R.id.profile_menu) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit();

                }
                if (item.getItemId() == R.id.group_menu) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, groupFragment).commit();
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.chat_menu);

    }

    void createGroup(String name) {
        String groupId = FirebaseUtil.getGroupChatId(name);

        chatGroup = new ChatGroup(
                groupId,
                name,
                Arrays.asList(FirebaseUtil.currentUserId()),
                Timestamp.now()
        );

        FirebaseUtil.getGroupChatsReference(groupId).set(chatGroup)
                .addOnSuccessListener(aVoid -> {
                    AndroidUtil.showToast(MainActivity2.this, "Successfully created new Group");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, groupFragment).commit();

                })
                .addOnFailureListener(e -> AndroidUtil.showToast(MainActivity2.this, "Failed to create group: " + e.getMessage()));


    }
}