package com.example.letschat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.R;
import com.example.letschat.model.ChatMessage;
import com.example.letschat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessage, ChatRecyclerAdapter.ChatModelViewHolder> {
    Context context;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRecyclerAdapter.ChatModelViewHolder holder, int position, @NonNull ChatMessage model) {
        if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextView.setText( model.getMessage());
            holder.rightChatTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
        }else{
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatTextView.setText(model.getMessage());
            holder.leftChatTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));

        }
    }

    @NonNull
    @Override
    public ChatRecyclerAdapter.ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_view, parent, false);
        return new ChatRecyclerAdapter.ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextView, rightChatTextView, leftChatTimestamp, rightChatTimestamp;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextView = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextView = itemView.findViewById(R.id.right_chat_textview);
            rightChatTimestamp = itemView.findViewById(R.id.right_chat_timestamp);
            leftChatTimestamp = itemView.findViewById(R.id.left_chat_timestamp);

        }


    }
}
