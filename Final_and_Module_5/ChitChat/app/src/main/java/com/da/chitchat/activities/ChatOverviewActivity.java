package com.da.chitchat.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.da.chitchat.Message;
import com.da.chitchat.R;
import com.da.chitchat.UserMessageStore;
import com.da.chitchat.adapters.UserAdapter;
import com.da.chitchat.listeners.UserListListener;
import com.da.chitchat.WebSocketManager;
import com.da.chitchat.singletons.WebSocketManagerSingleton;
import com.da.chitchat.database.messages.MessageRepository;

import java.util.ArrayList;
import java.util.List;

public class ChatOverviewActivity extends AppCompatActivity {

    private WebSocketManager webSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        MessageRepository messageDB = new MessageRepository(this);

        List<String> userList = new ArrayList<>();
        UserAdapter userAdapter = new UserAdapter(userList);

        RecyclerView recyclerView = findViewById(R.id.userListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        // Retrieve the username passed from the previous activity
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        String uuid = intent.getStringExtra("USERID");

        List<Message> messages = messageDB.getAllMessages();
        for (Message msg : messages) {
            UserMessageStore.addMessageToUser(msg.getSender(), msg);
        }

        webSocketManager = WebSocketManagerSingleton.getInstance(getApplicationContext());

        // Register the user with the entered username
        webSocketManager.registerUser(username, uuid);

        // Set up a listener to receive user list updates
        webSocketManager.setUserListListener(new UserListListener(userAdapter));

        webSocketManager.getOfflineMessages(username, uuid);
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
