package com.example.whatsdownapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsdownapp.GroupActivity;
import com.example.whatsdownapp.R;
import com.example.whatsdownapp.model.ChatGroupModel;
import com.example.whatsdownapp.model.ChatMessageModel;
import com.example.whatsdownapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatGroupsRecyclerAdapter extends FirestoreRecyclerAdapter<ChatGroupModel, RecentChatGroupsRecyclerAdapter.ChatGroupModelViewHolder> {

    Context context;

    public RecentChatGroupsRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatGroupModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatGroupModelViewHolder holder, int position, @NonNull ChatGroupModel model) {

        holder.groupNameText.setText(model.getGroupName());

        // Check if last message is available
        if (model.getLastMsg() != null) {
            boolean lastMessageSentByMe = model.getLastMsgSenderId().equals(FirebaseUtil.currentUserId());
            boolean isDeletedLastMsg = model.getLastMsg().isDeleted();
            ChatMessageModel.MessageType type = model.getLastMsg().getMessageType();
            String lastMessageText = model.getLastMsg().getMessage();

            if (isDeletedLastMsg) {
                lastMessageText = "This message was deleted.";
            }

            if (type != ChatMessageModel.MessageType.TEXT) {
                lastMessageText = "📷 Attachment";
            }

            if (lastMessageSentByMe) {
                lastMessageText = isDeletedLastMsg ? "You deleted this message." : String.format("You: %s", lastMessageText);
            }

            holder.lastMsgText.setText(lastMessageText);
            holder.lastMsgTime.setText(FirebaseUtil.timestampToString(model.getLastMsg().getTimestamp()));
        } else {
            // If last message is not available, set blank strings
            holder.lastMsgText.setText("");
            holder.lastMsgTime.setText("");
        }

        holder.itemView.setOnClickListener(v -> {
            //navigate to chat activity
            Intent intent = new Intent(context, GroupActivity.class);
            intent.putExtra("groupName", model.getGroupName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

    }


    @NonNull
    @Override
    public ChatGroupModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_chat_recycler_view, parent, false);
        return new ChatGroupModelViewHolder(view);
    }

    class ChatGroupModelViewHolder extends RecyclerView.ViewHolder {
        TextView groupNameText;
        TextView lastMsgText, lastMsgTime;


        public ChatGroupModelViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameText = itemView.findViewById(R.id.recent_group_chat_text);
            lastMsgText = itemView.findViewById(R.id.last_group_msg_text);
            lastMsgTime = itemView.findViewById(R.id.last_msg_time_text);

        }


    }
}