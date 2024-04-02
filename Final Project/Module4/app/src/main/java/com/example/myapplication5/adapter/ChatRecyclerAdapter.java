package com.example.myapplication5.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication5.R;
import com.example.myapplication5.ChatMessageModel;
import com.example.myapplication5.utils.FirebaseUtil;

import java.util.List;



public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatModelViewHolder>  {
    Context context;
    private List<ChatMessageModel> chatMessageList;
    public String chatroomId;

    public ChatRecyclerAdapter(List<ChatMessageModel> chatMessageList, Context context, String chatroomId) {
        this.chatMessageList = chatMessageList;
        this.context = context;
        this.chatroomId =  chatroomId;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position) {
        ChatMessageModel model = chatMessageList.get(position);
        System.out.println("in chat adapter" + model.getSenderId());
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){

            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(model.getMessage());
            holder.rightTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
//edit del
            holder.rightChatLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Show options for sender's messages (Edit, Delete)
                    showOptionsDialog(model);
                    return true;
                }
            });

            //edit del

        }else{
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(model.getMessage());
            holder.leftTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
        }



    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    public void addData(ChatMessageModel newMessage) {
        chatMessageList.add(newMessage);
        notifyItemInserted(chatMessageList.size() - 1); // Notify the adapter about the new item
    }

    public void clearData() {
        if (chatMessageList != null) {
            chatMessageList.clear();
            notifyDataSetChanged();
        }
    }

    //edit del
    public void showOptionsDialog(ChatMessageModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Message Options");

        // Edit option
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showEditDialog(model);
            }
        });

        // Delete option
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDeleteDialog(model);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showEditDialog(ChatMessageModel model) {
        // Implement a dialog for editing messages
        // You can use an AlertDialog or a custom dialog for this
        // On editing, update the message in the list and notify the adapter
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Message");

        // Create an EditText to allow the user to edit the message
        final EditText input = new EditText(context);
        input.setText(model.getMessage());
        builder.setView(input);

        // Set up the buttons for positive (update) and negative (cancel) actions
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the updated message from the EditText
                String updatedMessage = input.getText().toString().trim();

                // Update the message in the list
                model.setMessage(updatedMessage);
                for (ChatMessageModel chatMessage : chatMessageList) {
                    if (chatMessage.getTimestamp().equals(model.getTimestamp())) {
                        chatMessage.setMessage(updatedMessage);
                        // Update the message in the Firebase database using your logic
                        FirebaseUtil.updateChatMessage(chatroomId, chatMessage);
                        break;
                    }
                }

                // Notify the adapter about the change
                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDeleteDialog(ChatMessageModel model) {
        // Implement a dialog for deleting messages
        // You can use an AlertDialog or a custom dialog for this
        // On deletion, remove the message from the list and notify the adapter
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Message");
        builder.setMessage("Are you sure you want to delete this message?");

        // Set up the buttons for positive (delete) and negative (cancel) actions
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove the message from the list

                for (ChatMessageModel chatMessage : chatMessageList) {
                    if (chatMessage.getTimestamp().equals(model.getTimestamp())) {
                        chatMessageList.remove(model);
//                        chatMessage.setMessage(updatedMessage);
                        // Update the message in the Firebase database using your logic
                        System.out.println("delete method call");
                        FirebaseUtil.deleteChatMessage(chatroomId, chatMessage);
                        break;
                    }
                }

                // Notify the adapter about the removal
                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //edit del

    class ChatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextview,rightChatTextview, leftTimestamp, rightTimestamp;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            leftTimestamp = itemView.findViewById(R.id.left_chat_timestamp);
            rightTimestamp = itemView.findViewById(R.id.right_chat_timestamp);
        }
    }
}


