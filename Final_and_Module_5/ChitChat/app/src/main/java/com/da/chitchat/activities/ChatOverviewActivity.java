// Sven Schickentanz - fdai7287
package com.da.chitchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.da.chitchat.DialogHelper;
import com.da.chitchat.Message;
import com.da.chitchat.R;
import com.da.chitchat.UserMessageStore;
import com.da.chitchat.adapters.GroupAdapter;
import com.da.chitchat.adapters.UserAdapter;
import com.da.chitchat.listeners.GroupListListener;
import com.da.chitchat.listeners.UserListListener;
import com.da.chitchat.WebSocketManager;
import com.da.chitchat.singletons.AppContextSingleton;
import com.da.chitchat.singletons.WebSocketManagerSingleton;
import com.da.chitchat.database.messages.MessageRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the activity that displays the chat overview / user selection screen.
 */
public class ChatOverviewActivity extends AppCompatActivity {

    private WebSocketManager webSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        TextView userNameTextView = findViewById(R.id.ownNameTextView);

        AppContextSingleton.getInstance().initialize(getApplicationContext());

        MessageRepository messageDB = new MessageRepository(this);

        // Initialize the user list and group list adapters for the RecyclerViews
        List<String> userList = new ArrayList<>();
        UserAdapter userAdapter = new UserAdapter(userList);

        List<String> groups = new ArrayList<>();
        GroupAdapter groupAdapter = new GroupAdapter(groups);

        // Set up the RecyclerViews
        RecyclerView recyclerViewGroups = findViewById(R.id.groupRecyclerView);
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewGroups.setAdapter(groupAdapter);

        RecyclerView recyclerViewUsers = findViewById(R.id.userListRecyclerView);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);

        // Retrieve the username passed from the previous activity
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        String uuid = intent.getStringExtra("USERID");
        userNameTextView.setText(getString(R.string.hello_user, username));

        // Load all messages from the database and add them to the UserMessageStore
        List<Message> messages = messageDB.getAllMessages();
        for (Message msg : messages) {
            if (msg.getChatGroup() != null) {
                UserMessageStore.addMessageToGroup(msg.getChatGroup(), msg);
            } else {
                UserMessageStore.addMessageToUser(msg.getSender(), msg);
            }
        }

        // Initialize and connect to the WebSocketManager
        webSocketManager = WebSocketManagerSingleton.getInstance(getApplicationContext());

        // Register the user with the entered username
        webSocketManager.registerUser(username, uuid);

        // Set up the listeners for the WebSocketManager
        webSocketManager.setUserListListener(new UserListListener(userAdapter));

        webSocketManager.setGroupListener(new GroupListListener(groupAdapter), this);

        webSocketManager.getOfflineMessages(username, uuid);

        webSocketManager.getGroups(username);
    }

    /**
     * Method to create a dialog for the user to enter a group name.
     *
     * @param view The current view
     */
    public void createGroupDialog(View view) {
        DialogHelper.showInputDialog(this, null, getString(R.string.group_dialog_title),
                getString(R.string.group_dialog_text), this::groupNameSelected);
    }

    /**
     * Method to handle the group name entered by the user.
     * Send the group name to the WebSocketManager to create a new group.
     *
     * @param groupName The name of the group entered by the user
     */
    private void groupNameSelected(String groupName) {
        if (!groupName.trim().equals("")) {
            webSocketManager.createChatGroup(groupName);
        }
    }

    /**
     * Methods to show a toast message when the group name entered by the user is taken.
     *
     * @param groupName The name of the group that was clicked
     * @param activity The current activity
     */
    public void showInvalidGroupName(String groupName, ChatOverviewActivity activity) {
        activity.runOnUiThread(() ->
                    Toast.makeText(activity, "Group '" + groupName + "' already taken.",
                            Toast.LENGTH_LONG).show());
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
