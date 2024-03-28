// Sven Schickentanz - fdai7287
package com.da.chitchat.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

/**
 * The `MessageActivity` class is responsible for displaying and managing the chat messages
 * between the user and a target partner or group. It extends the `AppCompatActivity` class
 * and implements the `OnDataChangedListener` interface.
 * The activity contains various UI elements such as an EditText for typing messages, a Button
 * for sending messages, a RecyclerView for displaying the chat messages, and other related
 * views and buttons.
 * The activity initializes the necessary components and sets up event listeners for sending
 * messages, sending media, editing messages, deleting messages, and managing group members.
 * The activity communicates with the server using a WebSocket connection managed by the
 * `WebSocketManager` class. It also interacts with the local message database through the
 * `MessageRepository` class to store and retrieve chat messages.
 */
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

        // Get the target partner and group information from the intent
        Intent intent = getIntent();
        if (intent != null) {
            targetPartner = intent.getStringExtra("TARGET_PARTNER");
            isGroup = intent.getBooleanExtra("IS_GROUP", false);
            setTitle("Chat with " + targetPartner);
            // Set the partner name and group name if it's a group chat
            // Adjust the text size and margins for group chats
            // Shows the group name and add to group button if it's a group chat
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
        messageAdapter = umListener.createAdapter(targetPartner, isGroup, this, this);

        // Set up the RecyclerView to display the chat messages
        recyclerView = findViewById(R.id.messageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
        registerForContextMenu(recyclerView);

        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> finish());

        sendButton.setEnabled(false);
        // Set up the input watcher for the message EditText
        initInputWatcher();

        webSocketManager.setCurMessageActivity(this);
    }

    /**
     * Initializes the input watcher for the message EditText to enable/disable the send button
     */
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

    /**
     * Launches the media selector to pick an image or video from the device
     */
    private final ActivityResultLauncher<Intent> pickMediaLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null) {
                    // Grant read permission to the selected media URI
                    // Needs to be set as persistable to access the media on reload
                    getContentResolver().takePersistableUriPermission(uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sendMedia(uri);
                }
            }
        }
    );

    /**
     * Launches the media selector to pick an image or video from the device
     * 
     * @param view The view that triggered the media selector
     */
    public void mediaSelector(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        // Allow the user to select images and videos
        String[] mimeTypes = {"image/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launch the media selector
        pickMediaLauncher.launch(intent);
    }

    /**
     * Sends the selected media to the target partner or group
     * 
     * @param mediaUri The URI of the selected media
     */
    public void sendMedia(Uri mediaUri) {
        // Create a new message with the media URI and send it to the target partner or group
        // It will show as pending until the media was received by the recipient
        Message msg = new Message(getString(R.string.media_pending), null, false);
        msg.setMediaUri(mediaUri);
        String mimeType = getContentResolver().getType(mediaUri);
        // Check if the media is a video
        if (mimeType != null && mimeType.startsWith("video"))
            msg.setIsVideo(true);
        if (isGroup)
            msg.setChatGroup(targetPartner);
        UUID id = messageAdapter.addMessage(msg, isGroup);

        // Add the message to the local database
        messageDB.addMessage(msg, targetPartner);
        // Send the media message to the target partner or group
        webSocketManager.sendMedia(targetPartner, msg.getText(), mediaUri, id, mimeType, isGroup,
                msg.isVideo());
    }

    /**
     * Sends a text message to the target partner or group
     * 
     * @param view The view that triggered the message sending
     */
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

    /**
     * Deletes the specified message from the chat
     * 
     * @param message The message to be deleted
     */
    public void deleteMessage(Message message) {
        if (!message.isIncoming()) {
            webSocketManager.deleteMessage(targetPartner, message.getID(), message.isGroup());
        }
        messageDB.deleteMessage(message.getID());
        messageAdapter.deleteMessage(message);
    }

    /**
     * Edits the specified message with the new input text
     * 
     * @param message The message to be edited
     * @param input The new text input for the message
     */
    public void editMessage(Message message, String input) {
        long currentTimeMillis = System.currentTimeMillis();
        Date editDate = new Date(currentTimeMillis);
        message.setEditTimestamp(editDate);
        if (!message.isIncoming()) {
            // Send the edited message to the target partner or group
            webSocketManager.editMessage(targetPartner, message.getID(), input, editDate, message.isGroup());
        }
        messageDB.editMessage(message.getID(), input, editDate);
        messageAdapter.editMessage(message, input);
    }

    /**
     * Handles the context menu item selection for editing and deleting messages
     * 
     * @param item The selected menu item
     * @return True if the item was handled, false otherwise
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            if (messageAdapter != null) {
                Message selectedMessage = messageAdapter.getItem(messageAdapter.getPosition());

                if (item.getItemId() == R.id.menu_edit) {
                    // Show the input dialog to edit the selected message
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
                    // Show the confirmation dialog to delete the selected message
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

    /**
     * Creates a dialog to select users to add to the group
     * 
     * @param view The view that triggered the user selection dialog
     */
    public void createUserSelectDialog(View view) {
        webSocketManager.getGroupUsers(targetPartner);
    }

    /**
     * Opens the user selection dialog with the preselected user
     * 
     * @param preselectedUser The preselected users that are currently in group
     */
    public void openDialog(String[] preselectedUser) {
        runOnUiThread(() -> DialogHelper.showUserSelectDialog(this, UserAdapter.getUsers(),
                preselectedUser, getString(R.string.select_group_member), this::selectionReceived));
    }

    /**
     * Opens an alert to inform the user that they are not authorized to perform the action
     */
    public void openUnauthorizedDialog() {
        runOnUiThread(() -> DialogHelper.showSimpleAlert(this, getString(R.string.unauthorized),
                getString(R.string.only_admin)));
    }

    /**
     * Updates the group members list with the new selection
     * 
     * @param selection The selected users to add / remove from the group
     */
    private void selectionReceived(String[] selection) {
        webSocketManager.changeGroupUsers(targetPartner, selection);
    }

    /**
     * Notifies the adapter that the data has changed
     */
    @Override
    public void onDataChanged() {
        if (messageAdapter != null && messageAdapter.getItemCount() > 0) {
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
        }
    }
}
