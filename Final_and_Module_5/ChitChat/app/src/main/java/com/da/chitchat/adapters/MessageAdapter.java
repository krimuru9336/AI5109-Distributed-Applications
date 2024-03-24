package com.da.chitchat.adapters;

import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.da.chitchat.Message;
import com.da.chitchat.R;
import com.da.chitchat.UserMessageStore;
import com.da.chitchat.interfaces.OnDataChangedListener;
import com.da.chitchat.singletons.AppContextSingleton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private static List<Message> messageList = null;
    private final String username;
    private final OnDataChangedListener onDataChangedListener;
    private int position;
    private final boolean isGroup;

    public MessageAdapter(List<Message> messageList, String username, OnDataChangedListener listener,
                          boolean isGroup) {
        this.onDataChangedListener = listener;

        MessageAdapter.messageList = messageList;
        this.username = username;
        this.isGroup = isGroup;

        loadAllUserMessages();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.resetState();

        Message message = messageList.get(position);
        holder.bind(message);

        holder.itemView.setOnLongClickListener(v -> {
            setPosition(holder.getAdapterPosition());
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public String currentUser() {
        return username;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public Message getItem(int position) {
        if (position >= 0 && position < messageList.size()) {
            return messageList.get(position);
        }
        return null;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private void loadAllUserMessages() {
        if (messageList != null && messageList.size() > 0) {
            notifyItemRangeInserted(0, messageList.size() - 1);
        }
    }

    public UUID addMessage(Message message, boolean isGroup) {
        if (isGroup) {
            UserMessageStore.addMessageToGroup(username, message);
        } else {
            UserMessageStore.addMessageToUser(username, message);
        }
        showNewMessage();
        return message.getID();
    }

    public void addTimestamp(UUID id, long timestamp, boolean isEditTimestamp) {
        Message foundMessage = findMessageById(id);
        if (foundMessage != null) {
            if (isEditTimestamp) {
                foundMessage.setEditTimestamp(new Date(timestamp));
            } else {
                foundMessage.setTimestamp(timestamp);
            }
            notifyItemChanged(messageList.indexOf(foundMessage));
        }
    }

    public void editMessage(UUID id, String userInput, Date editDate) {
        Message foundMessage = findMessageById(id);
        if (foundMessage != null) {
            foundMessage.setEditTimestamp(editDate);
            editMessage(foundMessage, userInput);
        }
    }

    public void editMessage(Message message, String userInput) {
        if (message.getState() != Message.State.DELETED) {
            message.setText(userInput);
            message.setState(Message.State.EDITED);
            notifyItemChanged(messageList.indexOf(message));
            updateScrollPosition();
        }
    }

    public void deleteMessage(UUID id) {
        Message foundMessage = findMessageById(id);
        if (foundMessage != null) {
            deleteMessage(foundMessage);
        }
    }

    public void deleteMessage(Message message) {
        message.setEditTimestamp(null);
        String deleteMessageText = AppContextSingleton.getInstance().getString(R.string.deleteMessageText);
        message.setText(deleteMessageText);
        message.setState(Message.State.DELETED);
        notifyItemChanged(messageList.indexOf(message));
        updateScrollPosition();
    }

    private Message findMessageById(UUID messageId) {
        for (Message message : messageList) {
            if (message.getID().equals(messageId)) {
                return message;
            }
        }
        return null;
    }

    public void showNewMessage() {
        notifyItemInserted(messageList.size() - 1);
        updateScrollPosition();
    }

    private void updateScrollPosition() {
        if (onDataChangedListener != null) {
            onDataChangedListener.onDataChanged();
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        // Create a SimpleDateFormat to format the timestamp
        SimpleDateFormat dateFormat;
        private final TextView messageTextView;
        private final TextView usernameTextView;
        private final TextView timestampTextView;
        private final TextView editTimestampTextView;
        private final LinearLayout backgroundContainer;
        private final LinearLayout messageContainer;
        private final LinearLayout editInfoContainer;
        private final int standardTextColor;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
            messageTextView = itemView.findViewById(R.id.messageTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            backgroundContainer = itemView.findViewById(R.id.messageBackgroundContainer);
            messageContainer = itemView.findViewById(R.id.messageContainer);
            editInfoContainer = itemView.findViewById(R.id.editInfoContainer);
            editTimestampTextView = itemView.findViewById(R.id.editTimestampTextView);
            standardTextColor = messageTextView.getCurrentTextColor();

            itemView.setOnCreateContextMenuListener(this);
        }

        public void bind(Message message) {
            String username = message.isIncoming() ? message.getSender() : "You";
            usernameTextView.setText(username);

            ViewGroup.LayoutParams layoutParams = usernameTextView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            usernameTextView.setLayoutParams(layoutParams);

            messageTextView.setText(message.getText());

            // Format the timestamp and set it to the timestampTextView
            timestampTextView.setText(dateFormat.format(message.getTimestamp()));

            // Set background based on message type (incoming or outgoing)
            int backgroundResource = message.isIncoming() ?
                    R.drawable.incoming_message_background : R.drawable.outgoing_message_background;

            backgroundContainer.setBackgroundResource(backgroundResource);

            int curGrav = message.isIncoming() ? Gravity.START : Gravity.END;
            messageContainer.setGravity(curGrav);

            if (message.getState() == Message.State.DELETED) {
                int textColor = ContextCompat.getColor(itemView.getContext(), R.color.grey);
                messageTextView.setTextColor(textColor);
            } else if (message.getState() == Message.State.EDITED) {
                editInfoContainer.setVisibility(View.VISIBLE);
                editTimestampTextView.setText(dateFormat.format(message.getEditTimestamp()));
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Message selectedMessage = messageList.get(position);

                if (selectedMessage != null) {
                    boolean isDeleted = (selectedMessage.getState() == Message.State.DELETED);

                    MenuItem editItem = menu.add(0, R.id.menu_edit, 0, "Edit");
                    editItem.setEnabled(!isDeleted && !selectedMessage.isIncoming());

                    MenuItem deleteItem = menu.add(0, R.id.menu_delete, 1, "Delete");
                    deleteItem.setEnabled(!isDeleted);
                }
            }
        }

        public void resetState() {
            editInfoContainer.setVisibility(View.GONE);
            messageTextView.setTextColor(standardTextColor);
        }
    }
}
