package com.example.whatsdownapp;

import android.content.Intent;
import android.os.Bundle;

import com.example.whatsdownapp.model.ChatGroupModel;
import com.example.whatsdownapp.utils.AndroidUtil;
import com.example.whatsdownapp.utils.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.whatsdownapp.databinding.ActivityMainBinding;
import com.google.firebase.Timestamp;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;
    ImageButton newGroupBtn;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;
    GroupFragment groupFragment;
    ChatGroupModel chatGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();
        groupFragment = new GroupFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.main_search_btn);
        newGroupBtn = findViewById(R.id.plus_btn);

        searchButton.setOnClickListener((v) -> {
            startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
        });

        newGroupBtn.setOnClickListener((v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_create_group, null);
            builder.setView(dialogView);

            EditText input = dialogView.findViewById(R.id.group_name_input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String groupName = input.getText().toString().trim();
                if (!groupName.isEmpty()) {
                    createGroup(groupName);
                } else {
                    AndroidUtil.showToast(MainActivity.this, "Please enter a group name");
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.menu_chat){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment ).commit();
                }
                if(item.getItemId()==R.id.menu_profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment ).commit();
                }
                if (item.getItemId() == R.id.group_menu) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, groupFragment).commit();
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);
    }

    void createGroup(String name) {
        String groupId = FirebaseUtil.getGroupChatId(name);

        chatGroup = new ChatGroupModel(
                groupId,
                name,
                Arrays.asList(FirebaseUtil.currentUserId()),
                Timestamp.now()
        );

        FirebaseUtil.getGroupChatsReference(groupId).set(chatGroup)
                .addOnSuccessListener(aVoid -> {
                    AndroidUtil.showToast(MainActivity.this, "Successfully created new Group");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, groupFragment).commit();

                })
                .addOnFailureListener(e -> AndroidUtil.showToast(MainActivity.this, "Failed to create group: " + e.getMessage()));


    }


}