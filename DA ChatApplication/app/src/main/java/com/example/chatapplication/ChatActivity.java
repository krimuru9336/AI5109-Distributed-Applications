package com.example.chatapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
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
import androidx.appcompat.widget.Toolbar;
public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private EditText editTextMessage;
    private Button buttonSend;
    private DatabaseReference messagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recycler_view);
        editTextMessage = findViewById(R.id.edittext_chatbox);
        buttonSend = findViewById(R.id.button_chatbox_send);
        adapter = new MessageAdapter(messageList, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // To scroll to the bottom on data change
        recyclerView.setLayoutManager(layoutManager);

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        messagesRef = database.getReference("messages");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = editTextMessage.getText().toString().trim();
                if (!msg.isEmpty()) {
                    sendMessage(msg);
                    editTextMessage.setText("");
                }
            }
        });

        loadMessages();
    }

    @Override
    public void onMessageClick(final Message message) {
        // Show options to delete or edit
        new AlertDialog.Builder(this)
                .setItems(new String[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // Edit the message
                            editMessage(message);
                        } else {
                            // Delete the message
                            deleteMessage(message);
                        }
                    }
                })
                .show();
    }


    private void editMessage(Message message) {
        final EditText input = new EditText(this);
        input.setText(message.getText());

        new AlertDialog.Builder(this)
                .setTitle("Edit Message")
                .setView(input)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newText = input.getText().toString();
                        message.setText(newText);
                        messagesRef.child(message.getId()).setValue(message);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteMessage(Message message) {
        // Delete message from Firebase
        messagesRef.child(message.getId()).removeValue();
    }
    private void sendMessage(String text, String sender) {
        long timestamp = System.currentTimeMillis();
        DatabaseReference newMessageRef = messagesRef.push();
        String messageId = newMessageRef.getKey();
        Message message = new Message(messageId,text, "John", timestamp); // Ensure 'Message' class has this constructor
        newMessageRef.setValue(message);
    }

    private void loadMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log error or show error message
            }
        });
    }
}
