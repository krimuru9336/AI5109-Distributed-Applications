package com.da.chitchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.da.chitchat.adapters.UserAdapter;
import com.da.chitchat.database.messages.MessageRepository;
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
    private String targetPartner = "";
    private boolean isGroup = false;
    private RecyclerView recyclerView;
    private MessageRepository messageDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        messageDB = new MessageRepository(this);

        sendButton = findViewById(R.id.sendButton);
        messageEditText = findViewById(R.id.messageEditText);
        TextView partnerNameView = findViewById(R.id.partnerNameView);
        TextView groupNameView = findViewById(R.id.groupNameView);
        Button addToGroupButton = findViewById(R.id.addToGroupButton);

        Intent intent = getIntent();
        if (intent != null) {
            targetPartner = intent.getStringExtra("TARGET_PARTNER");
            isGroup = intent.getBooleanExtra("IS_GROUP", false);
            setTitle("Chat with " + targetPartner);
            if (isGroup) {
                partnerNameView.setTextSize(20);
                ViewGroup.MarginLayoutParams paramsName =
                        (ViewGroup.MarginLayoutParams) partnerNameView.getLayoutParams();
                paramsName.topMargin = 2;
                groupNameView.setVisibility(View.VISIBLE);
                ViewGroup.MarginLayoutParams paramsGroup =
                        (ViewGroup.MarginLayoutParams) groupNameView.getLayoutParams();
                paramsGroup.bottomMargin = 40;
                addToGroupButton.setVisibility(View.VISIBLE);
            }
            partnerNameView.setText(targetPartner);
        }

        webSocketManager = WebSocketManagerSingleton.getInstance(getApplicationContext());

        UserMessageListener umListener = UserMessageListenerSingleton.getInstance();
        messageAdapter = umListener.createAdapter(targetPartner, isGroup, this);

        recyclerView = findViewById(R.id.messageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
        registerForContextMenu(recyclerView);

        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> finish());

        sendButton.setEnabled(false);
        initInputWatcher();

        webSocketManager.setCurMessageActivity(this);
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
            Message msg = new Message(messageText, null, false);
            if (isGroup)
                msg.setChatGroup(targetPartner);
            UUID id = messageAdapter.addMessage(msg, isGroup);
            messageDB.addMessage(msg, targetPartner);
            webSocketManager.sendMessage(targetPartner, messageText, id, isGroup);

            // Clear the message input field
            messageEditText.setText("");
        }
    }

    public void deleteMessage(Message message) {
        if (!message.isIncoming()) {
            webSocketManager.deleteMessage(targetPartner, message.getID(), message.isGroup());
        }
        messageDB.deleteMessage(message.getID());
        messageAdapter.deleteMessage(message);
    }

    public void editMessage(Message message, String input) {
        long currentTimeMillis = System.currentTimeMillis();
        Date editDate = new Date(currentTimeMillis);
        message.setEditTimestamp(editDate);
        if (!message.isIncoming()) {
            webSocketManager.editMessage(targetPartner, message.getID(), input, editDate, message.isGroup());
        }
        messageDB.editMessage(message.getID(), input, editDate);
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

    public void createUserSelectDialog(View view) {
        webSocketManager.getGroupUsers(targetPartner);
    }

    public void openDialog(String[] preselectedUser) {
        runOnUiThread(() -> {
            DialogHelper.showUserSelectDialog(this, UserAdapter.getUsers(), preselectedUser,
                    getString(R.string.select_group_member), this::selectionReceived);
        });
    }

    public void openUnauthorizedDialog() {
        runOnUiThread(() -> {
            DialogHelper.showSimpleAlert(this, getString(R.string.unauthorized),
                    getString(R.string.only_admin));
        });
    }

    private void selectionReceived(String[] selection) {
        webSocketManager.changeGroupUsers(targetPartner, selection);
    }

    @Override
    public void onDataChanged() {
        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
    }
}
