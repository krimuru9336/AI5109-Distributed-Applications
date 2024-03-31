package com.example.mychatapplication.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mychatapplication.R;
import com.example.mychatapplication.adapters.MessageAdapter;
import com.example.mychatapplication.databinding.ActivityConversationBinding;
import com.example.mychatapplication.listeners.ConversionListener;
import com.example.mychatapplication.models.Chat;
import com.example.mychatapplication.models.Message;
import com.example.mychatapplication.models.User;
import com.example.mychatapplication.network.ApiClient;
import com.example.mychatapplication.network.ApiService;
import com.example.mychatapplication.utilities.Constants;
import com.example.mychatapplication.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationActivity extends BaseActivity
        implements ConversionListener {
    private String chatId;
    private String otherUserId;
    private String chatType;
    private String groupName;
    private String contentType;
    private String otherUserImage;
    private String groupImage;
    private PreferenceManager preferenceManager;
    List<Message> messages = new ArrayList<>();


    String otherUserToken;

    private ActivityConversationBinding binding;

    private MessageAdapter messageAdapter;
    private FirebaseFirestore db;
    List<String> selectedUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(this);
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();


        // Retrieve chat information from Intent extras
        Intent intent = getIntent();
        if (intent != null) {
            chatId = intent.getStringExtra("chatId");
            otherUserId = intent.getStringExtra("otherUserId");
            chatType = intent.getStringExtra("chatType");
            groupName = intent.getStringExtra("groupName");
            groupImage = intent.getStringExtra("groupImage");
        }

        if(chatId!=null){
            if(chatType.equals("group")){
                setName(groupName);
            }else{
                getUserDetails(otherUserId);
            }

            // Listen for messages
            listenForMessages();
            listenAvailabilityOfReceiver();
        }else{

            loading(false);
            Chat newChat = new Chat();

            // Set additional fields
            newChat.setTimestamp(new Date()); // Set current timestamp
            newChat.setLastMessage("");
            if(chatType.equals("group")){
                selectedUsers.addAll( intent.getStringArrayListExtra(Constants.KEY_SELECTED_USERS));
                selectedUsers.add(getCurrentUserId());
                setName(groupName);
                newChat.setName(groupName);
                newChat.setImage(groupImage);
                newChat.setType("group");
                newChat.setParticipants(selectedUsers); // Add current user as the first participant
                createNewChat(newChat);
            }else{
                User user=(User) getIntent().getParcelableExtra(Constants.KEY_USER);
                setName(user.getName());
                newChat.setName("");
                newChat.setImage("");
                newChat.setType("user");
                newChat.setParticipants(Arrays.asList(getCurrentUserId(),user.getId())); // Add current user as the first participant
                checkExistingChat(newChat,user.getId());
            }


        }




        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Initialize RecyclerView and adapter
        messageAdapter = new MessageAdapter(this,messages,this);
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecyclerView.setAdapter(messageAdapter);



        // Set click listener for send button
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageContent = binding.inputMessage.getText().toString();
                if (!TextUtils.isEmpty(messageContent)) {
                    if(chatId!=null){
                        contentType="text";
                        sendMessage(messageContent);
                    }
                }
            }
        });

        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMediaResultLauncher.launch(new String[]{"image/*", "video/*"});
            }
        });

    }


    // Define a result launcher for picking videos
    private final ActivityResultLauncher<String[]> pickMediaResultLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        String fileType = getContentResolver().getType(result);
                        if (fileType != null && (fileType.startsWith("image/") || fileType.startsWith("video/"))) {
                            // The picked file is either an image or a video
                            if (fileType.startsWith("image/")) {
                                // Upload video to Firebase Storage
                                contentType="image";

                            } else {
                                // Handle video
                                contentType="video";
                            }

                            // Upload video to Firebase Storage
                            uploadMediaToStorage(result);
                        }

                    }
                }
            }
    );


    private void checkExistingChat(Chat chat,String otherUserId) {
        db.collection("chats")
                .whereArrayContains("participants", preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Chat> chats = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Chat chat = documentSnapshot.toObject(Chat.class);
                            if (chat != null&&chat.getType().equals("user") && chat.getParticipants().contains(otherUserId)) {
                                // Found chat where both users are participants
                                chats.add(chat);
                            }
                        }
                        if (!chats.isEmpty()) {
                            // Existing chat found, get the chat details
                            Chat existingChat = chats.get(0);
                            chatId = existingChat.getId();
                            listenForMessages();
                        } else {
                            // No existing chat found, create a new chat
                            createNewChat(chat);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to query Firestore, handle error
                    }
                });
    }


    // Method to upload video to Firebase Storage
    private void uploadMediaToStorage(Uri mediaUri) {
        showProgressDialog();
        loading(true);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("videos")
                .child(System.currentTimeMillis() + "_" + mediaUri.getLastPathSegment());
        UploadTask uploadTask = storageRef.putFile(mediaUri);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            dismissProgressDialog();
                            String videoUrl = uri.toString();
                            sendMessage(videoUrl); // Send video URL to Firestore
                        }
                    });
                } else {
                    dismissProgressDialog();
                    loading(false);
                    showToast("Failed to upload video");
                }
            }
        });
    }

    private void setName(String name){
        binding.textName.setText(name);
    }

    ListenerRegistration listener = null;
    boolean initialLoaded = false;

    private void listenForMessages() {
        // Query messages for the chat ID and listen for new messages
        Query query = db.collection("chats").document(chatId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        // Check if messages list is not empty
        if (initialLoaded && !messages.isEmpty()) {
            query = query.startAfter(messages.get(messages.size() - 1).getTimestamp());
        }

        listener = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                Log.d("ConversationActivity","on event");
                loading(false);
                if (e != null) {
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Message message = doc.toObject(Message.class);
                        Log.d("ConversationActivity",message.getContent());
                        // Check if the message is already in the list
                        if (!messages.contains(message)) {
                            message.setSenderProfileImageUrl(otherUserImage);
                            messages.add(message);
                            messageAdapter.notifyItemInserted(messages.size() - 1);
                        }
                    }
                    // Update initialLoaded flag after processing initial messages
                    if (!initialLoaded) {
                        initialLoaded = true;
                        listener.remove();
                        // Call listenForMessages() again after initial messages are loaded
                        listenForMessages();
                    }
                    binding.chatRecyclerView.scrollToPosition(messages.size() - 1);
                }
            }
        });
    }



    private void sendMessage(String messageContent) {
        // Create a new message object
        Message message = new Message();
        message.setContent(messageContent);
        message.setContentType(contentType);
        message.setSenderId(getCurrentUserId());
        message.setTimestamp(new Date());

        DocumentReference documentReference=db.collection("chats").document(chatId).collection("messages").document();
        message.setMessageId(documentReference.getId());
        // Add the message to the Firestore database
        documentReference
                .set(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateLastMessage(messageContent);
                        prePareNotification(message.getMessageId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void prePareNotification(String messageId){
        if (!isReceiverAvailable){
            try {
                JSONArray tokens = new JSONArray();
                tokens.put(otherUserToken);

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_MESSAGE_ID, messageId);
                data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());

                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA, data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

                sendNotifications(body.toString());
            } catch(Exception exception){
                showToast(exception.getMessage());
            }
        }

        binding.inputMessage.setText("");

    }

    private void updateLastMessage(String message) {
        String lastMessage=message;
        if(contentType.equals("video")){
            lastMessage="Video";
        }else if(contentType.equals("image")){
            lastMessage="Image";
        }

        HashMap<String , Object> updates = new HashMap<>();
        updates.put("lastMessage", lastMessage);
        updates.put("timestamp", new Date());



        // Update the last message field in the chat document
        db.collection("chats").document(chatId)
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Last message updated successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                    }
                });
    }

    private String getCurrentUserId() {
        // Implement code to get the current user's ID
        return preferenceManager.getString(Constants.KEY_USER_ID);
    }

    private void getUserDetails(String userId) {
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        otherUserToken=user.getFcmToken();
                        binding.textName.setText(user.getName());
                        otherUserImage=user.getImage();
                        loadMessagesWithSenderImage(user.getImage());
                    }
                } else {
                    // Error occurred while retrieving user details
                    Exception exception = task.getException();
                    // Handle error
                }
            }
        });
    }

    private void loadMessagesWithSenderImage(String senderImageUrl) {
        // Iterate through the messages list and update each message with the sender's image URL
        for (Message message : messages) {
            message.setSenderProfileImageUrl(senderImageUrl);
        }

        // Notify the adapter that the data set has changed
        messageAdapter.notifyDataSetChanged();
    }

    private void createNewChat(Chat newChat) {
        // Create a new chat document
        final DocumentReference newChatRef = db.collection("chats").document();
        final String newChatId = newChatRef.getId();
        newChat.setId(newChatId);

        // Add the chat to Firestore
        newChatRef.set(newChat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Chat created successfully, update chatId and send the message
                chatId = newChatId;
                listenForMessages();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to create chat, handle error
            }
        });
    }

    boolean isReceiverAvailable=false;
    private void listenAvailabilityOfReceiver(){
        db.collection(Constants.KEY_COLLECTION_USERS).document(otherUserId)
                .addSnapshotListener(ConversationActivity.this, (value, error) -> {
                    if(error != null){
                        return;
                    }
                    if(value != null){
                        if (value.getLong(Constants.KEY_AVAILABILITY) != null){
                            int availability = Objects.requireNonNull(
                                    value.getLong(Constants.KEY_AVAILABILITY)
                            ).intValue();
                            isReceiverAvailable = availability == 1;
                        }
                    }
                    if(isReceiverAvailable){
                        binding.textAvailability.setVisibility(View.VISIBLE);
                    } else {
                        binding.textAvailability.setVisibility(View.GONE);
                    }
                });
    }

    private void sendNotifications(String messageBody){
       ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()){
                    try {
                        if (response.body() != null){
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if(responseJson.getInt("Failure") ==1){
                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }
                   } catch (JSONException e){
                        e.printStackTrace();
                    }
                    showToast("Notification sent successfully");
                }else {
                    showToast("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
                showToast(t.getMessage());
            }
        });
   }

    private void showToast(String message){
     Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }



    private void showEditMessageDialog(final Message message,int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_dialog, null);
        builder.setView(dialogView);

        EditText editTextMessage = dialogView.findViewById(R.id.editTextMessage);

        // Retrieve the current message content from Firebase Firestore
        DocumentReference messageRef = db.collection("chats").document(chatId)
                .collection("messages").document(message.getMessageId());
        messageRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Message message = documentSnapshot.toObject(Message.class);
                    if (message != null) {
                        editTextMessage.setText(message.getContent());
                    }
                }
            }
        });

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedContent = editTextMessage.getText().toString().trim();
                if (!TextUtils.isEmpty(updatedContent)) {
                    message.setContent(updatedContent);
                    // Update the message in Firebase Firestore
                    updateMessage(message,position);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onEdit(int position) {
        showEditMessageDialog(messages.get(position),position);
    }

    @Override
    public void onDelete(int position) {
        deleteMessage(messages.get(position).getMessageId(),position);
    }

    // Function to update a message in Firebase Firestore
    private void updateMessage(Message message,int position) {
        DocumentReference messageRef = db.collection("chats").document(chatId)
                .collection("messages").document(message.getMessageId());
        messageRef.update("content", message.getContent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateLastMessage(message.getContent());
                        messages.set(position,message);
                        messageAdapter.notifyItemChanged(position);

                        // Message updated successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                    }
                });
    }

    // Function to delete a message from Firebase Firestore
    private void deleteMessage(String messageId,int position) {
        DocumentReference messageRef = db.collection("chats").document(chatId)
                .collection("messages").document(messageId);
        messageRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        messages.remove(position);
                        messageAdapter.notifyItemRemoved(position);
                        // Message deleted successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                    }
                });
    }


    private ProgressDialog progressDialog;

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
