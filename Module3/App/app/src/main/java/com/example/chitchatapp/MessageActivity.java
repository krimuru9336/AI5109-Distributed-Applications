package com.example.chitchatapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

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
        registerForContextMenu(recyclerView);

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
            Message message = new Message(messageText, username, targetUser, false, MessageType.TEXT);
            socketHelper.sendMessage(message, MessageAction.MESSAGE);

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

    public void deleteMessage(Message message) {
        Log.d("ContextLog", "Delete");
        if (!message.isIncoming() && message.getMessageState() != MessageState.DELETED) {
            message.setState(MessageState.DELETED);
            message.setMessage("message deleted");
            socketHelper.sendMessage(message, MessageAction.DELETE);
//            messageAdapter.replaceMessage(message);
            MessageStore.deleteMessageFromUser(message);
            messageAdapter.notifyMessageChanged(message);
        }
    }

    public void editMessage(Message message, String input) {
        Log.d("ContextLog", "Edit");
        if (!message.isIncoming()) {
            long currentTimeMillis = System.currentTimeMillis();
            Date editDate = new Date(currentTimeMillis);
            message.setEditTimeStamp(editDate);
            message.setMessage(input);
            message.setState(MessageState.EDITED);

            socketHelper.sendMessage(message, MessageAction.EDIT);
//            messageAdapter.replaceMessage(message);
            MessageStore.editMessageFromUser(message);
            messageAdapter.notifyMessageChanged(message);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            if (messageAdapter != null) {
                Message selectedMessage = messageAdapter.getItem(messageAdapter.getPosition());
                Log.d("ContextLog", "Message: " + selectedMessage.getMessage());

                if (item.getItemId() == R.id.menu_edit) {
                    DialogHelper.showInputDialog(
                            this,
                            selectedMessage,
                            getString(R.string.editDialogTitle),
                            getString(R.string.editMessageText),
                            (userInput) -> editMessage(selectedMessage, userInput)
                    );
                    return true;
                } else if (item.getItemId() == R.id.menu_delete) {
                    DialogHelper.showConfirmationDialog(
                            this,
                            getString(R.string.deleteDialogTitle),
                            getString(R.string.deleteMessageText),
                            (dialog, which) -> deleteMessage(selectedMessage)
                    );
                    return true;
                } else {
                    return super.onContextItemSelected(item);
                }
            } else {
                Log.d("ContextLog", "Selected message is null");
                return false;
            }
        } catch (NullPointerException npe) {
            return false;
        }
    }

    @Override
    public void onDataChanged() {
        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
    }
}

