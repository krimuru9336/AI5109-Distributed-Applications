package de.lorenz.da_exam_project;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.List;

import de.lorenz.da_exam_project.adapters.ChatRoomRecyclerAdapter;
import de.lorenz.da_exam_project.listeners.SendButtonClickListener;
import de.lorenz.da_exam_project.models.ChatMessage;
import de.lorenz.da_exam_project.models.ChatRoom;
import de.lorenz.da_exam_project.models.User;
import de.lorenz.da_exam_project.utils.AndroidUtil;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class ChatRoomActivity extends AppCompatActivity {

    User partner;
    ChatRoomRecyclerAdapter adapter;
    RecyclerView chatRoomRecyclerView;
    ImageView backButton;
    TextView username;
    TextView message;
    ImageView sendButton;
    String chatRoomId;
    ChatRoom chatRoom;
    TextView longClickedChatMessage;
    ChatMessage longClickedChatMessageModel;
    ChatRoomActions chatRoomActions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        partner = AndroidUtil.getUserFromIntent(getIntent());
        chatRoomId = AndroidUtil.getChatRoomId(FirebaseUtil.getCurrentUserId(), partner.getUserId());
        chatRoomActions = new ChatRoomActions(this, chatRoomId);

        chatRoomRecyclerView = findViewById(R.id.chat_room_recycler_view);
        backButton = findViewById(R.id.back_button);
        username = findViewById(R.id.username);
        message = findViewById(R.id.message);
        sendButton = findViewById(R.id.send_button);

        // register context menu for chat room recycler view
        registerForContextMenu(chatRoomRecyclerView);

        // add click listener on back button
        backButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        // set partner username
        username.setText(partner.getUsername());

        loadOrCreateChatRoom();

        setupRecyclerView();
    }

    /**
     * Checks whether the chat room exists and creates it if not.
     */
    private void loadOrCreateChatRoom() {
        FirebaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatRoom = task.getResult().toObject(ChatRoom.class);

                // if chat room doesn't exist, create it
                if (chatRoom == null) {
                    List<String> userIds = Arrays.asList(partner.getUserId(), FirebaseUtil.getCurrentUserId());
                    chatRoom = new ChatRoom(chatRoomId, userIds, Timestamp.now(), "", "", "");

                    // add chat room model to database
                    FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoom);
                }

                // add click listener on send button
                sendButton.setOnClickListener(new SendButtonClickListener(message, FirebaseUtil.getCurrentUserId(), chatRoom));
            }
        });
    }

    /**
     * Registers the recycler view to the adapter.
     * This will make the recycler view display all messages and updates automatically if a message is added or removed.
     */
    private void setupRecyclerView() {
        Query query = FirebaseUtil.getChatRoomMessagesReference(chatRoomId).orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();

        adapter = new ChatRoomRecyclerAdapter(options, this);
        LinearLayoutManagerWrapper linearLayoutManagerWrapper = new LinearLayoutManagerWrapper(this, RecyclerView.VERTICAL, true);
        chatRoomRecyclerView.setLayoutManager(linearLayoutManagerWrapper);
        chatRoomRecyclerView.setAdapter(adapter);
        adapter.startListening();

        // register listener to scroll to bottom when new message is added
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatRoomRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_message_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit) {
            return chatRoomActions.editMessage(item);
        } else if (item.getItemId() == R.id.delete) {
            return chatRoomActions.deleteMessage(item);
        }
        return super.onContextItemSelected(item);
    }

    public void setLongClickedChatMessage(TextView longClickedChatMessage) {
        this.longClickedChatMessage = longClickedChatMessage;
    }

    public void setLongClickedChatMessageModel(ChatMessage model) {
        this.longClickedChatMessageModel = model;
    }

    public ChatMessage getLongClickedChatMessageModel() {
        return longClickedChatMessageModel;
    }
}