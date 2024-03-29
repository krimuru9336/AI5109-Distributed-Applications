package com.example.mysheetchatda;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.mysheetchatda.Adapter.ChatAdapter;
import com.example.mysheetchatda.Models.ChatMessageModel;
import com.example.mysheetchatda.databinding.ActivityChatPageBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

import android.Manifest;


/*
- Name: Adrianus Jonathan Engelbracht
- Matriculation number: 1151826
- Date: 02.02.2024
*/

public class ChatPage extends AppCompatActivity {


    ActivityChatPageBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    private static final int PICK_IMAGE_REQUEST = 1;

    private static final int PICK_FILE_REQUEST = 2;
    private static final int YOUR_PERMISSIONS_REQUEST_READ_STORAGE = 11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        final String senderId = auth.getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        binding.userName.setText(userName);

        binding.backArrowChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // chat message model
        final ArrayList<ChatMessageModel> chatMessageModels = new ArrayList<>();
        // chat adapter
        final ChatAdapter chatAdapter = new ChatAdapter(chatMessageModels, this, receiverId);

        binding.chatRecyclerView.setAdapter((chatAdapter));

        LinearLayoutManager layoutManager = new LinearLayoutManager((this));
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        // identify sender and receiver via id = unique id
        final String senderRoom = senderId + receiverId;
        final String receiverRoom = receiverId + senderId;

        // get snapshot from firebase db
        // notify chatAdapter that message is changed
        // -> enables the messages to be displayed
        database.getReference().child("Chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatMessageModels.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ChatMessageModel chatMsgMod = dataSnapshot.getValue(ChatMessageModel.class);
                            chatMsgMod.setMessageId(dataSnapshot.getKey());
                            chatMessageModels.add(chatMsgMod);
                        }
                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        // handling click event for sending text
        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = binding.enterMessage.getText().toString();
                if (!messageText.isEmpty()) {
                    sendMessage(messageText, null, null, null);
                    binding.enterMessage.setText("");
                }
            }
        });

        // handling click event for sending Media
        binding.sendMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageGifVideoChooser();
            }
        });
    }

    // logic for sending messages (text, video, gif, image)
    private void sendMessage(String messageText, String imageUrl, String videoUrl, String gifUrl) {
        final String senderId = FirebaseAuth.getInstance().getUid();
        String receiverId = getIntent().getStringExtra("userId");
        // Define sender and receiver rooms
        final String senderRoom = senderId + receiverId;
        final String receiverRoom = receiverId + senderId;

        ChatMessageModel messageModel;
        if (videoUrl != null && imageUrl == null && messageText == null && gifUrl == null) {
            // sending an video message
            //Toast.makeText(ChatPage.this, "Sending videourl: " + videoUrl, Toast.LENGTH_SHORT).show();
            messageModel = new ChatMessageModel(senderId, null, new Date().getTime(), "", "", videoUrl);
        } else if (videoUrl == null && imageUrl != null && messageText == null && gifUrl == null) {
            // sending an image message
            //Toast.makeText(ChatPage.this, "Sending imageurl: " + imageUrl, Toast.LENGTH_SHORT).show();
            messageModel = new ChatMessageModel(senderId, null, new Date().getTime(), "", imageUrl, "");
        } else if (videoUrl == null && imageUrl == null && messageText == null && gifUrl != null) {
            // sending a gif message
            messageModel = new ChatMessageModel(senderId, null, new Date().getTime(), null, null, null, gifUrl);
        } else {
            // sending an text message
            //Toast.makeText(ChatPage.this, "Sending text: " + messageText, Toast.LENGTH_SHORT).show();
            messageModel = new ChatMessageModel(senderId, messageText, new Date().getTime());
        }

        String messageId = database.getReference().child("Chats").push().getKey();
        database.getReference().child("Chats").child(senderRoom).child(messageId)
                .setValue(messageModel).addOnSuccessListener(aVoid -> {
                    database.getReference().child("Chats").child(receiverRoom).child(messageId)
                            .setValue(messageModel);
                });
    }


    // lets the user choose media to send
    private void openImageGifVideoChooser() {
        // checks permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // if permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, YOUR_PERMISSIONS_REQUEST_READ_STORAGE);
            // do not proceed until permission is granted
            return;
        }
        Intent intent = new Intent();
        intent.setType("*/*");
        // define types of media to choose
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*", "image/gif"});
        //intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        //intent.setType("image/*"); // Set type to select only images
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture,Video or Gif"), PICK_FILE_REQUEST);
        //startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // actions after picking an image -> upload media in specific folder in firebase storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri imageUri = data.getData();
//            // Handle the image URI, e.g., upload it to Firebase Storage
//            uploadImage(imageUri);
//        }

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            String mimeType = getContentResolver().getType(fileUri);

            if (mimeType != null) {
                Log.e("ChatPage", "MimeType: " + mimeType);
                if (mimeType.equals("image/gif")) {
                    // Handle GIF selection
                    uploadFile(fileUri, "gifs");
                } else if (mimeType.startsWith("image")) {
                    uploadFile(fileUri, "images");
                } else if (mimeType.startsWith("video")) {
                    uploadFile(fileUri, "videos");
                }
            }
        }

    }


    // uploads different files like images, gifs and videos to firebase storage based on file extension
    // shows progress of upload
    // after upload is complete sends the message
    private void uploadFile(Uri fileUri, String folder) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(folder + "/" + System.currentTimeMillis() + "." + getFileExtension(fileUri));
        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    progressDialog.dismiss();
                    if (folder.equals("videos")) {
                        sendMessage(null, null, uri.toString(), null);
                    } else if (folder.equals("images")) {
                        sendMessage(null, uri.toString(), null, null);
                    } else if (folder.equals("gifs")) {
                        Log.e("ChatPage", "Sending gif with uri: " + uri.toString());
                        sendMessage(null, null, null, uri.toString());
                    }
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(ChatPage.this, "Failed to upload file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    // check after granting permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == YOUR_PERMISSIONS_REQUEST_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, open the image chooser
                openImageGifVideoChooser();
            } else {
                // permission was denied
                Toast.makeText(this, "Permission denied to read external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }


}