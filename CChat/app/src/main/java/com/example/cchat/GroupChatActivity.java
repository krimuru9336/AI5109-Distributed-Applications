package com.example.cchat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchat.adapter.GroupChatRecyclerAdapter;
import com.example.cchat.model.ChatMessageModel;
import com.example.cchat.model.ChatRoomModel;
import com.example.cchat.model.SecretsModel;
import com.example.cchat.model.UserModel;
import com.example.cchat.utils.AndroidUtil;
import com.example.cchat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GroupChatActivity extends AppCompatActivity implements GroupChatRecyclerAdapter.OnChatMessageClickListener {

    ChatRoomModel chatRoomModel;
    UserModel currentUserModel;
    List<String> userIds;
    String chatroomId;
    GroupChatRecyclerAdapter adapter;
    EditText messageInput;
    ImageButton sendMsgBtn;
    ImageButton backBtn;
    TextView groupName;
    RecyclerView recyclerView;
    ImageView imageView;
    String msgType;
    String senderName;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;

    public static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        GroupChatActivity.context = this.getApplicationContext();

        messageInput = findViewById(R.id.chat_message_input);
        sendMsgBtn = findViewById(R.id.send_msg_btn);
        backBtn = findViewById(R.id.back_btn);
        groupName = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.groupchat_recycler_view);
        imageView = findViewById(R.id.profile_image_view);

        chatroomId = getIntent().getStringExtra("chatroomId");

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            currentUserModel = task.getResult().toObject(UserModel.class);
            senderName = currentUserModel.getUsername();
        });

        FirebaseUtil.getOtherProfilePicStorageRef(chatroomId).getDownloadUrl()
                .addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()) {
                        Uri uri = task1.getResult();
                        AndroidUtil.setProfilePicture(this, uri, imageView);
                    }
                }).addOnFailureListener(command -> {

                });

        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        sendMsgBtn.setOnClickListener((v) -> {
            String message = messageInput.getText().toString().trim();
            msgType = "text";
            if(message.isEmpty())
                return;

            sendMessageToUser(message);
        });

        setupImagePicker();
        setupListeners();
        getChatroom();
        setupChatRecyclerView();
    }

    void getChatroom(){
        getOrCreateChatMediaRef();
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                groupName.setText(chatRoomModel.getGroupName());
                userIds = chatRoomModel.getUserIds();
            }
        });
    }

    void getOrCreateChatMediaRef() {
        FirebaseUtil.getChatMediaStorageRef(chatroomId).child(chatroomId).getDownloadUrl().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Log.i("ref", "folder found");
            } else {
                StorageReference storageRef = FirebaseUtil.getCurrentStorageRef().child(chatroomId + "/");
                storageRef.child(chatroomId).putBytes(new byte[0]).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()) {
                        Log.i("ref", "created folder");
                    } else {
                        Log.e("ref", "failed to create a folder");
                    }
                });
                Log.e("ref", "folder not found");
            }
        });
    }

    void setupChatRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId).orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new GroupChatRecyclerAdapter(options, getApplicationContext(), this);
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

    void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if(data!=null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            uploadMedia();
                        }
                    }
                }
        );
    }

    @SuppressLint("ClickableViewAccessibility")
    void setupListeners() {
        messageInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    Drawable[] drawables = messageInput.getCompoundDrawables();
                    if(drawables[2] != null) {
                        int rightDrawableEnd = messageInput.getRight() - messageInput.getPaddingRight();
                        int rightDrawableStart = rightDrawableEnd - drawables[2].getIntrinsicWidth();
                        if(event.getRawX() >= rightDrawableStart && event.getRawX() <= rightDrawableEnd) {
                            Log.i("touch", "clicked on attachment");
                            openImagePicker();
                            return true;
                        }
                    } else {
                        Log.i("touch", "else");
                    }
                }
                return false;
            }
        });
    }

    void openImagePicker() {
        ImagePicker.with(this).galleryMimeTypes(new String[]{"image/*"}).compress(512).maxResultSize(512, 512)
                .createIntent(new Function1<Intent, Unit>() {
                    @Override
                    public Unit invoke(Intent intent) {
                        imagePickerLauncher.launch(intent);
                        return null;
                    }
                });
    }

    void sendMessageToUser(String message) {
        updateChatRoom(message);

        FirebaseUtil.getChatroomReference(chatroomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now(), msgType);
        chatMessageModel.setSenderName(currentUserModel.getUsername());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()) {
                            messageInput.setText("");
                            sendNotification(message);
                        }
                    }
                });
    }

    void uploadMedia() {
        Log.d("media", "uploadMedia:" + selectedImageUri);
        if(selectedImageUri != null) {
            Log.d("media", selectedImageUri.getPath().substring(selectedImageUri.getPath().lastIndexOf(".") + 1));
            String filePath = System.currentTimeMillis() + ".jpg";
            FirebaseUtil.getChatMediaStorageRef(chatroomId).child(filePath).putFile(selectedImageUri).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Log.d("media", "uploaded successfully");
                    FirebaseUtil.getChatMediaFileRef(chatroomId, filePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String message = uri.toString();
                            msgType = "media";
                            sendMessageToUser(message);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("media", filePath + "Media Not found");

                        }
                    });
                } else {
                    Log.e("media", "Not found");
                }
            });
        }
    }

    void updateChatRoom(String message) {
        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatRoomModel.setLastMessage(message);
        chatRoomModel.setLastMessageType(msgType);
    }

    void sendNotification(String message) {

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try {
                    JSONObject jsonObject = new JSONObject();

                    JSONObject notificationObject = new JSONObject();
                    notificationObject.put("title", currentUser.getUsername());
                    notificationObject.put("body", message);


                    JSONObject dataObject = new JSONObject();
                    dataObject.put("userId", currentUser.getUserId());

                    jsonObject.put("notification", notificationObject);
                    jsonObject.put("data", dataObject);

                    userIds.forEach(id -> {
                        if(!id.equals(FirebaseUtil.currentUserId())) {
                            FirebaseUtil.getUserFromChatroom(id).get().addOnCompleteListener(task1 -> {
                                if(task.isSuccessful()) {
                                    UserModel userModel = task1.getResult().toObject(UserModel.class);
                                    try {
                                        jsonObject.put("to", userModel.getFcmToken());
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    callApi(jsonObject);
                                }
                            });
                        }
                    });

//                    Log.i("n", "otherUserFcm "+otherUserModel.getFcmToken());
//
//                    jsonObject.put("to", otherUserModel.getFcmToken());
//
//                    callApi(jsonObject);

                } catch (Exception e) {

                }
            }
        });
    }

    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        FirebaseUtil.getSecrets().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                SecretsModel secrets = task.getResult().toObject(SecretsModel.class);

                String apiKey = secrets.getFcmAPIKey();

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .header("Authorization", "Bearer " + apiKey)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.i("F", "Failed Api Call : " + e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.i("F", "Success Api Call");
                    }
                });
            }
        });

    }

    @Override
    public void onChatMessageClicked(ChatMessageModel chatMessage, String messageId) {
        Log.d("ChatActivity", "Clicked on message: " + chatMessage.getMessage());
        showOptionsDialog(chatMessage, messageId);
    }

    private void showOptionsDialog(ChatMessageModel chatMessage, String messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options")
                .setItems(new CharSequence[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // Edit action
                                // Implement logic to allow the user to edit the message
                                showEditDialog(chatMessage, messageId);
                                break;
                            case 1:
                                // Delete action
                                showDeleteConfirmationDialog(chatMessage, messageId);
                                break;
                        }
                    }
                })
                .show();
    }

    private void showEditDialog(ChatMessageModel messageModel, String messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Message");

        // Set up the input
        final EditText input = new EditText(this);
        input.setText(messageModel.getMessage());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String editedMessage = input.getText().toString();
                // Implement logic to save the edited message
                if((editedMessage != "" || editedMessage != " ") && !editedMessage.equals(messageModel.getMessage())) {
                    editMessage(chatroomId, messageId, editedMessage);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void editMessage(String chatroomId, String messageId, String editedMessage) {
        // Assuming you have a method in FirebaseUtil to update the message
        FirebaseUtil.getChatMessageReference(chatroomId, messageId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                ChatMessageModel message = task.getResult().toObject(ChatMessageModel.class);
                if(message != null) {
                    FirebaseUtil.getChatMessageReference(chatroomId, messageId).update("message", editedMessage).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()) {
                            AndroidUtil.showToast(this.getApplicationContext(), "Message updated successfully");
                        } else {
                            AndroidUtil.showToast(this.getApplicationContext(), "Message update failed");
                        }
                    });
                }
            }
        });
    }

    private void showDeleteConfirmationDialog(ChatMessageModel chatMessage, String messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Message");
        builder.setMessage("Are you sure you want to delete this message?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                User clicked "Yes," delete the message
                deleteMessage(chatMessage, messageId);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "No," do nothing
                AndroidUtil.showToast(GroupChatActivity.context, "No");
            }
        });

        builder.show();
    }

    private void deleteMessage(ChatMessageModel chatMessage, String messageId) {
        // Implement the logic to delete the message here
        // For example, remove it from the Firestore database
        FirebaseUtil.getChatroomMessageReference(chatroomId).document(messageId)
                .delete()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        // Message deleted successfully
                        updateLastMessageAndTimestamp();
                        Log.i("deb", "Message deleted successfully");
                    }
                });
    }

    private void updateLastMessageAndTimestamp() {
        FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (!documents.isEmpty()) {
                            DocumentSnapshot lastMessageSnapshot = documents.get(0);
                            ChatMessageModel lastMessageModel = lastMessageSnapshot.toObject(ChatMessageModel.class);
                            if (lastMessageModel != null) {
                                chatRoomModel.setLastMessage(lastMessageModel.getMessage());
                                chatRoomModel.setLastMessageTimestamp(lastMessageModel.getTimestamp());
                                FirebaseUtil.getChatroomReference(chatroomId).set(chatRoomModel);
                            }
                        }
                    }
                });
    }


}