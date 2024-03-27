package com.example.module5.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.module5.model.Message;
import com.example.module5.R;
import com.google.firebase.database.DatabaseReference;


import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;
    private DatabaseReference messagesRef; // Added for Firebase interaction
    private Context context; // Added for Firebase interaction

    private String userId;

    AdapterListener listener;

    public MessageAdapter(List<Message> messageList, DatabaseReference messagesRef, String userId, Context context) {
        this.context = context;
        this.messageList = messageList;
        this.messagesRef = messagesRef;
        this.userId = userId;
        this.listener = (AdapterListener) context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Message message = messageList.get(position);

        // Format and display the timestamp
        String formattedTime = DateFormat.format("dd-MM-yyyy (HH:mm:ss)", message.getTimestamp()).toString();
        holder.timestamp.setText(formattedTime);


        if (message.getType().equals("image") || message.getType().equals("gif")) {
            // Use Glide or similar library to load the image

                Glide.with(context)
                        .load(message.getMessage())
                        .into(holder.imageView);

            holder.imageView.setVisibility(View.VISIBLE);
            holder.messageText.setVisibility(View.GONE);
            holder.player.setVisibility(View.GONE);
        } else if (message.getType().equals("videos")) {
            // Handle video loading. You might want to show a thumbnail and play the video in a different activity or use a VideoView/ExoPlayer.
            holder.imageView.setVisibility(View.GONE);
            holder.player.setVisibility(View.VISIBLE);
            holder.messageText.setVisibility(View.GONE);

        }
        else {
            holder.messageText.setText(message.getMessage());
            holder.imageView.setVisibility(View.GONE);
            holder.player.setVisibility(View.GONE);
            holder.messageText.setVisibility(View.VISIBLE);
        }





        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.linear.getLayoutParams();
        if (message.getSenderId().equals(userId)) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        else {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        }
        holder.linear.setLayoutParams(params);

        holder.linear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                showOptionsDialog(holder.linear.getContext(), position);
                return true;
            }
        });
        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getType().equals("videos")) {
                    listener.viewVideo(message.getMessage());
                }
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


        private ImageView player;

        ImageView imageView;


        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestamp = itemView.findViewById(R.id.timestamp);
            linear = itemView.findViewById(R.id.mainLinear);
            imageView = itemView.findViewById(R.id.imageView);
            player = itemView.findViewById(R.id.video_view);

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

    public interface AdapterListener {
        void viewVideo(String url);
    }
}

