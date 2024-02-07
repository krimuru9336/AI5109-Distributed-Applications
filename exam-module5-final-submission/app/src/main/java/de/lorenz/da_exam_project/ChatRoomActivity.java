package de.lorenz.da_exam_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.List;

import de.lorenz.da_exam_project.adapters.ChatRoomRecyclerAdapter;
import de.lorenz.da_exam_project.listeners.chatroom.AddMediaButtonClickListener;
import de.lorenz.da_exam_project.listeners.chatroom.SendButtonClickListener;
import de.lorenz.da_exam_project.models.ChatMessage;
import de.lorenz.da_exam_project.models.ChatRoom;
import de.lorenz.da_exam_project.models.User;
import de.lorenz.da_exam_project.utils.AndroidUtil;
import de.lorenz.da_exam_project.utils.ChatRoomUtil;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class ChatRoomActivity extends AppCompatActivity {

    ChatRoom chatRoom;
    ChatRoomRecyclerAdapter adapter;
    RecyclerView chatRoomRecyclerView;
    ImageView backButton;
    TextView chatTitleTextView;
    TextView message;
    ImageView sendButton;
    ImageView addMediaButton;
    ChatMessage longClickedChatMessageModel;
    LinearLayout longClickedChatLayout;
    ChatRoomActions chatRoomActions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        chatRoom = AndroidUtil.getChatRoomFromIntent(getIntent());

        if (chatRoom == null) {
            AndroidUtil.showToast(this, "Chat room not found and unable to create chat room!");
            finish();
            return;
        }

        chatRoomActions = new ChatRoomActions(this, chatRoom.getId());

        chatRoomRecyclerView = findViewById(R.id.chat_room_recycler_view);
        backButton = findViewById(R.id.back_button);
        chatTitleTextView = findViewById(R.id.chat_title);
        message = findViewById(R.id.message);
        sendButton = findViewById(R.id.send_button);
        addMediaButton = findViewById(R.id.add_media_button);

        // register context menu for chat room recycler view
        registerForContextMenu(chatRoomRecyclerView);

        // add click listener on back button
        backButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        setChatTitle();
        setupListeners();
        setupRecyclerView();
    }

    private void setupListeners() {
        // add click listener on send button
        sendButton.setOnClickListener(new SendButtonClickListener(message, FirebaseUtil.getCurrentUserId(), chatRoom));

        // add click listener on add media
        addMediaButton.setOnClickListener(new AddMediaButtonClickListener(this, FirebaseUtil.getCurrentUserId(), chatRoom));
    }

    private void setChatTitle() {

        if (chatRoom.isGroup()) {
            this.chatTitleTextView.setText(chatRoom.getTitle());
        } else {
            List<String> userIds = chatRoom.getUserIds();
            String partnerId = ChatRoomUtil.getPartnerId(userIds);

            FirebaseUtil.getUser(partnerId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User partner = task.getResult().toObject(User.class);
                    chatTitleTextView.setText(partner.getUsername());
                }
            });
        }
    }

    /**
     * Registers the recycler view to the adapter.
     * This will make the recycler view display all messages and updates automatically if a message is added or removed.
     */
    private void setupRecyclerView() {
        Query query = FirebaseUtil.getChatRoomMessagesReference(this.chatRoom.getId()).orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();

        adapter = new ChatRoomRecyclerAdapter(options, this, this.chatRoom);
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
            // disable if message is media

            if (longClickedChatMessageModel.getType() != ChatMessage.Type.TEXT) {
                AndroidUtil.showToast(this, "You can only edit text messages.");
                return false;
            }

            return chatRoomActions.editMessage();
        } else if (item.getItemId() == R.id.delete) {
            return chatRoomActions.deleteMessage();
        }
        return super.onContextItemSelected(item);
    }

    public void setLongClickedChatMessageModel(ChatMessage model) {
        this.longClickedChatMessageModel = model;
    }

    public ChatMessage getLongClickedChatMessageModel() {
        return longClickedChatMessageModel;
    }

    public void setLongClickedChatLayout(LinearLayout longClickedChatLayout) {
        this.longClickedChatLayout = longClickedChatLayout;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri selectedMediaUri = data != null ? data.getData() : null;
            String mediaType = null;
            if (selectedMediaUri != null) {
                mediaType = this.getContentResolver().getType(selectedMediaUri);
            }

            if (mediaType != null) {
                if (mediaType.startsWith("image")) {
                    ChatRoomUtil.uploadMedia(this, this.chatRoom, selectedMediaUri, ChatMessage.Type.IMAGE);
                } else if (mediaType.startsWith("video")) {
                    ChatRoomUtil.uploadMedia(this, this.chatRoom, selectedMediaUri, ChatMessage.Type.VIDEO);
                }
            } else {
                AndroidUtil.showToast(this, "Failed to upload media!");
            }
        }
    }
}