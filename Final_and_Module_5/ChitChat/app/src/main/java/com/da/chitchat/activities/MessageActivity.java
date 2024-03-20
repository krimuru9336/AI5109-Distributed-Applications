package com.da.chitchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.da.chitchat.DialogHelper;
import com.da.chitchat.Message;
import com.da.chitchat.adapters.MessageAdapter;
import com.da.chitchat.R;
import com.da.chitchat.listeners.UserMessageListener;
import com.da.chitchat.singletons.UserMessageListenerSingleton;
import com.da.chitchat.WebSocketManager;
import com.da.chitchat.singletons.WebSocketManagerSingleton;
import com.da.chitchat.interfaces.OnDataChangedListener;

import java.util.Date;
import java.util.UUID;

public class MessageActivity extends AppCompatActivity implements OnDataChangedListener {
    private EditText messageEditText;
    private Button sendButton;
    private WebSocketManager webSocketManager;
    private MessageAdapter messageAdapter;
    private String targetUser = "";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        sendButton = findViewById(R.id.sendButton);
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
        registerForContextMenu(recyclerView);

        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> finish());

        sendButton.setEnabled(false);
        initInputWatcher();
    }

    private void initInputWatcher() {
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean isInputEmpty = charSequence.toString().trim().isEmpty();
                sendButton.setEnabled(!isInputEmpty);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing
            }
        });
    }

    public void sendMessage(View view) {
        String messageText = messageEditText.getText().toString();

        if (!TextUtils.isEmpty(messageText)) {
            UUID id = messageAdapter.addMessage(new Message(messageText, targetUser, false));
            webSocketManager.sendMessage(targetUser, messageText, id);

            // Clear the message input field
            messageEditText.setText("");
        }
    }

    public void deleteMessage(Message message) {
        if (!message.isIncoming()) {
            webSocketManager.deleteMessage(targetUser, message.getID());
        }
        messageAdapter.deleteMessage(message);
    }

    public void editMessage(Message message, String input) {
        long currentTimeMillis = System.currentTimeMillis();
        Date editDate = new Date(currentTimeMillis);
        message.setEditTimestamp(editDate);
        if (!message.isIncoming()) {
            webSocketManager.editMessage(targetUser, message.getID(), input, editDate);
        }
        messageAdapter.editMessage(message, input);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            if (messageAdapter != null) {
                Message selectedMessage = messageAdapter.getItem(messageAdapter.getPosition());

                if (item.getItemId() == R.id.menu_edit) {
                    DialogHelper.showInputDialog(
                            this,
                            selectedMessage,
                            getString(R.string.editDialogTitle),
                            getString(R.string.edit_your_message),
                            (userInput) -> editMessage(selectedMessage, userInput)
                    );
                    return true;
                } else if (item.getItemId() == R.id.menu_delete) {
                    String messageToShow = getString(R.string.deleteDialogText);
                    if (selectedMessage.isIncoming()) {
                        messageToShow = messageToShow.concat(" " +
                                getString(R.string.deleteDialogTextIncoming));
                    }
                    DialogHelper.showConfirmationDialog(
                            this,
                            getString(R.string.deleteDialogTitle),
                            messageToShow,
                            (dialog, which) -> deleteMessage(selectedMessage)
                    );
                    return true;
                } else {
                    return super.onContextItemSelected(item);
                }
            } else {
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
