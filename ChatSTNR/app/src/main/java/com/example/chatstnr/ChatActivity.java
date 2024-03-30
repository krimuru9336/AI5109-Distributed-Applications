package com.example.chatstnr;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.chatstnr.adapter.ChatRecyclerAdapter;
import com.example.chatstnr.adapter.SearchUserRecyclerAdapter;
import com.example.chatstnr.models.ChatMessageModel;
import com.example.chatstnr.models.ChatroomModel;
import com.example.chatstnr.models.UserModel;
import com.example.chatstnr.utils.AndroidUtil;
import com.example.chatstnr.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.C;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/*import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
*/
public class ChatActivity extends AppCompatActivity implements ChatRecyclerAdapter.OnEditDeleteClickListener {

    ProgressBar progressBar;
    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    ChatMessageModel chatMessageModel;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton cancelMessageBtn;
    ImageButton backBtn;
    ImageButton deleteMessageBtn;
    ImageButton addMediaBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;
    ImageView mediaImageView;
    VideoView mediaVideoView;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;
    String messageType = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserid(), otherUser.getUserId());

        progressBar = findViewById(R.id.chat_progressbar);
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profile_pic_image_view);
        cancelMessageBtn = findViewById(R.id.message_cancel_btn);
        deleteMessageBtn = findViewById(R.id.message_delete_btn);
        addMediaBtn = findViewById(R.id.add_media_btn);
        mediaImageView = findViewById(R.id.media_image_view);
        mediaVideoView = findViewById(R.id.media_video_view);


        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        // Get the URI of the selected media
                        selectedImageUri = result.getData().getData();
                        // Check the MIME type of the selected media
                        String mimeType = getContentResolver().getType(selectedImageUri);
                        // Determine the type of media based on MIME type
                        if (mimeType != null) {
                            if (mimeType.startsWith("image/")) {
                                // If it's an image, load it into the ImageView
                                messageType = "image";
                                mediaImageView.setVisibility(View.VISIBLE);
                                mediaVideoView.setVisibility(View.GONE);
                                messageInput.setVisibility(View.GONE);
                                Glide.with(ChatActivity.this).load(selectedImageUri).into(mediaImageView);
                            } else if (mimeType.startsWith("video/")) {
                                // If it's a video, set the URI to the VideoView and start playing it
                                messageType = "video";
                                mediaImageView.setVisibility(View.GONE);
                                mediaVideoView.setVisibility(View.VISIBLE);
                                messageInput.setVisibility(View.GONE);
                                mediaVideoView.setVideoURI(selectedImageUri);
//                                mediaVideoView.start();
                            } else {
                                // Handle other types of media, such as GIFs
                                // You can add additional logic here based on MIME type
                                Toast.makeText(ChatActivity.this, "Unsupported media type", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    AndroidUtil.showToast(getApplicationContext(), "Please Select Media");
                }
            }
        });

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(this, uri, imageView);
                    }
                });


        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty() && Objects.equals(messageType, "text")) {
                return;
            }

            sendMessageToUser(message, false);
        }));

        addMediaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open the chooser for selecting media
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*"); // Set MIME type to all types of files

                // Create a chooser title
                String chooserTitle = "Select Media";
                // Create a chooser to allow the user to pick between different media types
                Intent chooserIntent = Intent.createChooser(intent, chooserTitle);

                // Launch the chooser
                imagePickLauncher.launch(chooserIntent);
            }
        });
        mediaVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaVideoView.isPlaying()) {
                    // If the video is currently playing, pause it
                    mediaVideoView.pause();
                } else {
                    // If the video is paused or stopped, start playing it
                    mediaVideoView.start();
                }
            }
        });

        getOrCreateChatroomModel();
        setupChatRecyclerView();

    }

    void setupChatRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
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

        adapter.setOnEditDeleteClickListener(this);
    }

    void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    //first time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserid(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    void sendMessageToUser(String message, boolean isEdited) {

        setInProgress(true);

        if (Objects.equals(messageType, "text")) {
            if (!isEdited) {

                chatroomModel.setLastMessageTimestamp(Timestamp.now());
                chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserid());
                chatroomModel.setLastMessage(message);
                FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

                ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserid(), Timestamp.now());
                FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    messageInput.setText("");
                                    String documentID = task.getResult().getId();

                                    chatMessageModel.setMessageID(documentID);
                                    chatMessageModel.setMessageType(messageType);

                                    FirebaseUtil.getChatroomMessageReference(chatroomId)
                                            .document(documentID)
                                            .set(chatMessageModel);
//                            sendNotification(message);
                                }
                                setInProgress(false);
                            }
                        });

            }
            else if (!Objects.equals(message, "DELETED")) {
                String documentID = chatMessageModel.getMessageID(); // Assuming getMessageID() returns the document ID

                Map<String, Object> updateData = new HashMap<>();
                updateData.put("message", message);

                FirebaseUtil.getChatroomMessageReference(chatroomId)
                        .document(documentID)
                        .update(updateData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    messageInput.setText("");
                                } else {
                                    // Handle the update failure
                                }
                                setInProgress(false);
                            }


                        });

                chatMessageModel.setEditable(false);
            }
            else {

                String documentID = chatMessageModel.getMessageID(); // Assuming getMessageID() returns the document ID

                Map<String, Object> updateData = new HashMap<>();
                updateData.put("deleted", true);

                FirebaseUtil.getChatroomMessageReference(chatroomId)
                        .document(documentID)
                        .update(updateData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    messageInput.setText("");
                                } else {
                                    // Handle the update failure
                                }
                                setInProgress(false);
                            }
                        });

                chatMessageModel.setEditable(false);

            }
        }
        else if (Objects.equals(messageType, "image")) {

            if (!isEdited) {

                chatroomModel.setLastMessageTimestamp(Timestamp.now());
                chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserid());
                chatroomModel.setLastMessage(messageType);
                FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

                ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserid(), Timestamp.now());

                chatMessageModel.setMessage("Sending");

                FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    messageInput.setText("");
                                    String documentID = task.getResult().getId();

                                    chatMessageModel.setMessageID(documentID);
                                    chatMessageModel.setMessageType(messageType);

                                    FirebaseUtil.getChatroomMessageReference(chatroomId)
                                            .document(documentID)
                                            .set(chatMessageModel);
//                            sendNotification(message);

                                    //Add Media to storage
                                    FirebaseUtil.getCurrentChatRoomStorageRef(chatroomId, chatMessageModel.getMessageID())
                                            .putFile(selectedImageUri)
                                            .addOnSuccessListener(taskSnapshot -> {
                                                // Get the download URL of the uploaded file
                                                StorageReference storageRef = FirebaseUtil.getCurrentChatRoomStorageRef(chatroomId, chatMessageModel.getMessageID());
                                                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                    // Handle the download URL (e.g., store it in Firestore)
                                                    String downloadUrl = uri.toString();
                                                    // Now you can save this URL in Firestore along with other message details
                                                    chatMessageModel.setMessageUrl(downloadUrl); // Set the media URL in your ChatMessageModel

                                                    //TO check DELETEEEE
                                                    chatMessageModel.setMessage(downloadUrl);
                                                    // Save the ChatMessageModel in Firestore
                                                    // For example:
                                                    FirebaseUtil.getChatroomMessageReference(chatroomId).document(chatMessageModel.getMessageID()).set(chatMessageModel);

                                                    messageType = "text";
                                                    mediaImageView.setVisibility(View.GONE);
                                                    mediaVideoView.setVisibility(View.GONE);
                                                    messageInput.setVisibility(View.VISIBLE);
                                                    setInProgress(false);
                                                    AndroidUtil.showToast(getApplicationContext(), "Sent");

                                                }).addOnFailureListener(exception -> {
                                                    // Handle any errors getting the download URL
                                                });
                                            })
                                            .addOnFailureListener(exception -> {
                                                // Handle unsuccessful uploads
                                            });

                                }
                            }
                        });

            }

            AndroidUtil.showToast(getApplicationContext(), "Sending");

        }

        else  {

            if (!isEdited) {

                chatroomModel.setLastMessageTimestamp(Timestamp.now());
                chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserid());
                chatroomModel.setLastMessage(messageType);
                FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

                ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserid(), Timestamp.now());

                chatMessageModel.setMessage("Sending");

                FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    messageInput.setText("");
                                    String documentID = task.getResult().getId();

                                    chatMessageModel.setMessageID(documentID);
                                    chatMessageModel.setMessageType(messageType);

                                    FirebaseUtil.getChatroomMessageReference(chatroomId)
                                            .document(documentID)
                                            .set(chatMessageModel);
//                            sendNotification(message);
                                    Log.d("FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)", " onComplete");

                                    //Add Media to storage
                                    FirebaseUtil.getCurrentChatRoomStorageRef(chatroomId, chatMessageModel.getMessageID())
                                            .putFile(selectedImageUri)
                                            .addOnSuccessListener(taskSnapshot -> {
                                                // Get the download URL of the uploaded file
                                                StorageReference storageRef = FirebaseUtil.getCurrentChatRoomStorageRef(chatroomId, chatMessageModel.getMessageID());
                                                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                    // Handle the download URL (e.g., store it in Firestore)
                                                    String downloadUrl = uri.toString();
                                                    // Now you can save this URL in Firestore along with other message details
                                                    chatMessageModel.setMessageUrl(downloadUrl); // Set the media URL in your ChatMessageModel

                                                    //TO check DELETEEEE
                                                    chatMessageModel.setMessage(downloadUrl);
                                                    // Save the ChatMessageModel in Firestore
                                                    // For example:
                                                    FirebaseUtil.getChatroomMessageReference(chatroomId).document(chatMessageModel.getMessageID()).set(chatMessageModel);

                                                    messageType = "text";
                                                    mediaImageView.setVisibility(View.GONE);
                                                    mediaVideoView.setVisibility(View.GONE);
                                                    messageInput.setVisibility(View.VISIBLE);
                                                    setInProgress(false);
                                                    AndroidUtil.showToast(getApplicationContext(), "Sent");

                                                }).addOnFailureListener(exception -> {
                                                    // Handle any errors getting the download URL
                                                });
                                            })
                                            .addOnFailureListener(exception -> {
                                                // Handle unsuccessful uploads
                                            });

                                }
                            }
                        });

            }

            AndroidUtil.showToast(getApplicationContext(), "Sending");

        }
    }


    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            cancelMessageBtn.setVisibility(View.GONE);
            deleteMessageBtn.setVisibility(View.GONE);
            sendMessageBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            sendMessageBtn.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void OnEditDeleteClick(ChatMessageModel chatMessage) {

        Log.d("ChatActivity", "OnEditDeleteClick: ");

        mediaImageView.setVisibility(View.GONE);
        mediaVideoView.setVisibility(View.GONE);
        messageInput.setVisibility(View.VISIBLE);

        if (Objects.equals(chatMessage.getSenderId(), FirebaseUtil.currentUserid())) {

            if (!messageInput.getText().toString().equals("")) {
                messageInput.setText("");

            }

            if (!chatMessage.isDeleted()) {
                deleteMessageBtn.setVisibility(View.VISIBLE);
                cancelMessageBtn.setVisibility(View.VISIBLE);

                cancelMessageBtn.setOnClickListener((v -> {
                    chatMessageModel.setEditable(false);
                    messageInput.setText("");
                    cancelMessageBtn.setVisibility(View.GONE);
                    deleteMessageBtn.setVisibility(View.GONE);
                    mediaImageView.setVisibility(View.GONE);
                    mediaVideoView.setVisibility(View.GONE);
                    messageInput.setVisibility(View.VISIBLE);
                }));

                deleteMessageBtn.setOnClickListener((v -> {

                    onDelete();
                    cancelMessageBtn.setVisibility(View.GONE);
                    deleteMessageBtn.setVisibility(View.GONE);

                    adapter.notifyDataSetChanged();

                }));

                chatMessageModel = chatMessage;

                if (Objects.equals(chatMessage.getMessageType(), "image")) {
                    mediaImageView.setVisibility(View.VISIBLE);
                    mediaVideoView.setVisibility(View.GONE);
                    messageInput.setVisibility(View.GONE);
                    Glide.with(ChatActivity.this).load(chatMessage.getMessageUrl()).into(mediaImageView);

                } else if (Objects.equals(chatMessage.getMessageType(), "video")) {

                } else {
                    messageInput.setText(chatMessage.getMessage());
                    chatMessageModel.setEditable(true);

                    sendMessageBtn.setOnClickListener((v -> {
                        String message = messageInput.getText().toString().trim();
                        if (message.isEmpty())
                            return;
                        chatMessageModel.setMessage(message);
                        sendMessageToUser(message, chatMessageModel.isEditable());
                        cancelMessageBtn.setVisibility(View.GONE);
                        deleteMessageBtn.setVisibility(View.GONE);

                        adapter.notifyDataSetChanged();
                        chatMessageModel.setMessage("");
                        chatMessageModel.setMessageType("text");

                    }));
                }


            } else {
                AndroidUtil.showToast(ChatActivity.this, "This message is deleted");
                messageInput.setText("");
                cancelMessageBtn.setVisibility(View.GONE);
                deleteMessageBtn.setVisibility(View.GONE);
            }
        }

    }

    public void onDelete(){

        String documentID = chatMessageModel.getMessageID(); // Assuming getMessageID() returns the document ID

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("deleted", true);

        FirebaseUtil.getChatroomMessageReference(chatroomId)
                .document(documentID)
                .update(updateData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            messageInput.setText("");
                            cancelMessageBtn.setVisibility(View.GONE);
                            deleteMessageBtn.setVisibility(View.GONE);
                            mediaImageView.setVisibility(View.GONE);
                            mediaVideoView.setVisibility(View.GONE);
                            messageInput.setVisibility(View.VISIBLE);
                        } else {
                            // Handle the update failure
                        }
                        setInProgress(false);
                    }
                });

        chatMessageModel.setEditable(false);

    }
}