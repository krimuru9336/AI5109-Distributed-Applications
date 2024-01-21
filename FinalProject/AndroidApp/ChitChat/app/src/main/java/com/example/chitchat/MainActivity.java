package com.example.chitchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    TextInputEditText inputMsgDB;
    MessageRepository messageRepository;

    private WebSocketHandler webSocketHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageRepository = new MessageRepository(this);

        webSocketHandler = WebSocketHandler.getInstance(getApplicationContext());
        webSocketHandler.connect();

        Button connectBtn = findViewById(R.id.connectButton);
        NameListener nl = new NameListener();
        webSocketHandler.setNameListener(nl,this);

        connectBtn.setOnClickListener(view -> {
            if (!webSocketHandler.isConnected()) {
                Toast.makeText(view.getContext(), "No server connection...!", Toast.LENGTH_LONG).show();
                return;
            }
            webSocketHandler.getUsername();
        });
    }

    public void nextActivity(String username) {
        Intent intent = new Intent(MainActivity.this, ChatUsersActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
        webSocketHandler.preventDC();
        finish();
    }

    public void showUsernameToast(String username, MainActivity mainActivity) {
        if (mainActivity != null) {
            mainActivity.runOnUiThread(() ->
                    Toast.makeText(mainActivity, "Your username is "+username+"!",
                            Toast.LENGTH_LONG).show());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Disconnect from the WebSocketManager when the activity is destroyed
        if (webSocketHandler != null) {
            webSocketHandler.disconnect();
        }
    }
    public void saveMsg(View view){
        String msg = inputMsgDB.getText().toString();
        if(!msg.isEmpty()){
           messageRepository.saveMessage(msg);
           inputMsgDB.setText("");
            Toast.makeText(this,"Message saved",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Enter a message",Toast.LENGTH_SHORT).show();
        }
    }
    public void readMsg(View view){
        String msg = messageRepository.readMessage();
        if(msg != null && !msg.isEmpty()){
            Toast.makeText(this,"Last message: "+msg,Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"No messages yet",Toast.LENGTH_LONG).show();
        }
    }
}