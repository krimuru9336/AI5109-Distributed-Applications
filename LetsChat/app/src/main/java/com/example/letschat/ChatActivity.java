package com.example.letschat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.adapter.ChatRecyclerAdapter;
import com.example.letschat.model.ChatMessage;
import com.example.letschat.model.ChatRoom;
import com.example.letschat.model.User;
import com.example.letschat.util.AndroidUtil;
import com.example.letschat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity implements ChatRecyclerAdapter.OnChatItemClickListener {

    User otherUser;
    EditText messageInput;
    ImageButton sendMsgBtn;
    ImageButton backBtn;
    TextView otherUserView;
    RecyclerView recyclerView;
    String chatRoomId;
    ChatRoom chatRoom;

    ImageView profilePic;
    ImageButton addImageBtn;

    ChatRecyclerAdapter chatRecyclerAdapter;

    private static final int REQUEST_MEDIA_PICK = 1;

    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //getUser
        otherUser = AndroidUtil.getUserFromIntent(getIntent());
        chatRoomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserId(), otherUser.getUserId());
        messageInput = findViewById(R.id.chat_msg_input);
        sendMsgBtn = findViewById(R.id.send_message_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUserView = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        profilePic = findViewById(R.id.profile_pic_img_view);
        addImageBtn = findViewById(R.id.send_image_btn);

        FirebaseUtil.getCurrentProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        Uri uri = task1.getResult();
                        AndroidUtil.setProfilePic(this, uri, profilePic);
                    }
                });

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

        otherUserView.setText(otherUser.getUsername());


        sendMsgBtn.setOnClickListener(v -> {
            try {
                Log.d("ButtonClick", "Send Message Button Clicked");
                String message = messageInput.getText().toString().trim();
                if (message.isEmpty()) {
                    return;
                }
                sendMessageToUser(message, ChatMessage.MessageType.TEXT);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        getOrCreateChatRoom();
        setUpChatRecyclerView();

    }

    void getOrCreateChatRoom() {
        FirebaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatRoom = task.getResult().toObject(ChatRoom.class);
                if (chatRoom == null) {
                    //first time chatting
                    chatRoom = new ChatRoom(
                            chatRoomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoom);
                }
            }
        });
    }

    void sendMessageToUser(String message, ChatMessage.MessageType type) {

        ChatMessage chatMessage = new ChatMessage(message, FirebaseUtil.currentUserId(), Timestamp.now(), FirebaseUtil.createMessageId(), false);
        chatMessage.setMessageType(type);
        chatRoom.setLastMsgSenderId(FirebaseUtil.currentUserId());
        chatRoom.setLastMsg(chatMessage);

        FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoom);

        FirebaseUtil.getChatMessageReference(chatRoomId).add(chatMessage)
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
        Query query = FirebaseUtil.getChatMessageReference(chatRoomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class).build();


        chatRecyclerAdapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        chatRecyclerAdapter.setOnChatItemClickListener(this);
        recyclerView.setAdapter(chatRecyclerAdapter);
        chatRecyclerAdapter.startListening();

    }

    @Override
    public void onLongPress(int position, ChatMessage chatMessage) {
        showBottomSheet(chatMessage);
    }

    private void showBottomSheet(ChatMessage chatMessage) {
        if (!chatMessage.isDeleted()) {
            MessageOptionsBottomSheet bottomSheet = MessageOptionsBottomSheet.newInstance(chatMessage, chatRoom);
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
                                sendMessageToUser(gifUrl.toString(), ChatMessage.MessageType.GIF);
                            }

                            @Override
                            public void onGifUploadFailure(Exception e) {
                                // GIF upload failed, show an error message
                                Toast.makeText(ChatActivity.this, "Failed to upload GIF", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (mimeType.startsWith("image/")) {
                        // Upload the selected image to Firebase Storage
                        FirebaseUtil.uploadImage(selectedMediaUri, new FirebaseUtil.OnImageUploadListener() {
                            @Override
                            public void onImageUploadSuccess(Uri imageUrl) {
                                // Image upload successful, send a message with the image URL to the chat room
                                sendMessageToUser(imageUrl.toString(), ChatMessage.MessageType.IMAGE);
                            }

                            @Override
                            public void onImageUploadFailure(Exception e) {
                                // Image upload failed, show an error message
                                Toast.makeText(ChatActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (mimeType.startsWith("video/")) {
                        // Upload the selected video to Firebase Storage
                        FirebaseUtil.uploadVideo(selectedMediaUri, new FirebaseUtil.OnVideoUploadListener() {
                            @Override
                            public void onVideoUploadSuccess(Uri videoUrl) {
                                Log.d("Success", "video uploaded successfully :" + videoUrl);
                                // Video upload successful, send a message with the video URL to the chat room
                                sendMessageToUser(videoUrl.toString(), ChatMessage.MessageType.VIDEO);
                            }

                            @Override
                            public void onVideoUploadFailure(Exception e) {
                                Log.d("Failure", "video upload failed " + e);
                                // Video upload failed, show an error message
                                Toast.makeText(ChatActivity.this, "Failed to upload video", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }
    }



}