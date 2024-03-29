package com.example.mysheetchatda.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysheetchatda.Models.ChatMessageModel;
import com.example.mysheetchatda.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
- Name: Adrianus Jonathan Engelbracht
- Matriculation number: 1151826
- Date: 29.03.2024
*/
public class GroupChatAdapter extends RecyclerView.Adapter {

    private ArrayList<ChatMessageModel> chatMessageModel;
    private Context context;

    String receiverId;

    final int SENDER_TEXT_VIEW_TYPE = 1;
    final int RECEIVER_TEXT_VIEW_TYPE = 2;


    public GroupChatAdapter(ArrayList<ChatMessageModel> chatMessageModel, Context context, String receiverId) {
        this.chatMessageModel = chatMessageModel;
        this.context = context;
        this.receiverId = receiverId;
    }

    public GroupChatAdapter(ArrayList<ChatMessageModel> messages, Context context) {
        this.chatMessageModel = messages;
        this.context = context;
    }

    // distinguish between different view types returns the appropriate viewholder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_TEXT_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.group_sender_message, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.group_receiver_message, parent, false);
            return new ReceiverViewHolder(view);
        }
    }


    // determines if a chat message is a sending or receiving message in the group chat.
    @Override
    public int getItemViewType(int position) {

        if (chatMessageModel.get(position).getUserId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_TEXT_VIEW_TYPE;
        } else {
            return RECEIVER_TEXT_VIEW_TYPE;
        }

    }

    // handling onBind of  different  view holders (messages, images, videos)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ChatMessageModel chatMsgModel = chatMessageModel.get(position);
        // gets the correct time for time stamps
        Date date = new Date(chatMsgModel.getTimestamp());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String strDate = simpleDateFormat.format(date);

        switch (holder.getItemViewType()) {
            case SENDER_TEXT_VIEW_TYPE:
                SenderViewHolder senderHolder = (SenderViewHolder) holder;
                senderHolder.senderMsg.setText(chatMsgModel.getMessageText());
                senderHolder.senderTime.setText(strDate.toString());
                senderHolder.groupSenderName.setText(chatMsgModel.getSenderName());
                break;
            case RECEIVER_TEXT_VIEW_TYPE:
                ReceiverViewHolder receiverHolder = (ReceiverViewHolder) holder;
                receiverHolder.receiverMsg.setText(chatMsgModel.getMessageText());
                receiverHolder.receiverTime.setText(strDate.toString());
                receiverHolder.groupReceiverName.setText(chatMsgModel.getSenderName());
                break;
        }


        holder.itemView.setOnLongClickListener(view -> {
            // options to display in the dialog
            final CharSequence[] options = {"Delete", "Edit"};

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Choose an action");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        // delete message
                        showDeleteConfirmation(position, chatMsgModel);
                    } else if (which == 1) {
                        // edit the message
                        showEditMessageDialog(position, chatMsgModel);
                    }
                }
            });
            builder.show();
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return chatMessageModel.size();
    }

    // class for the sender view holder
    public class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView senderMsg, senderTime, groupSenderName;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderTextMessage);
            senderTime = itemView.findViewById(R.id.senderTimestamp);
            groupSenderName = itemView.findViewById(R.id.groupSenderName);
        }
    }


    // class for the receiverViewHolder
    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView receiverMsg, receiverTime, groupReceiverName;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiverTextMessage);
            receiverTime = itemView.findViewById(R.id.receiverTimestamp);
            groupReceiverName = itemView.findViewById(R.id.groupReceiverName);
        }
    }

    // delete dialog. which handles the deletion of the message in firebase
    private void showDeleteConfirmation(int position, ChatMessageModel chatMessageModelModel) {
        new AlertDialog.Builder(context)
                .setTitle("Delete")
                .setMessage("Do you want to delete this message?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
                        String receiverRoom = receiverId + FirebaseAuth.getInstance().getUid();

                        Log.d("GroupChatAdapter", "Group Chat Message deleted successfully");
                        Log.d("GroupChatAdapter", "Message ID:" + chatMessageModelModel.getMessageId());
                        Log.d("GroupChatAdapter", "Message Text:" + chatMessageModelModel.getMessageText());
                        Log.d("GroupChatAdapter", "User ID:" + chatMessageModelModel.getUserId());
                        Log.d("GroupChatAdapter", "Timestamp ID:" + chatMessageModelModel.getTimestamp());
                        Log.d("GroupChatAdapter", "Sendername ID:" + chatMessageModelModel.getSenderName());
                        // message only needs to be removed at one place in firebase
                        database.getReference().child("GroupChat")
                                .child(chatMessageModelModel.getMessageId())
                                .removeValue()
                                .addOnSuccessListener(unused -> {
                                    Log.d("GroupChatAdapter", "Group Chat Message deleted successfully");
                                    //message.remove(position);
                                    //notifyItemRemoved(position);
                                })
                                .addOnFailureListener(e -> Log.d("WhatsappTag", "Error deleting message", e));
                    }
                })
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }


    // edit message dialog, which calls updateMessageInFirebase
    private void showEditMessageDialog(int position, ChatMessageModel chatMessageModelModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.edit_message_popup, null);

        EditText editText = view.findViewById(R.id.edit_message_text);
        editText.setText(chatMessageModelModel.getMessageText());

        builder.setView(view)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String updatedText = editText.getText().toString();
                        updateMessageInFirebase(position, chatMessageModelModel, updatedText);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // edits a message in firebase
    private void updateMessageInFirebase(int position, ChatMessageModel chatMessageModelModel, String updatedText) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // message only needs to be edited at one place in firebase
        database.getReference().child("GroupChat")
                .child(chatMessageModelModel.getMessageId())
                .child("messageText")
                .setValue(updatedText)
                .addOnSuccessListener(aVoid -> {
                    Log.d("GroupChatAdapter", "GroupChat Message updated");
                    chatMessageModelModel.setMessageText(updatedText); // Update local model
                    notifyItemChanged(position); // Refresh item
                })
                .addOnFailureListener(e -> Log.e("GroupChatAdapter", "Failed to update groupchat message.", e));
    }

}
