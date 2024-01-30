package com.da.chitchat;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatOverviewActivity extends AppCompatActivity {

    private WebSocketManager webSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        List<String> userList = new ArrayList<>();
        UserAdapter userAdapter = new UserAdapter(userList);

        RecyclerView recyclerView = findViewById(R.id.userListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        // Retrieve the username passed from the previous activity
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");

        webSocketManager = WebSocketManagerSingleton.getInstance(getApplicationContext());

        // Register the user with the entered username
        webSocketManager.registerUser(username);

        // Set up a listener to receive user list updates
        webSocketManager.setUserListListener(new UserListListener(userAdapter));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Disconnect from the WebSocketManager when the activity is destroyed
        if (webSocketManager != null) {
            webSocketManager.disconnect();
        }
    }
}
