package com.example.chitchatapp;

import android.annotation.SuppressLint;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private static List<Message> messageList;
    private final String username;
    private final OnDataChangedListener onDataChangedListener;
    private int position;

    public MessageAdapter(List<Message> messageList, String username, OnDataChangedListener listener) {
        this.onDataChangedListener = listener;

        MessageAdapter.messageList = messageList;
        this.username = username;

        loadAllUserMessages();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
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

    private void loadAllUserMessages() {
        if (messageList != null && messageList.size() > 0) {
            notifyItemRangeInserted(0, messageList.size() - 1);
        }
    }

    public void addMessage(Message message) {
        MessageStore.addMessageToUser(username, message);
        showNewMessage();
    }

    public void showNewMessage() {
        notifyItemInserted(messageList.size() - 1);
        if (onDataChangedListener != null) {
            onDataChangedListener.onDataChanged();
        }
    }

//    public void replaceMessage(Message message) {
//        Message foundMessage = findMessageById(message.getId());
//
//        // If the message is found in the list
//        if (foundMessage != null) {
//            // Get the index of the foundMessage in the list
//            int index = messageList.indexOf(foundMessage);
//
//            // Replace the object in the list at the index with the new message
//            if (index != -1) {
//                MessageStore.replaceUserMessage(username, index, message);
//
//                // Notify the adapter that the data has changed for the item
//                notifyItemChanged(index);
//            }
//        }
//    }

    public void notifyMessageChanged(Message message) {
        Message foundMessage = findMessageById(message.getId());

        // If the message is found in the list
        if (foundMessage != null) {
            // Get the index of the foundMessage in the list
            int index = messageList.indexOf(foundMessage);

            // Replace the object in the list at the index with the new message
            if (index != -1) {
                // Notify the adapter that the data has changed for the item
                notifyItemChanged(index);
            }
        }
    }

    private Message findMessageById(UUID messageId) {
        for (Message message : messageList) {
            if (message.getId().equals(messageId)) {
                return message;
            }
        }
        return null;
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

            itemView.setOnCreateContextMenuListener(this);
        }

        public void bind(Message message) {
            messageTextView.setText(message.getMessage());

            String username = message.isIncoming() ? message.getSender() : "You";
            usernameTextView.setText(username);

            // Format the timestamp and set it to the timestampTextView
            timestampTextView.setText(dateFormat.format(message.getTimestamp()));

            // Set background based on message type (incoming or outgoing)
            int backgroundResource = message.isIncoming() ?
                    R.drawable.incoming_message_background : R.drawable.outgoing_message_background;

            if(message.getMessageState() == MessageState.DELETED){
                backgroundResource = message.isIncoming() ?
                        R.drawable.incoming_message_background_opaque : R.drawable.outgoing_message_background_opaque;
            }

            backgroundContainer.setBackgroundResource(backgroundResource);

            int curGrav = message.isIncoming() ? Gravity.START : Gravity.END;
            messageContainer.setGravity(curGrav);

            MessageState messageState = message.getMessageState();

            if (messageState == MessageState.DELETED) {
                int textColor = ContextCompat.getColor(itemView.getContext(), R.color.white);
                messageTextView.setTextColor(textColor);
            } else if (messageState == MessageState.EDITED) {
                editInfoContainer.setVisibility(View.VISIBLE);
                editTimestampTextView.setText(dateFormat.format(message.getEditTimeStamp()));
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            int position = getAdapterPosition();
            Message msg = messageList.get(position);

            //Context Menus do not support icons :(
            MenuItem editItem = menu.add(0, R.id.menu_edit, 0, "Edit");
            editItem.setIcon(R.drawable.baseline_delete_24);

            MenuItem deleteItem = menu.add(0, R.id.menu_delete, 1, "Delete");
            deleteItem.setIcon(R.drawable.baseline_delete_24);

            if(msg.isIncoming() || msg.getMessageState() == MessageState.DELETED) {
                editItem.setEnabled(false);
                deleteItem.setEnabled(false);
            }
        }
    }
}
