package com.example.mysheetchatda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.mysheetchatda.Adapter.ChatAdapter;
import com.example.mysheetchatda.Models.Message;
import com.example.mysheetchatda.databinding.ActivityChatPageBinding;
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
- Date: 02.02.2024
*/

public class ChatPage extends AppCompatActivity {


    ActivityChatPageBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");

        binding.userName.setText(userName);

        binding.backArrowChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // message model
        final ArrayList<Message> messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this, receiverId);

        binding.chatRecyclerView.setAdapter((chatAdapter));

        LinearLayoutManager layoutManager = new LinearLayoutManager((this));
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        // identify sender and receiver via id = unique id
        // TODO id + name
        final String senderRoom = senderId + receiverId;
        final String receiverRoom = receiverId + senderId;

        // get snapshot from firebase db
        // notify chatAdapter that message is changed
        // -> enables the messages to be displayed on screen
        database.getReference().child("Chats")
                        .child(senderRoom)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        messageModels.clear();
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                            Message model = snapshot1.getValue(Message.class);
                                            model.setMessageId(snapshot1.getKey());
                                            messageModels.add(model);
                                        }
                                        chatAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


        binding.sendMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String messageText = binding.enterMessage.getText().toString(); // extract text from send textfield
                final Message messageModel = new Message(senderId, messageText); // create new message with the user id + text
                messageModel.setTimestamp(new Date().getTime()); // add timestamp to message
                binding.enterMessage.setText(""); // clear text from input text field

                // generate a unique message id for a message which is the same for sender and receiver room.
                String messageId = database.getReference().child("Chats").push().getKey();


                // adds the message in the sender and the receiver room of the firebase database
                // not actually visible in the app
                database.getReference().child("Chats")
                        .child(senderRoom)
                        .child(messageId)
                        .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("Chats")
                                        .child(receiverRoom)
                                        .child(messageId)
                                        .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });
            }
        });

    }
}