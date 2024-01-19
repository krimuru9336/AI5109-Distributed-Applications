package com.example.whatsdown;

public class ChatActivity extends AppCompatActivity {

    private EditText editTextMessage;
    private Button buttonSendMessage;
    private ListView listViewChat;
    private DatabaseReference databaseReference;
    private ArrayAdapter<ChatMessage> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);
        listViewChat = findViewById(R.id.listViewChat);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("chat");

        // Initialize the adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listViewChat.setAdapter(adapter);

        // Listen for changes in the Firebase Database
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                adapter.add(chatMessage);
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
        String message = editTextMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            ChatMessage chatMessage = new ChatMessage(sender, message);
            databaseReference.push().setValue(chatMessage);

            // Clear the input field
            editTextMessage.setText("");
        }
    }

    public ChatMessageExtended(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseReference presenceRef = FirebaseDatabase.getInstance().getReference("users/" + userId + "/online");
        presenceRef.onDisconnect().setValue(false);
    }
}

