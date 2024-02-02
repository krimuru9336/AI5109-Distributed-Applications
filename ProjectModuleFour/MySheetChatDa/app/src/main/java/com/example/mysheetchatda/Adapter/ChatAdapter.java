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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysheetchatda.Models.Message;
import com.example.mysheetchatda.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
- Name: Adrianus Jonathan Engelbracht
- Matriculation number: 1151826
- Date: 02.02.2024
*/
public class ChatAdapter extends RecyclerView.Adapter{

    ArrayList<Message> message;
    Context context;
    String receiverId;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<Message> message, Context context, String receiverId) {
        this.message = message;
        this.context = context;
        this.receiverId = receiverId;
    }

    public ChatAdapter(ArrayList<Message> message, Context context){
        this.message = message;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_message, parent, false);
            return new SenderViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_message, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if(message.get(position).getUserId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }else{
            return RECEIVER_VIEW_TYPE;
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Message messageModel = message.get(position);

        holder.itemView.setOnLongClickListener(view -> {
            // Options to display in the dialog
            final CharSequence[] options = {"Delete", "Edit"};

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Choose an action");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        // delete message
                        showDeleteConfirmation(position, messageModel);
                    } else if (which == 1) {
                        // edit the message
                        showEditMessageDialog(position, messageModel);
                    }
                }
            });
            builder.show();
            return true;
        });

        // enables the correct setting of timestamps
        if(holder.getClass() == SenderViewHolder.class){
            ((SenderViewHolder) holder).senderMsg.setText(messageModel.getMessageText());

            Date date = new Date(messageModel.getTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String strDate = simpleDateFormat.format(date);
            ((SenderViewHolder) holder).senderTime.setText(strDate.toString());

        }else{
            ((ReceiverViewHolder) holder).receiverMsg.setText(messageModel.getMessageText());

            Date date = new Date(messageModel.getTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String strDate = simpleDateFormat.format(date);
            ((ReceiverViewHolder) holder).receiverTime.setText(strDate.toString());


        }
    }

    @Override
    public int getItemCount() {
        return message.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{

        TextView receiverMsg, receiverTime;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiverMessage);
            receiverTime = itemView.findViewById(R.id.receiverTimestamp);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView senderMsg, senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTimestamp);
        }
    }

    private void showDeleteConfirmation(int position, Message messageModel) {
        new AlertDialog.Builder(context)
                .setTitle("Delete")
                .setMessage("Do you want to delete this message?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
                        String receiverRoom = receiverId + FirebaseAuth.getInstance().getUid();


                        database.getReference().child("Chats")
                                .child(senderRoom)
                                .child(messageModel.getMessageId())
                                .removeValue()
                                .addOnSuccessListener(unused -> {
                                    Log.d("WhatsappTag", "Message deleted successfully");
                                    // Remove the message from your local list and notify the adapter
                                    //message.remove(position);
                                    //notifyItemRemoved(position);
                                })
                                .addOnFailureListener(e -> Log.d("WhatsappTag", "Error deleting message", e));

                        // Delete from Receiver's Room
                        database.getReference().child("Chats")
                                .child(receiverRoom)
                                .child(messageModel.getMessageId())
                                .removeValue()
                                .addOnSuccessListener(unused -> {
                                    Log.d("WhatsappTag", "Message deleted successfully (receiver)");
                                })
                                .addOnFailureListener(e -> Log.d("WhatsappTag", "Error deleting message (receiver)", e));

                    }
                })
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }


    private void showEditMessageDialog(int position, Message messageModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Inflate the edit text layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.edit_message_popup, null);

        EditText editText = view.findViewById(R.id.edit_message_text);
        editText.setText(messageModel.getMessageText());

        builder.setView(view)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String updatedText = editText.getText().toString();
                        updateMessageInFirebase(position, messageModel, updatedText);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void updateMessageInFirebase(int position, Message messageModel, String updatedText) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
        String receiverRoom = receiverId + FirebaseAuth.getInstance().getUid();


        database.getReference().child("Chats")
                .child(senderRoom)
                .child(messageModel.getMessageId())
                .child("messageText")
                .setValue(updatedText)
                .addOnSuccessListener(aVoid -> {
                    Log.d("WhatsappTag", "Message updated");
                    messageModel.setMessageText(updatedText); // Update local model
                    notifyItemChanged(position); // Refresh item
                })
                .addOnFailureListener(e -> Log.e("WhatsappTag", "Failed to update message.", e));

        database.getReference().child("Chats")
                .child(receiverRoom)
                .child(messageModel.getMessageId())
                .child("messageText")
                .setValue(updatedText)
                .addOnSuccessListener(aVoid -> {
                    Log.d("WhatsappTag", "Message updated (receiver)");
                    //messageModel.setMessageText(updatedText);
                    //notifyItemChanged(position);
                })
                .addOnFailureListener(e -> Log.e("WhatsappTag", "Failed to update message (receiver)", e));

    }





}
