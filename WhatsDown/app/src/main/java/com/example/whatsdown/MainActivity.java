package com.example.whatsdown;

import static com.example.whatsdown.Constants.BASE_URL;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.bumptech.glide.Glide;
import com.example.whatsdown.model.ChatMessage;
import com.example.whatsdown.model.UpdateMessage;
import com.example.whatsdown.model.User;
import com.example.whatsdown.requests.ApiService;
import com.example.whatsdown.requests.MessageCallback;
import com.example.whatsdown.requests.RetrieveChatController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MessageCallback {
    /*
     * Jonas Wagner - 1315578
     */
    private EditText inputText;
    private LinearLayout chatContainer;
    private final Handler messageHandler = new Handler();
    private static final int MESSAGE_FETCH_DELAY = 3000;
    private User loggedInUser;
    private User selectedUser;
    private final RetrieveChatController retrieveChatController = new RetrieveChatController(this);
    private ChatMessage selectedMessage;
    private ScrollView scrollView;
    private static final int PICK_MEDIA_REQUEST_CODE = 1;

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

        ImageButton addMediaButton = findViewById(R.id.add_media_button);
        addMediaButton.setOnClickListener(this::showMediaOptionsPopup);

        inputText = findViewById(R.id.input_text);
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

        String mediaType = message.getMediaType();
        if (mediaType != null) {
            if (mediaType.equalsIgnoreCase("Image")) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(800, 800)); // Set appropriate size
                Glide.with(this).load(message.getMediaUrl()).into(imageView);
                messageLayout.addView(imageView);
            } else if (mediaType.equalsIgnoreCase("Video")) {
                PlayerView playerView = new PlayerView(this);
                playerView.setLayoutParams(new ViewGroup.LayoutParams(800, 800)); // Set appropriate size
                messageLayout.addView(playerView);
                initializeExoPlayer(playerView, Uri.parse(message.getMediaUrl()));
            } else if (mediaType.equalsIgnoreCase("Gif")) {
                ImageView gifView = new ImageView(this);
                gifView.setLayoutParams(new ViewGroup.LayoutParams(800, 800)); // Set appropriate size
                Glide.with(this).asGif().load(message.getMediaUrl()).into(gifView);
                messageLayout.addView(gifView);
            }
        }

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

    private void initializeExoPlayer(PlayerView playerView, Uri parse) {
        ExoPlayer player = new ExoPlayer.Builder(this).build();

        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(parse);

        player.addMediaItem(mediaItem);
        player.setPlayWhenReady(true);
    }

    public void saveData(View data) {
        String userInput = inputText.getText().toString();
        sendMessageToServer(loggedInUser.getUserId(), selectedUser.getUserId(), userInput, null);
        inputText.setText("");
    }

    @Override
    public void onMessagesReceived(List<ChatMessage> messages) {
        for (ChatMessage message : messages) {
            System.out.println("Message: " + message.getContent() + ", timestamp: " + message.getTimestamp());
            boolean isRightAligned = message.getSenderId() == loggedInUser.getUserId();
            appendMessageToChat(message, message.getTimestamp(), isRightAligned);
        }
        if (messages.size() != 0) {
            scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
        }
    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();
        Toast.makeText(this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public void sendMessageToServer(int senderId, int receiverId, String content, Uri imageUri) {
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        RequestBody requestBodySenderId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(senderId));
        RequestBody requestBodyReceiverId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(receiverId));
        RequestBody requestBodyContent = RequestBody.create(MediaType.parse("text/plain"), content);

        MultipartBody.Part requestBodyMedia = null;
        if (imageUri != null && !Objects.requireNonNull(imageUri.getPath()).isEmpty()) {
            String fileName = "media_" + System.currentTimeMillis();

            try {
                File mediaFile = createFileFromUri(imageUri, fileName);
                String mimeType = getContentResolver().getType(imageUri);
                assert mimeType != null;
                RequestBody mediaRequestBody = RequestBody.create(MediaType.parse(mimeType), mediaFile);
                requestBodyMedia = MultipartBody.Part.createFormData("media", mediaFile.getName(), mediaRequestBody);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to create file from URI", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        System.out.println("Sending message...");
        Call<Void> call = apiService.sendMessage(requestBodySenderId, requestBodyReceiverId, requestBodyContent, requestBodyMedia);
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
                System.out.println("Error: " + t.getMessage());
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

    private void showMediaOptionsPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.media_options_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_image) {
                openFileExplorer("Image");
                return true;
            } else if (id == R.id.menu_video) {
                openFileExplorer("Video");
                return true;
            } else if (id == R.id.menu_gif) {
                openFileExplorer("Gif");
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void openFileExplorer(String mediaType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(getMimeType(mediaType));
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(Intent.createChooser(intent, "Select Media"), PICK_MEDIA_REQUEST_CODE);
    }

    private String getMimeType(String mediaType) {
        switch (mediaType.toLowerCase()) {
            case "image":
                return "image/*";
            case "video":
                return "video/*";
            case "gif":
                return "image/gif";
            default:
                return "*/*";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("Request code: " + requestCode);
        System.out.println("Result code: " + resultCode);
        System.out.println("Data: " + data);

        if (requestCode == PICK_MEDIA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedMediaUri = data.getData();
            String messageContent = inputText.getText().toString();
            sendMessageToServer(loggedInUser.getUserId(), selectedUser.getUserId(), messageContent, selectedMediaUri);
            inputText.setText("");
        }
    }

    private File createFileFromUri(Uri uri, String fileName) throws IOException {
        File file = new File(getFilesDir(), fileName);

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {

            if (inputStream != null) {
                byte[] buffer = new byte[4 * 1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            return file;
        }
    }

}