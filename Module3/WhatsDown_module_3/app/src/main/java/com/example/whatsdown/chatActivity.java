package com.example.whatsdown;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private EditText editTextMessage;
    private Button buttonSendMessage;
    private ListView listViewChat;
    private DatabaseReference databaseReference;
    private ArrayList<ChatMessage> messagesList;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);
        listViewChat = findViewById(R.id.listViewChat);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("chats");

        // Initialize the messages list and adapter
        messagesList = new ArrayList<>();
        adapter = new ChatAdapter(this, messagesList);
        listViewChat.setAdapter(adapter);

        // Listen for changes in the Firebase Database
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                messagesList.add(chatMessage);
                adapter.notifyDataSetChanged();
                // Scroll to the last message
                listViewChat.setSelection(adapter.getCount() - 1);
            }

            // Other required methods of ChildEventListener
        });

        // Set a click listener for the send button
        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String messageContent = editTextMessage.getText().toString().trim();
        if (!messageContent.isEmpty()) {
            String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            long timestamp = System.currentTimeMillis();
            ChatMessage chatMessage = new ChatMessage(sender, messageContent, timestamp);
            databaseReference.push().setValue(chatMessage);

            // Clear the input field
            editTextMessage.setText("");
        } else {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
        }
    }
}
