package com.example.module3;

import android.app.AlertDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;
    private DatabaseReference messagesRef; // Added for Firebase interaction

    public MessageAdapter(List<Message> messageList, DatabaseReference messagesRef) {
        this.messageList = messageList;
        this.messagesRef = messagesRef;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageText.setText(message.getMessage());
        // Format and display the timestamp
        String formattedTime = DateFormat.format("dd-MM-yyyy (HH:mm:ss)", message.getTimestamp()).toString();
        holder.timestamp.setText(formattedTime);

        holder.linear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showOptionsDialog(holder.linear.getContext(), position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestamp;
        LinearLayout linear;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestamp = itemView.findViewById(R.id.timestamp);
            linear = itemView.findViewById(R.id.linear);

        }




    }


    private void showOptionsDialog(Context context, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose an option")
                .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showEditDialog(context, position);
                            break;
                        case 1:
                            deleteMessage(context, position);
                            break;
                    }
                })
                .create()
                .show();
    }

    private void showEditDialog(Context context, int position) {
        // Implement a dialog to allow the user to edit the message
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Message");

        // Set up the input
        final EditText input = new EditText(context);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newMessage = input.getText().toString();
           // ((MainActivity) context).editMessage(position, newMessage);
            editMessage(position, newMessage);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteMessage(Context context, int position) {
        //((MainActivity) context).deleteMessage(position);
        deleteMessage(position);
    }


    public void editMessage(int position, String newMessage) {
        Message message = messageList.get(position);
        message.setMessage(newMessage);
        notifyItemChanged(position);
        updateMessageInFirebase(message);
    }

    public void deleteMessage(int position) {
        Message message = messageList.get(position);
        messageList.remove(position);
        notifyItemRemoved(position);
        deleteMessageFromFirebase(message);
    }

    private void updateMessageInFirebase(Message message) {
        String messageId = message.getMessageId(); // Assuming you have a messageId field in your Message class
        messagesRef.child(messageId).setValue(message);
    }

    private void deleteMessageFromFirebase(Message message) {
        String messageId = message.getMessageId();
        messagesRef.child(messageId).removeValue();
    }
}

