package com.example.chitchatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatOverviewActivity extends AppCompatActivity {

    private SocketHelper socketHelper;
    private UserHelper userHelper;
    private GroupHelper groupHelper;
    private MessageHelper messageHelper;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        List<String> userList = new ArrayList<>();
        Map<Integer, String> groupMap = new HashMap<>();
        userHelper = UserHelper.getInstance();
        groupHelper = GroupHelper.getInstance();
        UserAdapter userAdapter = userHelper.createAdapter(userList);
        GroupAdapter groupAdapter = groupHelper.createAdapter(groupMap);

        messageHelper = MessageHelper.getInstance();

        //User Chats
        RecyclerView recyclerView = findViewById(R.id.userListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        //Group Chats
        RecyclerView groupRecyclerView = findViewById(R.id.groupListRecyclerView);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupRecyclerView.setAdapter(groupAdapter);

        //Statically add Group Channels
        groupAdapter.addGroup(0,"GroupChat0");
        groupAdapter.addGroup(1,"GroupChat1");
        groupAdapter.addGroup(2,"GroupChat2");

        // Retrieve the username passed from the previous activity
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");

        socketHelper = SocketHelper.getInstance(getApplicationContext());
        socket = socketHelper.getSocket();

        // Register the user with the entered username
        socketHelper.registerUser(username);

        socket.on("init", onInit());
        socket.on("user", onUser());
        socket.on("message", onMessage());
    }

    private Emitter.Listener onInit() {
        return args -> {
            if (args.length > 0 && args[0] instanceof JSONObject) {
                try {
                    JSONObject jsonObject = (JSONObject) args[0];

                    if (jsonObject.has("data") && jsonObject.has("action")) {
                        JSONArray userArray = jsonObject.getJSONArray("data");

                        List<String> userList = new ArrayList<>();
                        for (int i = 0; i < userArray.length(); i++) {
                            userList.add(userArray.getString(i));
                        }

                        if (userHelper != null) {
                            userHelper.initializeUserList(userList, jsonObject.getString("action"));
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private Emitter.Listener onUser() {
        return args -> {
            if (args.length > 0 && args[0] instanceof JSONObject) {
                try {
                    JSONObject jsonObject = (JSONObject) args[0];

                    if (jsonObject.has("data") && jsonObject.has("action")) {

                        if (userHelper != null) {
                            userHelper.onUser(jsonObject.getString("data"), jsonObject.getString("action"));
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private Emitter.Listener onMessage() {
        return args -> {
            if (args.length > 0 && args[0] instanceof JSONObject) {
                try {
                    JSONObject jsonObject = (JSONObject) args[0];

                    if (jsonObject.has("data") && jsonObject.has("action")) {
                        JSONObject messageObj = jsonObject.getJSONObject("data");
                        Log.d("onMessage", messageObj.toString());

                        if (this.messageHelper != null) {
                            Message msg = Message.fromJSON(messageObj);
                            this.messageHelper.onMessageReceived(msg, jsonObject.getString("action"));
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Disconnect from the WebSocketManager when the activity is destroyed
        if (socketHelper != null) {
            socketHelper.disconnect();
        }
    }
}