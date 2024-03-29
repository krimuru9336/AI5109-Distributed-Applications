package com.example.chitchatapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

public class MessageActivity extends AppCompatActivity implements OnDataChangedListener {
    EditText messageEditText;
    SocketHelper socketHelper;
    MessageHelper messageHelper;
    Socket socket;
    MessageAdapter messageAdapter;
    String username = "";
    String targetUser = "";
    boolean typing = false;
    RecyclerView recyclerView;

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

        messageAdapter = messageHelper.createAdapter(targetUser, this, this);

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

    public void sendMedia(Uri uri) {
        String mediaType = getContentResolver().getType(uri);
        MessageType messageType;
        if (mediaType != null && mediaType.startsWith("video"))
            messageType = MessageType.VIDEO;
        else
            messageType = MessageType.IMAGE;

        Message message = new Message("Pending...", username, targetUser, false, messageType);
        message.setMediaUri(uri);
        message.setMimeType(Base64Converter.getMimeType(this, uri));

        socketHelper.sendMedia(message, MessageAction.MESSAGE);

        messageAdapter.addMessage(message);
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
            MessageStore.deleteMessage(message);
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
            MessageStore.editMessage(message);
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

    private final ActivityResultLauncher<Intent> launcher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result->{
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null) {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sendMedia(uri);
                }
            }
        });

    public void selectAttachment(View view){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] types =  {"image/*","video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,types);
        launcher.launch(intent);
    }

    @Override
    public void onDataChanged() {
        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
    }
}

