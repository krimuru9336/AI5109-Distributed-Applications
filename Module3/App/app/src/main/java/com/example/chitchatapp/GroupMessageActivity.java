package com.example.chitchatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

public class GroupMessageActivity extends MessageActivity implements OnDataChangedListener {
    private String targetGroup = "";
    private int groupId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messageEditText = findViewById(R.id.messageEditText);
        TextView partnerNameView = findViewById(R.id.partnerNameView);

        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("USERNAME");
            targetGroup = intent.getStringExtra("TARGET_GROUP");
            groupId = intent.getIntExtra("GROUP_ID", -1);
            setTitle(targetGroup);
            partnerNameView.setText(targetGroup);
        }

        socketHelper = SocketHelper.getInstance(getApplicationContext());
        messageHelper = MessageHelper.getInstance();
        socket = socketHelper.getSocket();

        messageAdapter = messageHelper.createAdapter(groupId, targetGroup, this, this);

        recyclerView = findViewById(R.id.messageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
        registerForContextMenu(recyclerView);

        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> finish());
    }

    public void sendMessage(View view) {
        String messageText = messageEditText.getText().toString();

        if (!TextUtils.isEmpty(messageText)) {
            Message message = new Message(messageText, username, targetGroup, false, MessageType.TEXT);
            message.setGroupId(groupId);
            Log.d("Send", "Message: " + message.toJSON().toString());
            socketHelper.sendMessage(message, MessageAction.MESSAGE);

            messageAdapter.addGroupMessage(message);

            messageEditText.setText("");
        }
    }

    public void sendMedia(Uri uri) {
        String mediaType = getContentResolver().getType(uri);
        MessageType messageType;
        if (mediaType != null && mediaType.startsWith("video"))
            messageType = MessageType.VIDEO;
        else
            messageType = MessageType.IMAGE;

        Message message = new Message("Pending...", username, targetGroup, false, messageType);
        message.setGroupId(groupId);
        message.setMediaUri(uri);
        message.setMimeType(Base64Converter.getMimeType(this, uri));

        socketHelper.sendMedia(message, MessageAction.MESSAGE);

        messageAdapter.addGroupMessage(message);
    }
}

