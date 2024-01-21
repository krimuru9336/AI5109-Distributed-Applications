package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.letschat.adapter.ChatRecyclerAdapter;
import com.example.letschat.adapter.SearchUserRecyclerAdapter;
import com.example.letschat.model.ChatMessage;
import com.example.letschat.model.ChatRoom;
import com.example.letschat.model.User;
import com.example.letschat.util.AndroidUtil;
import com.example.letschat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    User otherUser;
    EditText messageInput;
    ImageButton sendMsgBtn;
    ImageButton backBtn;
    TextView otherUserView;
    RecyclerView recyclerView;
    String chatRoomId;
    ChatRoom chatRoom;

    ChatRecyclerAdapter chatRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //getUser
        otherUser = AndroidUtil.getUserFromIntent(getIntent());
        chatRoomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserId(), otherUser.getUserId());
        messageInput = findViewById(R.id.chat_msg_input);
        sendMsgBtn = findViewById(R.id.send_message_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUserView = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);

        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });

        otherUserView.setText(otherUser.getUsername());


        sendMsgBtn.setOnClickListener(v -> {
            try {
                Log.d("ButtonClick", "Send Message Button Clicked");
                String message = messageInput.getText().toString().trim();
                if (message.isEmpty()) {
                    return;
                }
                sendMessageToUser(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        getOrCreateChatRoom();
        setUpChatRecyclerView();

    }

    void getOrCreateChatRoom() {
        FirebaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatRoom = task.getResult().toObject(ChatRoom.class);
                if (chatRoom == null) {
                    //first time chatting
                    chatRoom = new ChatRoom(
                            chatRoomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoom);
                }
            }
        });
    }

    void sendMessageToUser(String message) {
        chatRoom.setLastMsgTimestamp(Timestamp.now());
        chatRoom.setLastMsgSenderId(FirebaseUtil.currentUserId());
        chatRoom.setLastMsg(message);
        FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoom);

        ChatMessage chatMessage = new ChatMessage(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatMessageReference(chatRoomId).add(chatMessage).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    messageInput.setText("");

                }
            }
        });

    }
    void setUpChatRecyclerView(){
        Query query = FirebaseUtil.getChatMessageReference(chatRoomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class).build();

        chatRecyclerAdapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(chatRecyclerAdapter);
        chatRecyclerAdapter.startListening();
    }
}