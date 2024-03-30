package com.example.whatsdown;

import static com.example.whatsdown.utils.FirebaseUtil.getCurrentUserName;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.MediaStore;

import com.example.whatsdown.adapter.ChatRecyclerAdapter;
import com.example.whatsdown.model.ChatMessageModel;
import com.example.whatsdown.model.ChatroomModel;
import com.example.whatsdown.model.UserModel;
import com.example.whatsdown.utils.AndroidUtil;
import com.example.whatsdown.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity implements ChatRecyclerAdapter.OnChatItemClickListener{

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatMessageModel chatMessageModel;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ChatRecyclerAdapter adapter;
    ImageView imageView;
    ImageButton addImageBtn;
    private static final int REQUEST_MEDIA_PICK = 1;

    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get UserModel
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),otherUser.getUserId());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profile_pic_image_view);
        addImageBtn = findViewById(R.id.send_image_btn);

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(this, uri, imageView);
                    }
                });

        backBtn.setOnClickListener((v)->{
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

        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
//            sendMessageToUser(message, ChatMessageModel.MessageType.TEXT);
            getUserName(new UserNameCallback() {
                @Override
                public void onUserNameReceived(String username) {
                    // Call sendMessageToGroup() with the sender name retrieved asynchronously
                    sendMessageToUser(message, ChatMessageModel.MessageType.TEXT, username);
                }
            });
        }));

        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }

    void setupChatRecyclerView(){
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        adapter.setOnChatItemClickListener(this);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUser(String message, ChatMessageModel.MessageType type, String senderName) {
        ChatMessageModel chatMessage = new ChatMessageModel(FirebaseUtil.createMessageId(), message, FirebaseUtil.currentUserId(), Timestamp.now(),false);
        chatMessage.setMessageType(type);
        chatMessage.setSenderName(senderName);
        chatroomModel.setLastMessage(chatMessage);
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);


        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessage)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");
                        }
                    }
                });
    }
    void getOrCreateChatroomModel(){
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel==null){
                    //first time chatting
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    @Override
    public void onLongPress(int position, ChatMessageModel chatMessage) {
        showMessageEditBottomSheet(chatMessage);
    }
    private void showMessageEditBottomSheet(ChatMessageModel chatMessage) {
        if (!chatMessage.isDeleted()){
            MessageEditBottomSheet bottomSheet = MessageEditBottomSheet.newInstance(chatMessage, chatroomModel);
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
//                                sendMessageToUser(gifUrl.toString(), ChatMessageModel.MessageType.GIF);
                                getUserName(new UserNameCallback() {
                                    @Override
                                    public void onUserNameReceived(String username) {
                                        // Call sendMessageToGroup() with the sender name retrieved asynchronously
                                        sendMessageToUser(gifUrl.toString(), ChatMessageModel.MessageType.GIF, username);
                                    }
                                });
                            }

                            @Override
                            public void onGifUploadFailure(Exception e) {
                                // GIF upload failed, show an error message
//                                Toast.makeText(ChatActivity.this, "Failed to upload GIF", Toast.LENGTH_SHORT).show();
                                AndroidUtil.showToast(ChatActivity.this, "Failed to upload GIF");
                            }
                        });
                    } else if (mimeType.startsWith("image/")) {
                        // Upload the selected image to Firebase Storage
                        FirebaseUtil.uploadImage(selectedMediaUri, new FirebaseUtil.OnImageUploadListener() {
                            @Override
                            public void onImageUploadSuccess(Uri imageUrl) {
                                // Image upload successful, send a message with the image URL to the chat room
//                                sendMessageToUser(imageUrl.toString(), ChatMessageModel.MessageType.IMAGE);
                                getUserName(new UserNameCallback() {
                                    @Override
                                    public void onUserNameReceived(String username) {
                                        // Call sendMessageToGroup() with the sender name retrieved asynchronously
                                        sendMessageToUser(imageUrl.toString(), ChatMessageModel.MessageType.IMAGE, username);
                                    }
                                });
                            }

                            @Override
                            public void onImageUploadFailure(Exception e) {
                                // Image upload failed, show an error message
//                                Toast.makeText(ChatActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                AndroidUtil.showToast(ChatActivity.this, "Failed to upload image");
                            }
                        });
                    } else if (mimeType.startsWith("video/")) {
                        // Upload the selected video to Firebase Storage
                        FirebaseUtil.uploadVideo(selectedMediaUri, new FirebaseUtil.OnVideoUploadListener() {
                            @Override
                            public void onVideoUploadSuccess(Uri videoUrl) {
                                Log.d("Success", "video uploaded successfully :" + videoUrl);
                                // Video upload successful, send a message with the video URL to the chat room
//                                sendMessageToUser(videoUrl.toString(), ChatMessageModel.MessageType.VIDEO);
                                getUserName(new UserNameCallback() {
                                    @Override
                                    public void onUserNameReceived(String username) {
                                        // Call sendMessageToGroup() with the sender name retrieved asynchronously
                                        sendMessageToUser(videoUrl.toString(), ChatMessageModel.MessageType.VIDEO, username);
                                    }
                                });
                            }

                            @Override
                            public void onVideoUploadFailure(Exception e) {
                                Log.d("Failure", "video upload failed " + e);
                                // Video upload failed, show an error message
//                                Toast.makeText(ChatActivity.this, "Failed to upload video", Toast.LENGTH_SHORT).show();
                                AndroidUtil.showToast(ChatActivity.this, "Failed to upload video");
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

}