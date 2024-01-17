package com.da.chitchat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MessageActivity extends AppCompatActivity implements OnDataChangedListener {
    private EditText messageEditText;
    private WebSocketManager webSocketManager;
    private MessageAdapter messageAdapter;
    private String targetUser = "";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messageEditText = findViewById(R.id.messageEditText);
        TextView partnerNameView = findViewById(R.id.partnerNameView);

        Intent intent = getIntent();
        if (intent != null) {
            targetUser = intent.getStringExtra("TARGET_USER");
            setTitle("Chat with " + targetUser);
            partnerNameView.setText(targetUser);
        }

        webSocketManager = WebSocketManagerSingleton.getInstance(getApplicationContext());

        UserMessageListener umListener = UserMessageListenerSingleton.getInstance();
        messageAdapter = umListener.createAdapter(targetUser, this);

        recyclerView = findViewById(R.id.messageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> finish());
    }

    public void sendMessage(View view) {
        String messageText = messageEditText.getText().toString();

        if (!TextUtils.isEmpty(messageText)) {
            webSocketManager.sendMessage(targetUser, messageText);

            messageAdapter.addMessage(new Message(messageText, targetUser, false));

            // Clear the message input field
            messageEditText.setText("");
        }
    }

    @Override
    public void onDataChanged() {
        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
    }
}
