package com.example.whatsdownapp;

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

import com.example.whatsdownapp.adapter.ChatRecyclerAdapter;
import com.example.whatsdownapp.adapter.SearchUserRecyclerAdapter;
import com.example.whatsdownapp.model.ChatMessageModel;
import com.example.whatsdownapp.model.ChatroomModel;
import com.example.whatsdownapp.model.UserModel;
import com.example.whatsdownapp.utils.AndroidUtil;
import com.example.whatsdownapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ChatActivity extends AppCompatActivity implements ChatRecyclerAdapter.OnChatItemClickListener {

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView profilePic;
    ImageButton addImageBtn;

    private static final int REQUEST_MEDIA_PICK = 1;

    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatrooId(FirebaseUtil.currentUserId(),otherUser.getUserId());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        profilePic = findViewById(R.id.profile_pic_image_view);
        addImageBtn = findViewById(R.id.send_image_btn);

        FirebaseUtil.getCurrentProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        Uri uri = task1.getResult();
                        AndroidUtil.setProfilePic(this, uri, profilePic);
                    }
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

        backBtn.setOnClickListener((v) ->{
            onBackPressed();
        });

        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message, ChatMessageModel.MessageType.TEXT);
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
        recyclerView.setAdapter(adapter);
        adapter.setOnChatItemClickListener(this);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUser(String message, ChatMessageModel.MessageType type){

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now(), FirebaseUtil.createMessageId(), false);
        chatMessageModel.setMessageType(type);
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setlastMessage(chatMessageModel);

        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

//        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtil.currentUserId(),Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
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
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task ->{
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel==null){
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
        showBottomSheet(chatMessage);
    }

    private void showBottomSheet(ChatMessageModel chatMessage) {
        if (!chatMessage.isDeleted()) {
            ChatMenuOptionsBottomSheet bottomSheet = ChatMenuOptionsBottomSheet.newInstance(chatMessage, chatroomModel);
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
                                sendMessageToUser(gifUrl.toString(), ChatMessageModel.MessageType.GIF);
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
                                sendMessageToUser(imageUrl.toString(), ChatMessageModel.MessageType.IMAGE);
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
                                sendMessageToUser(videoUrl.toString(), ChatMessageModel.MessageType.VIDEO);
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