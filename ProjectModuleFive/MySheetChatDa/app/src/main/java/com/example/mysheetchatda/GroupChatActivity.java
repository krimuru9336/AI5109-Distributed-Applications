package com.example.mysheetchatda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mysheetchatda.Adapter.ChatAdapter;
import com.example.mysheetchatda.Adapter.GroupChatAdapter;
import com.example.mysheetchatda.Models.ChatMessageModel;
import com.example.mysheetchatda.Models.User;
import com.example.mysheetchatda.Util.FirebaseUtil;
import com.example.mysheetchatda.Util.UsernameCallback;
import com.example.mysheetchatda.databinding.ActivityChatPageBinding;
import com.example.mysheetchatda.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

/*
- Name: Adrianus Jonathan Engelbracht
- Matriculation number: 1151826
- Date: 29.03.2024
*/
public class GroupChatActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        final String senderId = auth.getUid();
        String receiverId = getIntent().getStringExtra("userId");
        //String userName = getIntent().getStringExtra("userName");
        binding.userName.setText("Group Chat");

        binding.backArrowChatGr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // chat message model
        final ArrayList<ChatMessageModel> chatMessageModels = new ArrayList<>();
        // groupChatAdapter instead of single chat adapter
        final GroupChatAdapter groupChatAdapter = new GroupChatAdapter(chatMessageModels, this, receiverId);

        binding.chatRecyclerView.setAdapter(groupChatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        // fetch and display messages in group chat
        database.getReference().child("GroupChat")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatMessageModels.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ChatMessageModel chatMsgMod = dataSnapshot.getValue(ChatMessageModel.class);
                            chatMsgMod.setMessageId(dataSnapshot.getKey());
                            chatMessageModels.add(chatMsgMod);
                        }
                        groupChatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(GroupChatActivity.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
                    }
                });

        // handling of sending text messages in group chat
        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String messageText = binding.enterMessage.getText().toString();
                if (!messageText.isEmpty()) {
                    // Use FirebaseUtil to fetch the current username
                    FirebaseUtil.getCurrentUserName(FirebaseAuth.getInstance(), FirebaseDatabase.getInstance(), new UsernameCallback() {
                        @Override
                        public void onUsernameFetched(String username) {
                            // Now that you have the username, create the ChatMessageModel
                            final ChatMessageModel chatMessageModel = new ChatMessageModel(senderId, messageText, new Date().getTime(),username);
                            // Proceed to send the message
                            database.getReference().child("GroupChat")
                                    .push()
                                    .setValue(chatMessageModel)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //Toast.makeText(GroupChatActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                                            binding.enterMessage.setText(""); // Clear the input field after sending
                                        }
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(GroupChatActivity.this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    });
                }
            }
        });
    }


//    private String fetchSenderNameAndSendMessage(final String messageText) {
//        String usernameGot = "";
//        FirebaseUtil.getCurrentUserName(FirebaseAuth.getInstance(), FirebaseDatabase.getInstance(), new UsernameCallback() {
//            @Override
//            public void onUsernameFetched(String username) {
//                Toast.makeText(GroupChatActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
//            }
//
//        });
//
//        return "Test";
//    }




}