package com.example.myapplication5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication5.adapter.ChatRecyclerAdapter;
import com.example.myapplication5.adapter.UserAdapter;
import com.example.myapplication5.utils.AndroidUtil;
import com.example.myapplication5.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    HelperClass sender;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter chat_adapter;
    List<ChatMessageModel> chatMessageList;
    boolean isAdapterSet = false; // Flag to check if the adapter has been set
    String chatroomId;

    ImageButton backButton;
    TextView sender_username;
    RecyclerView recyclerView;

    EditText messageInput;
    ImageButton sendMessageButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("entered chat");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageButton = findViewById(R.id.message_send_btn);
        sender_username = findViewById(R.id.sender_username);
        backButton = findViewById(R.id.back_button);
        recyclerView = findViewById(R.id.chat_recycler_view);

        sender = AndroidUtil.getUserModelFromIntent(getIntent());
       chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),sender.getUserId());


        //button actions
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            // Optional: finish the ChatActivity to remove it from the back stack

        });

        sender_username.setText(sender.getUsername());
        // Initialize the adapter only once when the ChatActivity is created
        List<ChatMessageModel> chatMessageList = new ArrayList<>();
        chat_adapter = new ChatRecyclerAdapter(chatMessageList, this, chatroomId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chat_adapter);

        sendMessageButton.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        }));

        getOrCreateChatroomModel();
        setupChatRecyclerView();


    }


    void setupChatRecyclerView() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference chatsCollection = db.collection("chatrooms").document(chatroomId).collection("chats");

        chatsCollection.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.out.println("Error getting chats: " + error.getMessage());
                    return;
                }

                chat_adapter.clearData();
//                if (chatMessageList)
//                chatMessageList.clear();

                for (QueryDocumentSnapshot document : querySnapshot) {
                    ChatMessageModel chatMessage = document.toObject(ChatMessageModel.class);
                    chat_adapter.addData(chatMessage);
                }

                // Notify your adapter about the data change
                chat_adapter.notifyDataSetChanged();
            }
        });
    }

    void sendMessageToUser(String message){


        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
//messageid
        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now(), messageId);
        //messageid
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");
//                            sendNotification(message);
                        }
                    }
                });
        chat_adapter.notifyDataSetChanged();
    }

    void getOrCreateChatroomModel(){
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel==null){
                    //first time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(),sender.getUserId()),
//                            Arrays.asList(FirebaseUtil.currentUserId(),sender.getUsername()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }


}
