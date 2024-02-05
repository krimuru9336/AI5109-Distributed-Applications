package com.example.cchat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cchat.R;
import com.example.cchat.model.ChatMessageModel;
import com.example.cchat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;
    private OnChatMessageClickListener clickListener;
    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context, OnChatMessageClickListener clickListener) {
        super(options);
        this.context = context;
        this.clickListener = clickListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            holder.receivedMsgLayout.setVisibility(View.GONE);
            holder.sentMsgLayout.setVisibility(View.VISIBLE);
            holder.sentMsgView.setText(model.getMessage());
            holder.sentMsgTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));

            holder.sentMsgLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //alert here
                    if (clickListener != null) {
                        String messageId = getSnapshots().getSnapshot(position).getId();
                        clickListener.onChatMessageClicked(model, messageId);
                    }
                    return true;
                }
            });

        } else {
            holder.receivedMsgLayout.setVisibility(View.VISIBLE);
            holder.sentMsgLayout.setVisibility(View.GONE);
            holder.receivedMsgView.setText(model.getMessage());
            holder.receivedMsgTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row,parent, false);
        return new ChatModelViewHolder(view);
    }

    public interface OnChatMessageClickListener {
        void onChatMessageClicked(ChatMessageModel chatMessage, String messageId);
    }
    class ChatModelViewHolder extends RecyclerView.ViewHolder {

        LinearLayout receivedMsgLayout;
        LinearLayout sentMsgLayout;
        TextView receivedMsgView;
        TextView receivedMsgTimestamp;

        TextView sentMsgView;
        TextView sentMsgTimestamp;


        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            receivedMsgLayout = itemView.findViewById(R.id.received_msg_layout);
            sentMsgLayout = itemView.findViewById(R.id.sent_msg_layout);
            receivedMsgView = itemView.findViewById(R.id.received_msg_textview);
            receivedMsgTimestamp = itemView.findViewById(R.id.received_msg_timestamp);
            receivedMsgView = itemView.findViewById(R.id.received_msg_textview);
            sentMsgView = itemView.findViewById(R.id.sent_msg_textview);
            sentMsgTimestamp = itemView.findViewById(R.id.sent_msg_timestamp);
        }
    }
}
