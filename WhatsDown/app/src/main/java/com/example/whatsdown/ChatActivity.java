package com.example.whatsdown;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whatsdown.adapter.ChatAdapter;
import com.example.whatsdown.model.ChatMessageModel;
import com.example.whatsdown.model.ChatroomModel;
import com.example.whatsdown.model.UserModel;
import com.example.whatsdown.utils.AndroidUtil;
import com.example.whatsdown.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatAdapter adapter;

    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;


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
        ImageButton uploadMediaBtn = findViewById(R.id.upload_media_btn);

        uploadMediaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/* video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri  = t.getResult();
                        AndroidUtil.setProfilePic(this,uri,imageView);
                    }
                });

        backBtn.setOnClickListener((v)->{
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        }));

        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();

            StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("media/" + fileUri.getLastPathSegment());
            UploadTask uploadTask = fileRef.putFile(fileUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Here you can get the download URL of the uploaded file
                            // You can send this URL as a message
                            ChatMessageModel mediaMessage = new ChatMessageModel();
                            mediaMessage.setMessage(uri.toString());
                            mediaMessage.setSenderId(FirebaseUtil.currentUserId());
                            mediaMessage.setTimestamp(Timestamp.now());
                            mediaMessage.setChatroomID(chatroomId);
                            mediaMessage.setMediaType("image");
                            FirebaseUtil.getChatroomMessageReference(chatroomId).add(mediaMessage);
                        }
                    });
                }
            });
        }
    }

    void setupChatRecyclerView(){
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatAdapter(options,getApplicationContext(), new ChatAdapter.ClickListener() {
            @Override
            public void onItemLongClick(int position, View v) {
                Log.d("TAG", "onItemLongClick pos = " + position);
                showOptionsDialog(v, position);

            }
        });
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.e("", "onCreateContextMenu");

        // This method will no longer be used for modal dialog
        // Keep it empty or remove it if not needed for other purposes
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // This method will no longer be used for modal dialog
        // Keep it empty or remove it if not needed for other purposes
        Log.e("", "onContextItemSelected");

        return false;
    }

    public void showOptionsDialog(View view, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select an option")
                .setItems(R.array.options_array, (dialog, which) -> {
                    // Handle the selected option
                    switch (which) {
                        case 0:
                            // Handle option 1
                            Log.e("EDIT DELETE", "edit message");

                            AlertDialog.Builder builderEdit = new AlertDialog.Builder(this);
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.dialog_editmessage_input, null);
                            builderEdit.setView(dialogView);

                            EditText editTextMessageInput = dialogView.findViewById(R.id.editTextMessageInput);
                            ChatMessageModel chatMessage = adapter.getItem(position);
                            editTextMessageInput.setText(chatMessage.getMessage());
                            Button buttonSave = dialogView.findViewById(R.id.buttonSave);

                            AlertDialog dialogEdit = builderEdit.create();
                            buttonSave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Get the message from the EditText
                                    String newMessage = editTextMessageInput.getText().toString();

                                    // Update the ChatMessageModel
                                    ChatMessageModel chatMessage = adapter.getItem(position);
                                    editTextMessageInput.setText(chatMessage.getMessage());

                                    if (chatMessage != null) {
                                        chatMessage.setMessage(newMessage);
                                        FirebaseUtil.updateChatMessage(chatroomId, chatMessage.getSenderId(), chatMessage.getTimestamp(), newMessage);
                                        adapter.notifyItemChanged(position);
                                        adapter.notifyDataSetChanged();

                                    }

                                    // Dismiss the dialog
                                    dialogEdit.dismiss();
                                }
                            });

                            dialogEdit.show();
                            break;
                        case 1:
                            // Handle option 2
                            Log.e("EDIT DELETE", "delete message");
                            ChatMessageModel chatMessageToDelete = adapter.getItem(position);
                            FirebaseUtil.deleteChatMessage(chatroomId, chatMessageToDelete.getSenderId(), chatMessageToDelete.getTimestamp());
                            adapter.notifyItemRemoved(position);
                            adapter.notifyDataSetChanged();
                            break;
                        // Add more cases for other options if needed
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void sendMessageToUser(String message){

        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtil.currentUserId(),Timestamp.now(), chatroomId, "text");
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");
                            sendNotification(message);
                        }
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
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    void sendNotification(String message){

       FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               UserModel currentUser = task.getResult().toObject(UserModel.class);
               try{
                   JSONObject jsonObject  = new JSONObject();

                   JSONObject notificationObj = new JSONObject();
                   notificationObj.put("title",currentUser.getUsername());
                   notificationObj.put("body",message);

                   JSONObject dataObj = new JSONObject();
                   dataObj.put("userId",currentUser.getUserId());

                   jsonObject.put("notification",notificationObj);
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
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer YOUR_API_KEY")
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
}