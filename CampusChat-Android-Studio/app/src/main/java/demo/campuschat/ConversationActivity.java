package demo.campuschat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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
import demo.campuschat.adapter.MessageLongClickListener;
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
        adapter = new MessageAdapter(messageList, new MessageLongClickListener() {
            @Override
            public void onMessageLongClicked(View view, Message message, int position) {
                // Show a context menu or dialog with Edit/Delete options
                showContextMenu(view, message, position);
            }
        });


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

    private void showContextMenu(View view, Message message, int position) {
        // Logic to show a context menu or a dialog with options

        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.inflate(R.menu.context_menu);
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.edit){
                promptEditMessage(message, position);
                return true;
            } else if (id == R.id.delete){
                deleteMessage(message, position);
                return true;
            } else {
                return false;
            }
        });
        popup.show();
    }

    private void deleteMessage(Message message, int position) {
        String senderId = currentUser.getUid();

        String messageId = message.getMessageId();

        fetchUserName(senderId ,userName -> {
            messagesRef.child(messageId).removeValue()
                    .addOnSuccessListener(aVoid -> {

                        // Successfully deleted the message
                        if (isLastMessage(position - 1)) {
                            message.setMessageText("This message was deleted");
                            message.setTimestamp(System.currentTimeMillis());

                            createOrUpdateChatSummary(senderId, receiver_Id, receiver_Name, message);
                            createOrUpdateChatSummary(receiver_Id, senderId, userName, message);
                            Log.d("user-name", "deleteMessage: "+ userName);
                        }
                    })
                    .addOnFailureListener( e -> {
                        Toast.makeText(ConversationActivity.this, "Failed to delete message", Toast.LENGTH_SHORT).show();
                    });
        });

    }

    private void promptEditMessage(final Message message, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConversationActivity.this);
        builder.setTitle("Edit Message");

        final EditText input = new EditText(ConversationActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(message.getMessageText());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> editMessage(message, position, input.getText().toString()));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private void editMessage(Message message, int position, String newMessageText) {
        String senderId = currentUser.getUid();

        String messageId = message.getMessageId();

        fetchUserName(senderId, userName -> messagesRef.child(messageId).child("messageText").setValue(newMessageText)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated the message
                    message.setMessageText(newMessageText);
                    adapter.notifyItemChanged(position);

                    if (isLastMessage(position)) {
                        createOrUpdateChatSummary(senderId, receiver_Id, receiver_Name, message);
                        createOrUpdateChatSummary(receiver_Id, senderId, userName, message);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(ConversationActivity.this, "Failed to edit message", Toast.LENGTH_SHORT).show();
                }));

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
            String senderId = currentUser.getUid();


            fetchUserName( senderId ,senderName -> {
                String messageId = messagesRef.push().getKey();
                Message message = new Message(messageId, senderId, receiver_Id, messageText, System.currentTimeMillis());
                messagesRef.child(messageId).setValue(message)
                        .addOnSuccessListener(aVoid -> {
                            // Check if ChatSummary exists, if not create it
                            createOrUpdateChatSummary(senderId, receiver_Id, receiver_Name, message);
                            createOrUpdateChatSummary(receiver_Id, senderId, senderName, message);
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                            Log.e("Messaging", "Failed to send message", e);
                        });

                editTextChatBox.setText("");
            });

        }
    }

    private void createOrUpdateChatSummary(String userId, String chatPartnerId, String chatPartnerName, Message message) {

        chatSummaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                }
                chatSummaryRef.child(userId).child(chatPartnerId).setValue(chatSummary);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatSummaryUpdate", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void fetchUserName(String userId, final OnUserNameFetchedListener listener) {
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    listener.onUserNameFetched(user.getUserName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("fetchUserName", "Error fetching user data", databaseError.toException());
                listener.onUserNameFetched(null);
            }
        });
    }

    interface OnUserNameFetchedListener {
        void onUserNameFetched(String userName);
    }


    private boolean isLastMessage(int position) {
        return position == messageList.size() - 1;
    }
}