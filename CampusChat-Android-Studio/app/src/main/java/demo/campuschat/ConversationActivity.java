package demo.campuschat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import demo.campuschat.adapter.MessageAdapter;
import demo.campuschat.model.ChatSummary;
import demo.campuschat.model.Message;
import demo.campuschat.model.User;

public class ConversationActivity extends AppCompatActivity {

    private String receiver_Id;
    private String receiver_Name;

    private MessageAdapter adapter;
    private List<Message> messageList;
    private FirebaseDatabase database;
    private DatabaseReference messagesRef;
    private DatabaseReference chatSummaryRef;

    private DatabaseReference userRef;
    FirebaseUser currentUser;

    private EditText editTextChatBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);

        receiver_Id = getIntent().getStringExtra("RECEIVER_ID");
        receiver_Name = getIntent().getStringExtra("RECEIVER_NAME");

        RecyclerView recyclerView = findViewById(R.id.recycler_view_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        editTextChatBox = findViewById(R.id.edittext_chatbox);
        ImageButton buttonSend = findViewById(R.id.button_chatbox_send);
        ImageButton backbutton = findViewById(R.id.back_button);
        TextView userName= findViewById(R.id.user_info_name);

        userName.setText(receiver_Name);

        backbutton.setOnClickListener(v -> {
            Intent intent = new Intent(ConversationActivity.this, BaseActivity.class);
            startActivity(intent);
            finish();
        });

        database = FirebaseDatabase.getInstance("https://campuschat-13dbc-default-rtdb.europe-west1.firebasedatabase.app");
        messagesRef = database.getReference("messages");
        chatSummaryRef = database.getReference("chat_summaries");
        userRef = database.getReference("users");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        loadMessages();

        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {

        String senderId = currentUser.getUid();
        messagesRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        // Check if the message is between the current user and the selected receiver
                        if ((message.getSenderId().equals(senderId) && message.getReceiverId().equals(receiver_Id)) ||
                                (message.getSenderId().equals(receiver_Id) && message.getReceiverId().equals(senderId))) {
                            messageList.add(message);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ConversationActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void sendMessage() {
        String messageText = editTextChatBox.getText().toString().trim();


        if (!messageText.isEmpty() && currentUser != null) {
            String senderId = currentUser.getUid(); //1
            String receiverId = receiver_Id; //2
            String receiverName = receiver_Name; //user2

            DatabaseReference currentUserRef = userRef.child(senderId);

            currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    String senderName ;
                    if (user != null) {
                        senderName = user.getUserName();

                        Message message = new Message(senderId, receiverId, messageText, System.currentTimeMillis());
                        messagesRef.push().setValue(message)
                                .addOnSuccessListener(aVoid -> {
                                    // Check if ChatSummary exists, if not create it
                                    createOrUpdateChatSummary(senderId, receiverId, receiverName, message);
                                    createOrUpdateChatSummary(receiverId, senderId, senderName, message);
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                    Log.e("Messaging", "Failed to send message", e);
                                });

                        editTextChatBox.setText("");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("fetchUser", "Error fetching user data", databaseError.toException());
                }
            });

        }
    }

    private void createOrUpdateChatSummary(String userId, String chatPartnerId, String chatPartnerName, Message message) {

        chatSummaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatSummary chatSummary;
                if (dataSnapshot.exists()) {
                    // ChatSummary exists, update it
                    chatSummary = dataSnapshot.getValue(ChatSummary.class);
                    if (chatSummary != null) {
                        chatSummary.setChatPartnerId(chatPartnerId);
                        chatSummary.setChatPartnerName(chatPartnerName);
                        chatSummary.setLastMessage(message.getMessageText());
                        chatSummary.setLastMessageTimestamp(message.getTimestamp());
                    }
                } else {
                    // ChatSummary does not exist, create a new one
                    chatSummary = new ChatSummary(chatPartnerId, chatPartnerName, message.getMessageText(), message.getTimestamp());
                    // Assuming you have a method to fetch or derive chat partner's name
                }
                chatSummaryRef.child(userId).child(chatPartnerId).setValue(chatSummary);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ChatSummaryUpdate", "Database error: " + databaseError.getMessage());
            }
        });
    }
}