package com.example.chatapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;
    private Context context;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("messages");

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.itemView.setOnLongClickListener(v -> {
            showEditDeleteDialog(message, holder.itemView.getContext());
            return true;
        });

        // Determine if the message is sent or received to show or hide the sender's name
        if(message.isSent()) {
            holder.textViewSender.setVisibility(View.GONE);
        } else {
            holder.textViewSender.setVisibility(View.VISIBLE);
            holder.textViewSender.setText(message.getSender()); // Set the sender's name
        }

        holder.textViewMessage.setText(message.getText());

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        holder.textViewTimestamp.setText(dateFormat.format(new Date(message.getTimestamp())));

        ConstraintLayout.LayoutParams messageLayoutParams = (ConstraintLayout.LayoutParams) holder.textViewMessage.getLayoutParams();

        // Adjust constraints for sent and received messages
        if (message.isSent()) {
            holder.textViewMessage.setBackgroundResource(R.drawable.message_background_sent);
            messageLayoutParams.horizontalBias = 1.0f; // Align to the right for sent messages
        } else {
            holder.textViewMessage.setBackgroundResource(R.drawable.message_background_received);
            messageLayoutParams.horizontalBias = 0.0f; // Align to the left for received messages
        }
        holder.textViewMessage.setLayoutParams(messageLayoutParams);
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage, textViewTimestamp, textViewSender;

        public MessageViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
            textViewSender = itemView.findViewById(R.id.textViewSender); // Ensure this ID exists in your layout
        }
    }
    private void showEditDeleteDialog(Message message, Context context) {
        final CharSequence[] items = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Message options");
        builder.setItems(items, (dialog, which) -> {
            if ("Edit".equals(items[which])) {
                editMessage(message, context);
            } else if ("Delete".equals(items[which])) {
                deleteMessage(message);
            }
        });
        builder.show();
    }

    private void editMessage(Message message, Context context) {
        // Example dialog with an EditText to update the message text
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Message");

        final EditText input = new EditText(context);
        input.setText(message.getText());
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newText = input.getText().toString();
            updateMessageInFirebase(message.getId(), newText);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteMessage(Message message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("messages");
        databaseReference.child(message.getId()).removeValue();
    }

    private void updateMessageInFirebase(String messageId, String newText) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("messages").child(messageId);
        databaseReference.child("text").setValue(newText);
    }

}
