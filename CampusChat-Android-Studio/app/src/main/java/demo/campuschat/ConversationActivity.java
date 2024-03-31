package demo.campuschat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import demo.campuschat.adapter.MessageAdapter;
import demo.campuschat.model.ChatSummary;
import demo.campuschat.model.Group;
import demo.campuschat.model.Message;
import demo.campuschat.model.User;

public class ConversationActivity extends AppCompatActivity implements MessageAdapter.MessageClickListener, MessageAdapter.MessageLongClickListener{

    private String receiver_Id, receiver_Name, groupId, groupName;
    boolean isGroupChat;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private DatabaseReference messagesRef, chatSummaryRef, groupChatSummaryRef, userRef, groupRef;
    private FirebaseUser currentUser;
    private EditText editTextChatBox;
    private RecyclerView recyclerView;

    private ActivityResultLauncher<String> mGetContentImage;
    private ActivityResultLauncher<String> mGetContentVideo;
    private ActivityResultLauncher<String> mGetContentGif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);

        // Firebase real-time database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://campuschat-13dbc-default-rtdb.europe-west1.firebasedatabase.app");
        userRef = database.getReference("users");

        TextView userName= findViewById(R.id.user_info_name);


        isGroupChat = getIntent().getBooleanExtra("IS_GROUP_CHAT", false);
        if (isGroupChat){
            // This is a group chat
            groupId = getIntent().getStringExtra("GROUP_ID");
            groupName = getIntent().getStringExtra("GROUP_NAME");
            setTitle(groupName);
            messagesRef = database.getReference("group_messages").child(groupId);
            groupChatSummaryRef = database.getReference("group_summaries");
            groupRef = database.getReference("groups").child(groupId);
            userName.setText(groupName);
        } else {
            receiver_Id = getIntent().getStringExtra("RECEIVER_ID");
            receiver_Name = getIntent().getStringExtra("RECEIVER_NAME");
            setTitle(receiver_Name);
            messagesRef = database.getReference("messages");
            chatSummaryRef = database.getReference("chat_summaries");
            userName.setText(receiver_Name);
        }

        recyclerView = findViewById(R.id.recycler_view_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList, isGroupChat,this, this);

        recyclerView.setAdapter(adapter);


        editTextChatBox = findViewById(R.id.edittext_chatbox);
        ImageButton buttonSend = findViewById(R.id.button_chatbox_send);
        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton mediaSendButton = findViewById(R.id.send_media_button);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        // Firebase Storage



        loadMessages();

        // Initialize the launchers
        mGetContentImage = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                String mimeType = getContentResolver().getType(uri);
                assert mimeType != null;
                showImagePreviewDialog(uri, mimeType, true);
            }
        });

        mGetContentVideo = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                try {
                    showVideoPreviewDialog(uri, true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        mGetContentGif = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                String mimeType = getContentResolver().getType(uri);
                assert mimeType != null;
                showImagePreviewDialog(uri, mimeType, true);
            }
        });
        // buttons

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ConversationActivity.this, BaseActivity.class);
            startActivity(intent);
            finish();
        });

        buttonSend.setOnClickListener(v -> {
            sendMessage(null, null, null);
        });


        mediaSendButton.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(this, view);
            popup.getMenuInflater().inflate(R.menu.media_upload_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_send_image){
                    mGetContentImage.launch("image/*");
                    return true;
                } else if (id == R.id.action_send_gif){
                    mGetContentGif.launch("image/gif");
                    return true;
                } else if (id == R.id.action_send_video){
                    mGetContentVideo.launch("video/*");
                    return true;
                } else {
                    return false;
                }
            });
            popup.show();
        });

    }

    private void showImagePreviewDialog(Uri imageUri, String mimeType, boolean showSendButton) {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_preview);

        ImageView imageView = dialog.findViewById(R.id.image_preview);
        ImageButton sendButton = dialog.findViewById(R.id.button_chatbox_send);
        ImageButton closeButton = dialog.findViewById(R.id.close_button);

        Log.d("mime", "showImagePreviewDialog: "+mimeType);
        if (mimeType.contains("gif")) {
            // Load and play the GIF
            Glide.with(this)
                    .asGif()
                    .load(imageUri)
                    .into(imageView);
        } else{
            Glide.with(this).load(imageUri).into(imageView);
        }

        // Control the visibility of the send button
        sendButton.setVisibility(showSendButton ? View.VISIBLE : View.GONE);

        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // If showing the send button, set its click listener
        if (showSendButton) {
            sendButton.setOnClickListener(v -> {
                uploadImageToFirebaseStorage(imageUri, mimeType);
                dialog.dismiss(); // Dismiss the dialog after sending
            });
        }

        dialog.show();
    }



    private void uploadImageToFirebaseStorage(Uri imageUri, String mimeType) {
        if (imageUri != null) {
            String userId = currentUser.getUid();
            String partnerId = receiver_Id;

            // Determine file extension based on MIME type
            String fileExtension = mimeType.equals("image/gif") ? ".gif" : ".jpg";
            String fileName = System.currentTimeMillis() + "_media" + fileExtension;
            String path = "images-gifs/" + userId + "_" + partnerId + "/" + fileName;

            StorageReference imageRef = FirebaseStorage.getInstance().getReference(path);

            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    String downloadUrl = downloadUri.toString();
                    // Determine the media type based on MIME type for sending the message
                    Message.MediaType messageType = mimeType.contains("gif") ? Message.MediaType.GIF : Message.MediaType.IMAGE;
                    sendMessage(downloadUrl, null, messageType);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(ConversationActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        }
    }

    private void showVideoPreviewDialog(Uri videoUri, boolean showSendButton) throws IOException {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_video_preview);

        ImageView thumbnailView = dialog.findViewById(R.id.thumbnailView);
        VideoView videoView = dialog.findViewById(R.id.video_preview);
        ImageButton sendButton = dialog.findViewById(R.id.button_send_video);
        ImageButton closeButton = dialog.findViewById(R.id.close_button_vid);
        ImageButton playButton = dialog.findViewById(R.id.play_button);
        ProgressBar loadingSpinner = dialog.findViewById(R.id.videoLoadingProgressBar);


        // Generate thumbnail
        Bitmap thumbnail = generateVideoThumbnail(videoUri);
        Log.d("thumbnail", "showVideoPreviewDialog: "+ thumbnail);
        thumbnailView.setImageBitmap(thumbnail);

        // Initially, show the thumbnail and play button, hide the video view and spinner
        videoView.setVisibility(View.GONE);
        thumbnailView.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.VISIBLE);


        // Play button click listener
        playButton.setOnClickListener(v -> {
            // Show the loading spinner
            loadingSpinner.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.VISIBLE);
            thumbnailView.setVisibility(View.GONE);
            playButton.setVisibility(View.GONE);

            // Load and play video
            videoView.setVideoURI(videoUri);
            videoView.requestFocus();
            videoView.setOnPreparedListener(mp -> {
                videoView.start(); // Auto-play
                // Use a Handler to delay the hiding of the spinner and the setting of the video background
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    videoView.setBackgroundColor(Color.TRANSPARENT);
                    loadingSpinner.setVisibility(View.GONE);
                }, 500); // Delay in milliseconds, adjust based on testing
            });
        });

        videoView.setBackgroundColor(Color.BLACK);

        // Create and attach a MediaController
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView); // Anchor the MediaController to VideoView
        videoView.setMediaController(mediaController);

        closeButton.setOnClickListener(v -> dialog.dismiss());

        sendButton.setOnClickListener(v -> {
            try {
                uploadVideoToFirebaseStorage(videoUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            dialog.dismiss();
        });

        if (!showSendButton) {
            sendButton.setVisibility(View.GONE);
        }

        dialog.show();

    }

    private void uploadVideoToFirebaseStorage(Uri videoUri) throws IOException {
        if (videoUri == null) return;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Video...");
        progressDialog.show();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String fileName = "videos/" + userId + "_" + System.currentTimeMillis() + ".mp4";
        StorageReference videoRef = FirebaseStorage.getInstance().getReference().child(fileName);

        // Generate thumbnail
        Bitmap thumbnail = generateVideoThumbnail(videoUri);
        // Prepare thumbnail for upload (convert to byte array)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumbnailData = baos.toByteArray();
        String thumbnailFileName = "thumbnails/" + userId + "_" + System.currentTimeMillis() + ".jpg";
        StorageReference thumbnailRef = FirebaseStorage.getInstance().getReference().child(thumbnailFileName);

        // Start with uploading the thumbnail
        thumbnailRef.putBytes(thumbnailData)
                .addOnSuccessListener(thumbnailTaskSnapshot -> thumbnailRef.getDownloadUrl().addOnSuccessListener(thumbnailDownloadUri -> {
                    // Thumbnail uploaded, proceed with video upload
                    videoRef.putFile(videoUri)
                            .addOnSuccessListener(taskSnapshot -> videoRef.getDownloadUrl().addOnSuccessListener(videoDownloadUri -> {
                                progressDialog.dismiss();
                                // Here, call sendMessage with both video URL and thumbnail URL
                                sendMessage(videoDownloadUri.toString(), thumbnailDownloadUri.toString(), Message.MediaType.VIDEO);
                            })).addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Video upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }).addOnProgressListener(taskSnapshot -> {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            });
                })).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Thumbnail upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void showContextMenu(View view, Message message, int position) {
        // Logic to show a context menu or a dialog with options

        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.inflate(R.menu.context_menu);

        // Check if the message is an image message
        MenuItem editItem = popup.getMenu().findItem(R.id.edit);
        if (message.getMediaURL() != null && !message.getMediaURL().isEmpty()) {
            // If it's an image message, disable the Edit option
            editItem.setVisible(false);
        } else {
            // If it's a text message, enable the Edit option
            editItem.setVisible(true);
        }

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.edit){
                promptEditMessage(message, position);
                return true;
            } else if (id == R.id.delete){
                deleteMessage(message, position);
                return true;
            } else {
                return false;
            }
        });
        popup.show();
    }

    private void deleteMessage(Message message, int position) {
        String senderId = currentUser.getUid();
        String messageId = message.getMessageId();

        boolean isMediaMessage = message.getMediaType() != null && message.getMediaURL() != null;

        messagesRef.child(messageId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Successfully deleted the message
                    if (isLastMessage(position - 1)) {
                        String deletedMessageText = "This message was deleted";
                        message.setMessageText("This message was deleted");
                        if (isMediaMessage) {
                            // Clear media URL and type for media messages
                            message.setMediaURL(null);
                            message.setThumbnailURL(null);
                            message.setMediaType(null);
                        }
                        message.setTimestamp(System.currentTimeMillis());

                        if (isGroupChat) {
                            updateGroupChatSummary(groupId, message);
                        } else {
                                createOrUpdateChatSummary(senderId, receiver_Id, receiver_Name, message);
                                createOrUpdateChatSummary(receiver_Id, senderId, currentUser.getDisplayName(), message);
                        }
                    }
                })
                .addOnFailureListener( e -> {
                    Toast.makeText(ConversationActivity.this, "Failed to delete message", Toast.LENGTH_SHORT).show();
                });

    }

    private void promptEditMessage(final Message message, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConversationActivity.this);
        builder.setTitle("Edit Message");

        final EditText input = new EditText(ConversationActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(message.getMessageText());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> editMessage(message, position, input.getText().toString()));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private void editMessage(Message message, int position, String newMessageText) {
        String senderId = currentUser.getUid();

        String messageId = message.getMessageId();

        messagesRef.child(messageId).child("messageText").setValue(newMessageText)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated the message
                    message.setMessageText(newMessageText);
                    adapter.notifyItemChanged(position);

                    if (isGroupChat) {
                        updateGroupChatSummary(groupId, message);
                    } else {
                        fetchUserName(senderId, userName -> {
                            createOrUpdateChatSummary(senderId, receiver_Id, receiver_Name, message);
                            createOrUpdateChatSummary(receiver_Id, senderId, userName, message);
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(ConversationActivity.this, "Failed to edit message", Toast.LENGTH_SHORT).show();
                });

    }

    private void loadMessages() {
        String senderId = currentUser.getUid();

        messagesRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        if (isGroupChat) {
                            Log.d("idg", "onDataChange: "+ message.getMessageText());
                            if (message.getGroupId() != null && message.getGroupId().equals(groupId)) {
                                messageList.add(message);
                            }
                        } else {
                            Log.d("idnor", "onDataChange: "+ message.getGroupId());
                            // Check if the message is between the current user and the selected receiver
                            if ((message.getSenderId().equals(senderId) && message.getReceiverId().equals(receiver_Id)) ||
                                    (message.getSenderId().equals(receiver_Id) && message.getReceiverId().equals(senderId))) {
                                messageList.add(message);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(adapter.getItemCount() - 1); // To scroll to the bottom of the list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ConversationActivity", "Database error: " + databaseError.getMessage());
            }
        });

    }

    private void sendMessage(@Nullable String mediaURL, @Nullable String thumbnailURL, @Nullable Message.MediaType mediaType) {
        String messageText = editTextChatBox.getText().toString().trim();

        if (currentUser != null && (!messageText.isEmpty() || mediaURL != null)) {
            String senderId = currentUser.getUid();
            String messageId = messagesRef.push().getKey();

            Message message;
            if (isGroupChat) {
                // For group chats, receiverId can be null
                message = new Message(messageId, senderId, null,  groupId, messageText, System.currentTimeMillis(), mediaURL, thumbnailURL, mediaType);
            } else {
                // For individual chats
                message = new Message(messageId, senderId, receiver_Id, null, messageText, System.currentTimeMillis(), mediaURL, thumbnailURL, mediaType);
            }

            assert messageId != null;
            messagesRef.child(messageId).setValue(message)
                    .addOnSuccessListener(aVoid -> {
                        recyclerView.scrollToPosition(adapter.getItemCount());
                        if (!isGroupChat) {
                            fetchUserName(senderId, userName -> {
                                createOrUpdateChatSummary(senderId, receiver_Id, receiver_Name, message);
                                createOrUpdateChatSummary(receiver_Id, senderId, userName, message);
                            });
                        } else {
                            updateGroupChatSummary(groupId, message);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Log.e("Messaging", "Failed to send message", e);
                    });

            editTextChatBox.setText("");
        }
    }

    private void updateGroupChatSummary(String groupId, Message message) {
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                if (group != null) {
                    List<String> memberIds = group.getMemberIds();
                    String text = getChatSummaryMessage(message);

                    for (String memberId: memberIds) {
                        DatabaseReference groupChatSummaryForUserRef = groupChatSummaryRef.child(memberId).child(groupId);
                        groupChatSummaryForUserRef.runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                ChatSummary chatSummary = mutableData.getValue(ChatSummary.class);
                                if (chatSummary == null) {
                                    chatSummary = new ChatSummary(groupId, groupName, text, System.currentTimeMillis(), true);
                                } else {
                                    chatSummary.setLastMessage(text);
                                    chatSummary.setLastMessageTimestamp(message.getTimestamp());
                                }

                                mutableData.setValue(chatSummary);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                if (error != null) {
                                    Log.e("GroupChatSummaryUpdate", "Database error: " + error.getMessage());
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("GroupFetchError", "Failed to fetch group details", error.toException());
            }
        });

    }


    private void createOrUpdateChatSummary(String userId, String chatPartnerId, String chatPartnerName, Message message) {

        chatSummaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatSummary chatSummary;
                String text = getChatSummaryMessage(message);
                if (dataSnapshot.exists()) {
                    // ChatSummary exists, update it
                    chatSummary = dataSnapshot.getValue(ChatSummary.class);
                    if (chatSummary != null) {
                        chatSummary.setChatPartnerId(chatPartnerId);
                        chatSummary.setChatPartnerName(chatPartnerName);
                        chatSummary.setLastMessage(text);
                        chatSummary.setLastMessageTimestamp(message.getTimestamp());
                    }
                } else {
                    // ChatSummary does not exist, create a new one
                    chatSummary = new ChatSummary(chatPartnerId, chatPartnerName, text, message.getTimestamp(), false);
                }
                chatSummaryRef.child(userId).child(chatPartnerId).setValue(chatSummary);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatSummaryUpdate", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private String getChatSummaryMessage(Message message) {

        if (message.getMediaType() == null ) return message.getMessageText();

        switch (message.getMediaType()){
            case GIF:
                return getString(R.string.GIF_summary);

            case VIDEO:
                return getString(R.string.video_summary);

            case IMAGE:
                return getString(R.string.image_summary);

            default:
                return message.getMessageText();
        }
    }

    private void fetchUserName(String userId, final OnUserNameFetchedListener listener) {
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    listener.onUserNameFetched(user.getUserName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("fetchUserName", "Error fetching user data", databaseError.toException());
                listener.onUserNameFetched(null);
            }
        });
    }

    private Bitmap generateVideoThumbnail(Uri videoUri) throws IOException {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

        try {
            // Set video URI into MediaMetadataRetriever
            mediaMetadataRetriever.setDataSource(this, videoUri);

            // Get a frame from the video
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            // Handle error
            Log.e("Thumbnail Generation", "Could not generate thumbnail", e);
        } finally {
            mediaMetadataRetriever.release();
        }

        return bitmap;
    }


    @Override
    public void onImageClicked(Uri imageUri, String mimeType) {
        showImagePreviewDialog(imageUri, mimeType, false);
    }

    // Implementation of onVideoClicked if you have it
    @Override
    public void onVideoClicked(Uri videoUri) throws IOException {
        // Assuming you have a method or logic to show video preview
        showVideoPreviewDialog(videoUri, false);
    }

    // Implementation of onMessageLongClicked
    @Override
    public void onMessageLongClicked(View view, Message message, int position) {
        showContextMenu(view, message, position);
    }

    interface OnUserNameFetchedListener {
        void onUserNameFetched(String userName);
    }


    private boolean isLastMessage(int position) {
        return position == messageList.size() - 1;
    }
}

