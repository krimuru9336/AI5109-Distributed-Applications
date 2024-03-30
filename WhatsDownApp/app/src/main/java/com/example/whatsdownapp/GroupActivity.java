package com.example.whatsdownapp;

import static com.example.whatsdownapp.utils.FirebaseUtil.getCurrentUserName;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.example.whatsdownapp.adapter.GroupChatRecyclerAdapter;
import com.example.whatsdownapp.model.ChatGroupModel;
import com.example.whatsdownapp.model.ChatMessageModel;
import com.example.whatsdownapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

public class GroupActivity extends AppCompatActivity implements GroupChatRecyclerAdapter.OnChatItemClickListener {
    EditText messageInput;
    ImageButton sendMsgBtn;
    ImageButton backBtn;
    TextView groupView;
    RecyclerView recyclerView;
    String groupId;
    ChatGroupModel chatGroup;
    ImageButton addImageBtn;

    GroupChatRecyclerAdapter groupChatRecyclerAdapter;

    private static final int REQUEST_MEDIA_PICK = 1;

    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_activity);

        String groupName = getIntent().getStringExtra("groupName");
        assert groupName != null;
        groupId = FirebaseUtil.getGroupChatId(groupName);
        messageInput = findViewById(R.id.chat_msg_input);
        sendMsgBtn = findViewById(R.id.send_message_btn);
        backBtn = findViewById(R.id.back_btn);
        groupView = findViewById(R.id.group_chat_name);
        recyclerView = findViewById(R.id.chat_recycler_view);
        addImageBtn = findViewById(R.id.send_image_btn);

        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });

        addImageBtn.setOnClickListener(v -> {
            // Launch a picker to choose between images, videos, and gifs
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            String[] mimeTypes = {"image/*", "video/*", "image/gif"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(intent,REQUEST_MEDIA_PICK);
        });

        groupView.setText(groupName);


        sendMsgBtn.setOnClickListener(v -> {
            try {
                Log.d("ButtonClick", "Send Message Button Clicked");
                String message = messageInput.getText().toString().trim();
                if (message.isEmpty()) {
                    return;
                }
                getUserName(new UserNameCallback() {
                    @Override
                    public void onUserNameReceived(String username) {
                        // Call sendMessageToGroup() with the sender name retrieved asynchronously
                        sendMessageToGroup(message, ChatMessageModel.MessageType.TEXT, username);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        getOrCreateGroupChat();
        setUpChatRecyclerView();

    }

    void getOrCreateGroupChat() {
        FirebaseUtil.getGroupChatsReference(groupId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatGroup = task.getResult().toObject(ChatGroupModel.class);
            }else{
                Log.d("ERROR", "NO Group");
            }
        });
    }

    void sendMessageToGroup(String message, ChatMessageModel.MessageType type, String sendername) {

        ChatMessageModel chatMessage = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now(), FirebaseUtil.createMessageId(), false);
        chatMessage.setMessageType(type);
        chatMessage.setSenderName(sendername);
        chatGroup.setLastMsgSenderId(FirebaseUtil.currentUserId());
        chatGroup.setLastMsg(chatMessage);

        FirebaseUtil.getGroupChatsReference(groupId).set(chatGroup);

        FirebaseUtil.getGroupChatMessageReference(groupId).add(chatMessage)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            messageInput.setText("");

                        }
                    }
                });

    }

    void setUpChatRecyclerView() {
        Query query = FirebaseUtil.getGroupChatMessageReference(groupId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();


        groupChatRecyclerAdapter = new GroupChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        groupChatRecyclerAdapter.setOnChatItemClickListener(this);
        recyclerView.setAdapter(groupChatRecyclerAdapter);
        groupChatRecyclerAdapter.startListening();

    }


    private void showBottomSheet(ChatMessageModel chatMessage) {
        if (!chatMessage.isDeleted()) {
            GroupMessageOptionsBottomSheet bottomSheet = GroupMessageOptionsBottomSheet.newInstance(chatMessage, chatGroup);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PICK && resultCode == RESULT_OK && data != null) {
            // Get the URI of the selected media
            Uri selectedMediaUri = data.getData();
            if (selectedMediaUri != null) {
                // Check the type of media based on its MIME type
                String mimeType = getContentResolver().getType(selectedMediaUri);
                Log.d("MiME TYPE", "onActivityResult: " +  mimeType);
                if (mimeType != null) {
                    if (mimeType.equals("image/gif")) {
                        // Upload the selected GIF to Firebase Storage
                        FirebaseUtil.uploadGif(selectedMediaUri, new FirebaseUtil.OnGifUploadListener() {
                            @Override
                            public void onGifUploadSuccess(Uri gifUrl) {
                                // GIF upload successful, send a message with the GIF URL to the chat room
//                                sendMessageToGroup(gifUrl.toString(), ChatMessage.MessageType.GIF);
                                getUserName(new UserNameCallback() {
                                    @Override
                                    public void onUserNameReceived(String username) {
                                        // Call sendMessageToGroup() with the sender name retrieved asynchronously
                                        sendMessageToGroup(gifUrl.toString(), ChatMessageModel.MessageType.GIF, username);
                                    }
                                });
                            }

                            @Override
                            public void onGifUploadFailure(Exception e) {
                                // GIF upload failed, show an error message
                                Toast.makeText(GroupActivity.this, "Failed to upload GIF", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (mimeType.startsWith("image/")) {
                        // Upload the selected image to Firebase Storage
                        FirebaseUtil.uploadImage(selectedMediaUri, new FirebaseUtil.OnImageUploadListener() {
                            @Override
                            public void onImageUploadSuccess(Uri imageUrl) {
                                // Image upload successful, send a message with the image URL to the chat room
//                                sendMessageToGroup(imageUrl.toString(), ChatMessage.MessageType.IMAGE);
                                getUserName(new UserNameCallback() {
                                    @Override
                                    public void onUserNameReceived(String username) {
                                        // Call sendMessageToGroup() with the sender name retrieved asynchronously
                                        sendMessageToGroup(imageUrl.toString(), ChatMessageModel.MessageType.IMAGE, username);
                                    }
                                });
                            }

                            @Override
                            public void onImageUploadFailure(Exception e) {
                                // Image upload failed, show an error message
                                Toast.makeText(GroupActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (mimeType.startsWith("video/")) {
                        // Upload the selected video to Firebase Storage
                        FirebaseUtil.uploadVideo(selectedMediaUri, new FirebaseUtil.OnVideoUploadListener() {
                            @Override
                            public void onVideoUploadSuccess(Uri videoUrl) {
                                Log.d("Success", "video uploaded successfully :" + videoUrl);
                                // Video upload successful, send a message with the video URL to the chat room
//                                sendMessageToGroup(videoUrl.toString(), ChatMessage.MessageType.VIDEO);
                                getUserName(new UserNameCallback() {
                                    @Override
                                    public void onUserNameReceived(String username) {
                                        // Call sendMessageToGroup() with the sender name retrieved asynchronously
                                        sendMessageToGroup(videoUrl.toString(), ChatMessageModel.MessageType.VIDEO, username);
                                    }
                                });
                            }

                            @Override
                            public void onVideoUploadFailure(Exception e) {
                                Log.d("Failure", "video upload failed " + e);
                                // Video upload failed, show an error message
                                Toast.makeText(GroupActivity.this, "Failed to upload video", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }
    }
    public void getUserName(UserNameCallback callback) {
        getCurrentUserName().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String username = task.getResult();
                callback.onUserNameReceived(username);
            } else {
                callback.onUserNameReceived("");
            }
        });
    }

    // Define a callback interface
    public interface UserNameCallback {
        void onUserNameReceived(String username);
    }

    @Override
    public void onLongPress(int position, ChatMessageModel chatMessage) {
        showBottomSheet(chatMessage);
    }
}
