package com.example.whatsdown;

import static com.example.whatsdown.Constants.BASE_URL;

import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.whatsdown.model.ChatMessage;
import com.example.whatsdown.model.SendMessageRequest;
import com.example.whatsdown.model.UpdateMessage;
import com.example.whatsdown.model.User;
import com.example.whatsdown.requests.ApiService;
import com.example.whatsdown.requests.MessageCallback;
import com.example.whatsdown.requests.RetrieveChatController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MessageCallback {
    private EditText inputText;
    private DatabaseHelperSQLite databaseHelperSQLite;
    private LinearLayout chatContainer;
    private final Handler messageHandler = new Handler();
    private static final int MESSAGE_FETCH_DELAY = 3000;
    private User loggedInUser;
    private User selectedUser;
    private final RetrieveChatController retrieveChatController = new RetrieveChatController(this);
    private ChatMessage selectedMessage;
    private ScrollView scrollView;

    private final Runnable fetchMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            System.out.println("Fetching messages...");
            retrieveChatController.startLastFetchedTimestamp(loggedInUser.getUserId(), selectedUser.getUserId());
            messageHandler.postDelayed(this, MESSAGE_FETCH_DELAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectedUser = (User) getIntent().getSerializableExtra("selectedUser");
        loggedInUser = (User) getIntent().getSerializableExtra("loggedInUser");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(selectedUser.getName());
        }

        System.out.println("Selected user userId: " + selectedUser.getUserId());
        System.out.println("Selected user name: " + selectedUser.getName());

        System.out.println("Logged in user userId: " + loggedInUser.getUserId());
        System.out.println("Logged in user name: " + loggedInUser.getName());

        chatContainer = findViewById(R.id.chat_container);
        scrollView = findViewById(R.id.scroll_view);

        retrieveChatController.start(loggedInUser.getUserId(), selectedUser.getUserId());

        inputText = findViewById(R.id.input_text);
        databaseHelperSQLite = new DatabaseHelperSQLite(this);

        databaseHelperSQLite.clearData();

        startFetchingMessages();
    }

    private void startFetchingMessages() {
        messageHandler.postDelayed(fetchMessagesRunnable, MESSAGE_FETCH_DELAY);
    }

    private void stopFetchingMessages() {
        messageHandler.removeCallbacksAndMessages(null);
    }

    private void appendMessageToChat(ChatMessage message, String timestamp, boolean isRightAligned) {
        LinearLayout.LayoutParams messageLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        messageLayoutParams.setMargins(8, 8, 8, 8);

        if (isRightAligned) {
            messageLayoutParams.gravity = Gravity.END;
        }

        LinearLayout messageLayout = new LinearLayout(this);
        messageLayout.setLayoutParams(messageLayoutParams);
        messageLayout.setOrientation(LinearLayout.VERTICAL);

        int backgroundResource = isRightAligned ? R.drawable.right_message_background : R.drawable.left_message_background;
        messageLayout.setBackgroundResource(backgroundResource);

        messageLayout.setOnLongClickListener(v -> {
            selectedMessage = message;
            showEditDeleteOptions(selectedMessage);
            return true;
        });

        TextView messageTextView = new TextView(this);
        messageTextView.setLayoutParams(messageLayoutParams);
        messageTextView.setPadding(8, 8, 8, 8);
        messageTextView.setText(message.getContent());
        messageTextView.setTextColor(getResources().getColor(android.R.color.black));

        LinearLayout.LayoutParams timestampLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        timestampLayoutParams.gravity = isRightAligned ? Gravity.END : Gravity.START;
        TextView timestampTextView = new TextView(this);
        timestampTextView.setLayoutParams(timestampLayoutParams);
        timestampTextView.setPadding(8, 0, 8, 0);
        timestampTextView.setText(timestamp);
        timestampTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        timestampTextView.setTextSize(12);

        chatContainer.addView(messageLayout);

        messageLayout.addView(messageTextView);
        chatContainer.addView(timestampTextView);
    }

    public void saveData(View data) {
        String userInput = inputText.getText().toString();
        databaseHelperSQLite.insertData(userInput);
        sendMessageToServer(loggedInUser.getUserId(), selectedUser.getUserId(), userInput);
        inputText.setText("");
    }

    @Override
    public void onMessagesReceived(List<ChatMessage> messages) {
        for (ChatMessage message : messages) {
            System.out.println("Message: " + message.getContent() + ", timestamp: " + message.getTimestamp());
            boolean isRightAligned = message.getSenderId() == loggedInUser.getUserId();
            appendMessageToChat(message, message.getTimestamp(), isRightAligned);
        }
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();
        Toast.makeText(this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public void sendMessageToServer(int senderId, int receiverId, String content) {
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        SendMessageRequest sendMessageRequest = new SendMessageRequest(senderId, receiverId, content);
        Call<Void> call = apiService.sendMessage(sendMessageRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("Message sent successfully");
                } else {
                    Toast.makeText(MainActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.onBackPressed();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopFetchingMessages();
    }

    private void showEditDeleteOptions(ChatMessage selectedMessage) {

        // AlertDialog example
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an option")
                .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                    switch (which) {
                        case 0: // Edit option selected
                            editSelectedMessage(selectedMessage);
                            break;
                        case 1: // Delete option selected
                            deleteSelectedMessage(selectedMessage);
                            break;
                    }
                })
                .show();
    }

    private void editSelectedMessage(ChatMessage message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Message");

        EditText input = new EditText(this);
        input.setText(message.getContent());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedContent = input.getText().toString().trim();
            if (!TextUtils.isEmpty(updatedContent)) {
                int messageId = message.getId();
                ApiService apiService = retrofit.create(ApiService.class);
                UpdateMessage updateMessage = new UpdateMessage(updatedContent);
                Call<ChatMessage> call = apiService.updateMessage(messageId, updateMessage);

                call.enqueue(new Callback<ChatMessage>() {
                    @Override
                    public void onResponse(Call<ChatMessage> call, Response<ChatMessage> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Message updated successfully", Toast.LENGTH_SHORT).show();
                            clearChatUI();
                            retrieveChatController.start(loggedInUser.getUserId(), selectedUser.getUserId());
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to update message", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatMessage> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteSelectedMessage(ChatMessage message) {
        AlertDialog.Builder confirmDeleteDialog = new AlertDialog.Builder(this);
        confirmDeleteDialog.setTitle("Confirm Delete");
        confirmDeleteDialog.setMessage("Are you sure you want to delete this message?");

        confirmDeleteDialog.setPositiveButton("Yes", (dialog, which) -> {
            int messageId = message.getId();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            Call<Void> call = apiService.deleteMessage(messageId);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Message deleted successfully", Toast.LENGTH_SHORT).show();
                        clearChatUI();
                        retrieveChatController.start(loggedInUser.getUserId(), selectedUser.getUserId());
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to delete message", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        confirmDeleteDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        confirmDeleteDialog.show();
    }

    private void clearChatUI() {
        chatContainer.removeAllViews();
    }

}