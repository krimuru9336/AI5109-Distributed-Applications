package com.example.chitchatapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class MessageActivity extends AppCompatActivity implements OnDataChangedListener {
    private EditText messageEditText;
    private SocketHelper socketHelper;
    private MessageHelper messageHelper;
    private Socket socket;
    private MessageAdapter messageAdapter;
    private String username = "";
    private String targetUser = "";
    private boolean typing = false;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messageEditText = findViewById(R.id.messageEditText);
        TextView partnerNameView = findViewById(R.id.partnerNameView);

        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("USERNAME");
            targetUser = intent.getStringExtra("TARGET_USER");
            setTitle("Chat with " + targetUser);
            partnerNameView.setText(targetUser);
        }

        socketHelper = SocketHelper.getInstance(getApplicationContext());
        messageHelper = MessageHelper.getInstance();
        socket = socketHelper.getSocket();

        MessageHelper messageHelper = MessageHelper.getInstance();
        messageAdapter = messageHelper.createAdapter(targetUser, this);

        recyclerView = findViewById(R.id.messageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> finish());


    }

    private Emitter.Listener onTyping() {
        return args -> {
        //TODO
        };
    }

    private Emitter.Listener onStopTyping() {
        return args -> {
        //TODO
        };
    }

    public void sendMessage(View view) {
        String messageText = messageEditText.getText().toString();

        if (!TextUtils.isEmpty(messageText)) {
            Message message = new Message(messageText, username, false, MessageType.TEXT);
            socketHelper.sendMessage(targetUser, message);

            messageAdapter.addMessage(message);

            messageEditText.setText("");
        }
    }

    private void addTyping(String username) {
        //TODO
    }

    private void removeTyping(String username) {
        //TODO
    }

    @Override
    public void onDataChanged() {
        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
    }
}

