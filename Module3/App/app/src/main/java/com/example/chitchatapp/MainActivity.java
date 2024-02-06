package com.example.chitchatapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private SocketHelper socketHelper;
    private Socket socket;
    private EditText usernameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        socketHelper = SocketHelper.getInstance(getApplicationContext());
        socket = socketHelper.getSocket();

        socket.on("userExists", onUserAvailable(this));
        socket.on(Socket.EVENT_CONNECT, args -> Log.d("SocketConnection", "Socket connected successfully"));
        socket.on(Socket.EVENT_CONNECT_ERROR, args -> Log.e("SocketConnection", "Socket connection error: " + args[0]));

        usernameEditText = findViewById(R.id.usernameEditText);
        Button connectButton = findViewById(R.id.connectButton);

        connectButton.setOnClickListener(this::checkUsername);
    }

    private void checkUsername(View view) {
        String username = usernameEditText.getText().toString();

        if (!username.isEmpty()) {
            if (!socketHelper.isConnected()) {
                Toast.makeText(view.getContext(), "Can't connect to server!", Toast.LENGTH_LONG).show();
                return;
            }

            socketHelper.checkUsername(username);
        } else {
            Toast.makeText(view.getContext(), "Please enter a name!", Toast.LENGTH_LONG).show();
        }
    }

    private Emitter.Listener onUserAvailable(MainActivity mainActivity) {
       return args -> {
            try {
                JSONObject jsonObject = (JSONObject) args[0];

                boolean exists = jsonObject.getBoolean("data");
                String action = jsonObject.getString("action");
                String username = jsonObject.getString("name");

                if (!exists) {
                    nextActivity(username);
                } else {
                    Toast.makeText(mainActivity, "Name '" + username + "' already taken.", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public void nextActivity(String username) {
        Intent intent = new Intent(MainActivity.this, ChatOverviewActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
        socketHelper.preventDisconnectOnActivityChange();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socketHelper != null) {
            socketHelper.disconnect();
        }
    }
}
