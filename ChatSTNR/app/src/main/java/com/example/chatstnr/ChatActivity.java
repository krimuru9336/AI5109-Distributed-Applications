package com.example.chatstnr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatstnr.adapter.ChatRecyclerAdapter;
import com.example.chatstnr.adapter.SearchUserRecyclerAdapter;
import com.example.chatstnr.models.ChatMessageModel;
import com.example.chatstnr.models.ChatroomModel;
import com.example.chatstnr.models.UserModel;
import com.example.chatstnr.utils.AndroidUtil;
import com.example.chatstnr.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.checkerframework.checker.units.qual.C;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
*/
public class ChatActivity extends AppCompatActivity {

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
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserid(),otherUser.getUserId());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profile_pic_image_view);
        cancelMessageBtn = findViewById(R.id.message_cancel_btn);
        deleteMessageBtn = findViewById(R.id.message_delete_btn);


        backBtn.setOnClickListener((v)->{
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message, false);
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
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });

        adapter.setOnItemClickListener(new ChatRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ChatMessageModel chatMessage) {
                if(Objects.equals(chatMessage.getSenderId(), FirebaseUtil.currentUserid()) && !chatMessage.isDeleted()){


                    deleteMessageBtn.setVisibility(View.VISIBLE);
                    cancelMessageBtn.setVisibility(View.VISIBLE);

                    cancelMessageBtn.setOnClickListener((v -> {
                        chatMessageModel.setEditable(false);
                        messageInput.setText("");
                        cancelMessageBtn.setVisibility(View.GONE);
                        deleteMessageBtn.setVisibility(View.GONE);
                    }));

                    chatMessageModel= chatMessage;
                    messageInput.setText(chatMessage.getMessage());
                    chatMessageModel.setEditable(true);

                    sendMessageBtn.setOnClickListener((v -> {
                        String message = messageInput.getText().toString().trim();
                        if(message.isEmpty())
                            return;
                        chatMessageModel.setMessage(message);
                        sendMessageToUser(message, chatMessageModel.isEditable());
                        cancelMessageBtn.setVisibility(View.GONE);
                        deleteMessageBtn.setVisibility(View.GONE);

                        adapter.notifyDataSetChanged();
                    }));

                    deleteMessageBtn.setOnClickListener((v -> {

                        String message = "DELETED";

                        chatMessageModel.setMessage(message);
                        sendMessageToUser(message, chatMessageModel.isEditable());
                        cancelMessageBtn.setVisibility(View.GONE);
                        deleteMessageBtn.setVisibility(View.GONE);

                        adapter.notifyDataSetChanged();

                    }));

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
                            Arrays.asList(FirebaseUtil.currentUserid(),otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    void sendMessageToUser(String message, boolean isEdited){

        if(!isEdited){

            chatroomModel.setLastMessageTimestamp(Timestamp.now());
            chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserid());
            chatroomModel.setLastMessage(message);
            FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

            ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtil.currentUserid(),Timestamp.now());
            FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                messageInput.setText("");
                                String documentID = task.getResult().getId();

                                chatMessageModel.setMessageID(documentID);

                                FirebaseUtil.getChatroomMessageReference(chatroomId)
                                        .document(documentID)
                                        .set(chatMessageModel);
//                            sendNotification(message);
                            }
                        }
                    });

        }
        else if(!Objects.equals(message, "DELETED")){
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
                        }
                    });

            chatMessageModel.setEditable(false);
        }else{

            String documentID = chatMessageModel.getMessageID(); // Assuming getMessageID() returns the document ID

            Map<String, Object> updateData = new HashMap<>();
            updateData.put("message", "xxx. This message was deleted .xxx");
            updateData.put("isDeleted", true);

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
                        }
                    });

            chatMessageModel.setEditable(false);

        }
    }


}