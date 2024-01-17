package com.da.chitchat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebSocketManager webSocketManager;
    private EditText usernameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Initialize and connect to the WebSocketManager
        webSocketManager = WebSocketManagerSingleton.getInstance(getApplicationContext());
        webSocketManager.connect();

        usernameEditText = findViewById(R.id.usernameEditText);
        Button connectButton = findViewById(R.id.connectButton);

        NameListener<Boolean, String> listener = new UserNameListener();
        webSocketManager.setUserNameListener(listener, this);

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
        Intent intent = new Intent(MainActivity.this, ChatOverviewActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
        webSocketManager.preventDisconnectOnActivityChange();
        finish();
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
