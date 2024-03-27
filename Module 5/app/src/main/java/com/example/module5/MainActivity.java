package com.example.module5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.module5.adapter.MessageAdapter;
import com.example.module5.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements MessageAdapter.AdapterListener{

    // UI components
    private EditText messageInput;
    private Button sendButton;
    private RecyclerView messagesRecyclerView;

    // Firebase
    private DatabaseReference messagesRef;
    private String chatRoomId;
    private FirebaseAuth mAuth;
    private String userId;

    // Message list and adapter
    private List<Message> messageList;
    private MessageAdapter messageAdapter;

    ImageView select_media;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;

    private static final int PICK_GIF_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get chat room ID from intent
        chatRoomId = getIntent().getStringExtra("roomId");
        if (chatRoomId == null) {
            finish(); // Finish activity if no chat room ID is provided
            return;
        }

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        messagesRef = database.getReference("messages").child(chatRoomId);

        // Initialize UI components
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        select_media = findViewById(R.id.select_media);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);

        // Initialize message list and adapter
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, messagesRef,userId,MainActivity.this);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messageAdapter);

        // Display existing messages
        displayMessages();

        // Send button click listener
        sendButton.setOnClickListener(view -> sendMessage());

        // Listen for new messages
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Message newMessage = dataSnapshot.getValue(Message.class);
                if (newMessage != null) {
                    messageList.add(newMessage);
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    messagesRecyclerView.scrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

            // Other overridden methods
        });

        select_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });
    }



    // Method to send a message
    private void sendMessage() {
        String messageText = messageInput.getText().toString();
        if (!messageText.isEmpty()) {
            String messageId = messagesRef.push().getKey();
            if (messageId != null) {
                Message message = new Message(messageId, userId, messageText, "text", System.currentTimeMillis());
                messagesRef.child(messageId).setValue(message);
                messageInput.setText("");
            }
        }
    }

    // Method to display messages in the chat room
    private void displayMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void openFileChooser(String selectPicture, int pickImageRequest) {
        Intent intent = new Intent();
        intent.setType("image/* gif/* video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, selectPicture), pickImageRequest);
    }

    private void pickVideo(String selectPicture, int pickImageRequest) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST  && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            uploadFileToFirebaseStorage(fileUri);
        }
        else if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri videoUri = data.getData();
            uploadVideoToFirebase(videoUri);
        }
        else if (requestCode == PICK_GIF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri gifUri = data.getData();
            uploadGifToFirebase(gifUri);
        }
    }



    private void uploadFileToFirebaseStorage(Uri fileUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");

        // Generate a unique filename using the current timestamp
        String fileName = System.currentTimeMillis() + "." + getFileExtension(fileUri);
        StorageReference fileReference = storageReference.child(fileName);

        // Set up progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();

        fileReference.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    progressDialog.dismiss();
                    // Get the download URL and store it in your database
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        sendMessageWithMedia(downloadUrl, "image"); // Use "video" for videos
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("UPLOADFAILD", "Upload failed: " + e.getMessage());
                })
                .addOnProgressListener(taskSnapshot -> {
                    // Calculate progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress((int) progress);
                });
    }

    private void uploadVideoToFirebase(Uri videoUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("videos");
        final StorageReference videoRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(videoUri));

        // Set up progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();

        videoRef.putFile(videoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    progressDialog.dismiss();
                    videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String videoUrl = uri.toString();
                        // Do something with the video URL (e.g., save it in your database)
                        sendMessageWithMedia(videoUrl, "videos"); // Use "video" for videos
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(taskSnapshot -> {
                    // Calculate progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress((int) progress);
                });
    }

    private void uploadGifToFirebase(Uri gifUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("gifs");
        final StorageReference gifRef = storageReference.child(System.currentTimeMillis() + ".gif");

        // Set up progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();

        gifRef.putFile(gifUri)
                .addOnSuccessListener(taskSnapshot -> {
                    progressDialog.dismiss();
                    gifRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String gifUrl = uri.toString();
                        // Use this URL to display the GIF or store it in your database
                        Log.d("GIF URL", gifUrl);
                        sendMessageWithMedia(gifUrl, "gif");
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(taskSnapshot -> {
                    // Calculate progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress((int) progress);
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void sendMessageWithMedia(String downloadUrl, String image) {
        String messageId = messagesRef.push().getKey();
        if (messageId != null) {
            Message message = new Message(messageId, userId, downloadUrl, image, System.currentTimeMillis());
            messagesRef.child(messageId).setValue(message);
            messageInput.setText("");
        }

    }


    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option")
                .setItems(new CharSequence[]{"Image", "Gif","Video"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            openFileChooser("Select Picture",PICK_IMAGE_REQUEST);
                            break;
                        case 1:
                            openFileChooser("Select GIF", PICK_GIF_REQUEST);
                            break;
                        case 2:
                            pickVideo("Select Video",PICK_VIDEO_REQUEST);

                            break;
                    }
                })
                .create()
                .show();
    }


    @Override
    public void viewVideo(String url) {
        startActivity(new Intent(MainActivity.this,VideoActivity.class).putExtra("url",url));
    }
}
