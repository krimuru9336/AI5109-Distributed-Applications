package com.example.chitchat;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatUsersActivity extends AppCompatActivity {
    private WebSocketHandler webSocketHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");

        TextView usersOnlineTextView = findViewById(R.id.usersOnlineTextView);

        List<String> ul = new ArrayList<>();
        UserAdapter userAdapter = new UserAdapter(ul,usersOnlineTextView);

        RecyclerView recyclerView = findViewById(R.id.userListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        TextView myUsernameTextView = findViewById(R.id.myUsernameTextView);
        String text = "Welcome "+username+"!";
        myUsernameTextView.setText(text);

        webSocketHandler = WebSocketHandler.getInstance(getApplicationContext());
        webSocketHandler.registerUser(username);
        webSocketHandler.setUserListener(new UserListener(userAdapter));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (webSocketHandler != null) {
            webSocketHandler.disconnect();
        }
    }
}
