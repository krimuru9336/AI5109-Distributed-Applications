// Sven Schickentanz - fdai7287
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

/**
 * The main activity of the ChitChat application.
 * Allows user to enter a username and connect to the chat server.
 */
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

        // Set the listener for the WebSocketManager to check if the username is valid
        NameListener<Boolean, String> listener = new UserNameListener();
        webSocketManager.setUserNameListener(listener, this);

        // Check if the user has already entered a username
        curUser = userRepo.getUser();

        if (curUser != null) {
            // If the user has already entered a username, check if it is valid
            webSocketManager.checkUsername(curUser.getUsername(), curUser.getId());
        }

        // Set the listener for the connect button
        connectButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString();

            // Check if the username is valid
            if (!username.isEmpty()) {
                // Check if the WebSocketManager is connected to the server
                if (!webSocketManager.isConnected()) {
                    Toast.makeText(view.getContext(), "Can't connect to server!", Toast.LENGTH_LONG).show();
                    return;
                }

                webSocketManager.checkUsername(username);
            } else {
                // Display a toast if the user has not entered a username
                Toast.makeText(view.getContext(), "Please enter a name!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Method to start the ChatOverviewActivity.
     * Passes the username and user ID to the next activity.
     *
     * @param username The username entered by the user
     */
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
        // Prevent the WebSocketManager from disconnecting when the activity changes
        webSocketManager.preventDisconnectOnActivityChange();
        // Finish the current activity
        finish();
    }

    /**
     * Method to save the username in the database.
     * Generates a UUID for the user and saves the username and UUID in the database.
     *
     * @param name The username entered by the user
     * @return The UUID generated for the user
     */
    public String saveNameInDatabase(String name) {
        UUID uuid = UUID.randomUUID();
        if (!name.isEmpty()) {
            userRepo.saveUser(uuid, name);
        }
        return uuid.toString();
    }

    /**
     * Method to show a toast message when the username entered by the user is taken.
     *
     * @param username The username that was clicked
     * @param activity The current activity
     */
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
