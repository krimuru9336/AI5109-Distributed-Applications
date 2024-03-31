package com.example.chatapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapter.ChatRecyclerAdapter;
import com.example.chatapp.model.ChatMessageModel;
import com.example.chatapp.model.ChatroomModel;
import com.example.chatapp.model.UserModel;
import com.example.chatapp.utils.AndroidUtil;
import com.example.chatapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ChatActivity extends AppCompatActivity implements ChatRecyclerAdapter.OnChatItemLongClickListener  {

    private static final int REQUEST_IMAGE_PICKER = 1;
    private static final int REQUEST_VIDEO_PICKER = 2;

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    EditText messageInput;
    ImageButton backBtn;
    ImageButton sendMessageBtn;
    Button mediaBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;
    FirestoreRecyclerOptions<ChatMessageModel> options;


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
        mediaBtn = findViewById(R.id.media_send_btn);

        mediaBtn.setOnClickListener(this::showMediaOptions);
        //mediaBtn.setOnClickListener(view -> openImagePicker());
        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(this,uri,imageView);
                    }
                });

        backBtn.setOnClickListener(v -> {
            try {
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                // If the message is not empty, send the text message
                sendMessageToUser(message);
            }

        }));
        getOrCreateChatroomModel();

        setupChatRecyclerView();
        // Create an instance of ChatRecyclerAdapter and set the long click listener
        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        adapter.setOnChatItemLongClickListener(this); // Set the long click listener
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    // Implement the onItemLongClick method from the OnChatItemLongClickListener interface
    @Override
    public void onItemLongClick(View view, int position) {
        // Retrieve the ChatMessageModel at the clicked position
        ChatMessageModel clickedMessage = adapter.getMessageAtPosition(position);

        // Check if the clickedMessage is not null before showing the popup
        if (clickedMessage != null) {
            showPopupMenu(view, position);
        }
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(this.getApplicationContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        // Set the item click listener for the popup menu
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_edit) {
                // Handle edit action
                editMessage(position);
                return true;
            } else if (item.getItemId() == R.id.menu_delete) {
                // Handle delete action
                deleteMessage(position);
                return true;
            } else {
                return false;
            }
        });

        popupMenu.show();
    }
    private void editMessage(int position) {
        // Retrieve the ChatMessageModel at the clicked position
        ChatMessageModel clickedMessage = adapter.getMessageAtPosition(position);

        // Check if the clickedMessage is not null before proceeding
        if (clickedMessage != null) {
            // Call a method to show an edit dialog or perform any other action
            editMessageDirectly(clickedMessage);
        }
    }
    private void editMessageDirectly(ChatMessageModel messageToEdit) {
        // Implement the edit action
        // For simplicity, let's assume you're opening an edit text field for the user to edit the message

        // Create an EditText dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Message");

        // Set up the input
        final EditText input = new EditText(this);
        input.setText(messageToEdit.getMessage()); // Pre-fill the EditText with the existing message
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Get the edited message from the EditText
            String editedMessage = input.getText().toString().trim();

            // Check if the edited message is not empty
            if (!editedMessage.isEmpty()) {
                // Update the message in Firestore
                updateMessage(messageToEdit.getDocumentId(), editedMessage);
            } else {
                showToast("Message cannot be empty!");
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.show();
    }

    private void updateMessage(String messageId, String editedMessage) {
        // Update the message in Firestore using the document ID
        FirebaseUtil.getChatroomMessageReference(chatroomId).document(messageId)
                .update("message", editedMessage)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Message edited successfully!");
                    } else {
                        showToast("Failed to edit message!");
                    }
                });
    }

    private void deleteMessage(int position) {
        // Retrieve the ChatMessageModel at the clicked position
        ChatMessageModel clickedMessage = adapter.getMessageAtPosition(position);

        // Check if the clickedMessage is not null before proceeding
        if (clickedMessage != null) {
            // Delete the message directly
            deleteMessageConfirmed(clickedMessage);
        }
    }

    private void deleteMessageConfirmed(ChatMessageModel message) {
        // Get the unique document ID of the message
        String messageId = message.getDocumentId();

        // Check if the messageId is not null before proceeding
        if (messageId != null && !messageId.isEmpty()) {
            // Delete the message directly
            FirebaseUtil.getChatroomMessageReference(chatroomId).document(messageId)
                    .delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Message deleted successfully
                            showToast("Message deleted!");
                        } else {
                            // Failed to delete the message
                            // Handle the error or show a message to the user
                            showToast("Failed to delete message!");
                        }
                    });
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    void setupChatRecyclerView(){
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options,getApplicationContext());

        adapter.setOnChatItemLongClickListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
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
    void getOrCreateChatroomModel(){
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel==null){
                    //first time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()),
                            Timestamp.now(),
                            "",
                            null,
                            null
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

   void sendMessageToUser(String message){

        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);


        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now(), null,false,null,null);
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            DocumentReference documentReference = task.getResult();
                            String messageId = documentReference.getId(); // This is the generated document ID
                            chatMessageModel.setDocumentId(messageId); // Set the document ID in the ChatMessageModel

                            // Update the document with the correct documentId
                            FirebaseUtil.getChatroomMessageReference(chatroomId).document(messageId).set(chatMessageModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> updateTask) {
                                            if (updateTask.isSuccessful()) {
                                                messageInput.setText("");
                                                sendNotification(message);
                                            } else {
                                                // Handle the error
                                                showToast("Failed to update document with correct documentId!");
                                            }
                                        }
                                    });
                        } else {
                            // Handle the error
                            showToast("Failed to add new message!");
                        }
                    }
                });
    }

    void sendNotification(String message){
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try{
                    JSONObject jsonObject = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title",currentUser.getUsername());
                    notificationObj.put("body",message);


                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId", currentUser.getUserId());

                    jsonObject.put("notification", notificationObj);
                    jsonObject.put("data",dataObj);
                    jsonObject.put("to",otherUser.getFcmToken());

                    callApi(jsonObject);


                }catch (Exception e){

                }
            }
        });
    }
    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body  = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAACov5tSM:APA91bEsk3D99KLHhCxOv6lElQ5ILAq9nyj6jYWCExxEYwAaGEkgCguOm1WIb5V24V1NXrK2DQ1T7E58BmMc0cEnCsW1DB_efIqg5g2jzU4FRs9jnNevt6z7sHWhoy9PnsOP6oX35p7f")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    public void showMediaOptions(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.media_options_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_image) {
                // Handle image option
                openImagePicker();
            } else if (itemId == R.id.menu_video) {
                // Handle video option
                openVideoPicker();
            } else if (itemId == R.id.menu_gif) {
                // Handle GIF option
                // Open GIF picker or provide a list of GIFs to choose from
            }
            return true;
        });
        popupMenu.show();
    }
    private void openImagePicker() {
        // Intent to open image picker
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICKER);
    }
    private void openVideoPicker() {
        // Intent to open video picker
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_VIDEO_PICKER);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == RESULT_OK && data != null) {
            // Handle selected image
            Uri imageUri = data.getData();
            if (imageUri != null) {
                // Upload the selected image to Firebase Storage
                uploadImageToStorage(imageUri);
                // Send the image to the user
                sendImageToUser(imageUri);
            }
        } else if (requestCode == REQUEST_VIDEO_PICKER && resultCode == RESULT_OK && data != null) {
            // Handle selected video
            Uri videoUri = data.getData();
            if (videoUri != null) {
                // Upload the selected video to Firebase Storage
                //uploadVideoToStorage(videoUri);
                // Send the video to the user
                sendVideoToUser(videoUri);
            }
        }
    }

    private void sendImageToUser(Uri imageUri) {
        // Get a reference to the Firebase Storage location where you want to upload the image
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");

        // Create a unique filename for the image (optional)
        String filename = "image_" + System.currentTimeMillis() + ".jpg";

        // Upload the image to Firebase Storage
        storageRef.child(filename).putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image upload successful
                    // Get the download URL of the uploaded image
                    storageRef.child(filename).getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        // Now you have the imageUrl, you can create and send the message
                        String message = ""; // You can include a message or leave it empty
                        sendMessageWithImage(imageUrl, message);
                    });
                })
                .addOnFailureListener(e -> {
                    // Image upload failed
                    showToast("Failed to upload image.");
                });
    }


    private void uploadImageToStorage(Uri imageUri) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Get the current user's ID
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Get a reference to the Firebase Storage location where you want to upload the image
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(userId);

            // Generate a secure random filename for the image
            String filename = "image_" + System.currentTimeMillis() + ".jpg";

            // Upload the image to Firebase Storage
            storageRef.child(filename).putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image upload successful
                        // Get the download URL of the uploaded image
                        storageRef.child(filename).getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // Now you have the imageUrl, you can handle it as needed
                            // For example, you can display the image in the chat or save it to send later
                            showToast("Image uploaded successfully: " + imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Image upload failed
                        showToast("Failed to upload image.");
                    });
        } else {
            // User is not authenticated, handle accordingly
            showToast("User not authenticated.");
        }
    }
    private void sendMessageWithImage(String imageUrl, String message) {
        // Update the chatroomModel with the image URL and other message details
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);
        chatroomModel.setLastImageSentUrl(imageUrl); // Set the image URL
        chatroomModel.setLastVideoSentUrl(null);

        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        // Create a ChatMessageModel with the image URL and other message details
        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now(), null, false, imageUrl,null);

        // Send the message to Firebase Firestore
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Message sent successfully
                        showToast("Image message sent successfully.");
                        messageInput.setText(""); // Clear the message input field
                    } else {
                        // Handle the error
                        showToast("Failed to send image message.");
                    }
                });
    }


    private void sendVideoToUser(Uri videoUri) {
        Log.d("TAG", "Debug message");
        // Get a reference to the Firebase Storage location where you want to upload the video
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("videos");

        // Create a unique filename for the video (optional)
        String filename = "video_" + System.currentTimeMillis() + ".mp4";

        // Upload the video to Firebase Storage
        storageRef.child(filename).putFile(videoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Video upload successful
                    // Get the download URL of the uploaded video
                    storageRef.child(filename).getDownloadUrl().addOnSuccessListener(uri -> {
                        String videoUrl = uri.toString();
                        // Now you have the videoUrl, you can create and send the message
                        String message = ""; // You can include a message or leave it empty
                        sendMessageWithVideo(videoUrl, message);
                    });
                })
                .addOnFailureListener(e -> {
                    // Video upload failed
                    showToast("Failed to upload video.");
                });
    }

    private void sendMessageWithVideo(String videoUrl, String message) {
        // Update the chatroomModel with the video URL and other message details
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);
        chatroomModel.setLastVideoSentUrl(videoUrl); // Set the video URL
        chatroomModel.setLastImageSentUrl(null);

        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        // Create a ChatMessageModel with the video URL and other message details
        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now(), null, false, null,videoUrl);

        // Send the message to Firebase Firestore
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Message sent successfully
                        showToast("Video message sent successfully.");
                        messageInput.setText(""); // Clear the message input field
                    } else {
                        // Handle the error
                        showToast("Failed to send video message.");
                    }
                });
    }



}