package com.example.chatapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton; // Ensure this import matches the type you're casting to
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private DatabaseReference messagesRef;
    private EditText editTextMessage;
    private ImageButton sendButton; // Ensure this matches the type used in your XML

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recycler_view);
        editTextMessage = findViewById(R.id.edittext_chatbox);
        sendButton = findViewById(R.id.button_chatbox_send); // Make sure button_chatbox_send is an ImageButton in your XML

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(this, messageList, new MessageAdapter.MessageClickListener() {
            @Override
            public void onEditMessageClicked(Message message) {
                showEditMessageDialog(message);
            }

            @Override
            public void onDeleteMessageClicked(Message message) {
                deleteMessage(message);
            }
        });
        recyclerView.setAdapter(adapter);

        messagesRef = FirebaseDatabase.getInstance().getReference("messages");

        sendButton.setOnClickListener(view -> {
            String text = editTextMessage.getText().toString();
            if (!text.trim().isEmpty()) {
                sendMessage(text);
                editTextMessage.setText("");
            }
        });

        loadMessages();
    }

    private void sendMessage(String text) {
        String key = messagesRef.push().getKey();
        Message message = new Message(key, text, "Sender", System.currentTimeMillis());
        if (key != null) {
            messagesRef.child(key).setValue(message);
        }
    }

    private void loadMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    Log.d("ChatActivity", "Message loaded: " + message.getText()); // Debugging
                    messageList.add(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatActivity", "Failed to load messages: " + databaseError.getMessage()); // Error logging
                Toast.makeText(ChatActivity.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showEditMessageDialog(final Message message) {
        final EditText input = new EditText(this);
        input.setText(message.getText());

        new AlertDialog.Builder(this)
                .setTitle("Edit Message")
                .setView(input)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newText = input.getText().toString();
                    message.setText(newText);
                    messagesRef.child(message.getId()).child("text").setValue(newText);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    private void deleteMessage(final Message message) {
        messagesRef.child(message.getId()).removeValue();
    }
}
