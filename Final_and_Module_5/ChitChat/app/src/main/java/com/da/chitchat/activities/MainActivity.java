package com.da.chitchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.da.chitchat.database.user.UserRepository;
import com.da.chitchat.models.User;
import com.da.chitchat.singletons.AppContextSingleton;
import com.da.chitchat.R;
import com.da.chitchat.listeners.UserNameListener;
import com.da.chitchat.WebSocketManager;
import com.da.chitchat.singletons.WebSocketManagerSingleton;
import com.da.chitchat.interfaces.NameListener;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private WebSocketManager webSocketManager;
    private EditText usernameEditText;
    private UserRepository userRepo;
    private User curUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        userRepo = new UserRepository(this);

        // Make context available to access String values
        AppContextSingleton.getInstance().initialize(getApplicationContext());

        // Initialize and connect to the WebSocketManager
        webSocketManager = WebSocketManagerSingleton.getInstance(getApplicationContext());
        webSocketManager.connect();

        usernameEditText = findViewById(R.id.usernameEditText);
        Button connectButton = findViewById(R.id.connectButton);

        NameListener<Boolean, String> listener = new UserNameListener();
        webSocketManager.setUserNameListener(listener, this);

        curUser = userRepo.getUser();

        if (curUser != null) {
            webSocketManager.checkUsername(curUser.getUsername(), curUser.getId());
        }

        connectButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString();

            if (!username.isEmpty()) {
                if (!webSocketManager.isConnected()) {
                    Toast.makeText(view.getContext(), "Can't connect to server!", Toast.LENGTH_LONG).show();
                    return;
                }

                webSocketManager.checkUsername(username);
            } else {
                Toast.makeText(view.getContext(), "Please enter a name!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void nextActivity(String username) {
        String uuid;
        if (curUser != null) {
            uuid = curUser.getId();
        } else {
            uuid = saveNameInDatabase(username);
        }
        Intent intent = new Intent(MainActivity.this, ChatOverviewActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("USERID", uuid);
        startActivity(intent);
        webSocketManager.preventDisconnectOnActivityChange();
        finish();
    }

    public String saveNameInDatabase(String name) {
        UUID uuid = UUID.randomUUID();
        if (!name.isEmpty()) {
            userRepo.saveUser(uuid, name);
        }
        return uuid.toString();
    }

    public void showInvalidUsernameToast(String username, MainActivity activity) {
        if (activity != null) {
            activity.runOnUiThread(() ->
                    Toast.makeText(activity, "Name '" + username + "' already taken.",
                            Toast.LENGTH_LONG).show());
        }
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
