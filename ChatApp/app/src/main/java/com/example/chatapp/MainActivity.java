package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.model.UserModel;
import com.example.chatapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;

    ChatFragement chatFragement;
    ProfileFragement profileFragement;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatFragement = new ChatFragement();
        profileFragement = new ProfileFragement();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.main_search_btn);

        searchButton.setOnClickListener((v)->{
            startActivity(new Intent(MainActivity.this,SearchUserActivity.class));
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.menu_chat){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,chatFragement).commit();
                }
                if(item.getItemId()==R.id.menu_profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,profileFragement).commit();
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);
        getFCMToken();
        ImageButton groupChatButton = findViewById(R.id.group_chat_btn);
        groupChatButton.setOnClickListener(this::showGroupChatMenu);



        if (getIntent().hasExtra("group_name") && getIntent().hasExtra("selected_users")) {
            String groupName = getIntent().getStringExtra("group_name");
            ArrayList<Parcelable> parcelableList = getIntent().getParcelableArrayListExtra("selected_users");
            ArrayList<UserModel> selectedUsers = new ArrayList<>();
            for (Parcelable parcelable : parcelableList) {
                selectedUsers.add((UserModel) parcelable);
            }
            createGroupChat(groupName, selectedUsers);
        }
    }

    private void showGroupChatMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.group_chat_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_create_group) {
                startActivity(new Intent(MainActivity.this, SelectUsersActivity.class));
            }
            else if( itemId == R.id.menu_join_group) {

            }
            return true;
        });
        popupMenu.show();
    }

    void getFCMToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                FirebaseUtil.currentUserDetails().update("fcmToken",token);
            }

        });
    }

    private void createGroupChat(String groupName, ArrayList<UserModel> selectedUsers) {
        // Implement logic to create group chat with group name and selected users
        // For demonstration purposes, you can simply show the group name and selected users
        StringBuilder selectedUsersText = new StringBuilder("Group Name: " + groupName + "\n");
        selectedUsersText.append("Selected Users:\n");
        for (UserModel user : selectedUsers) {
            selectedUsersText.append(user.getUsername()).append("\n");
        }
        Toast.makeText(this, selectedUsersText.toString(), Toast.LENGTH_SHORT).show();


    }
}
