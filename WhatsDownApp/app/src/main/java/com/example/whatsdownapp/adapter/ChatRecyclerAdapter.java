package com.example.whatsdownapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsdownapp.ChatActivity;
import com.example.whatsdownapp.R;
import com.example.whatsdownapp.model.ChatMessageModel;
import com.example.whatsdownapp.utils.AndroidUtil;
import com.example.whatsdownapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        Log.i("haushd","asjd");
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(model.getMessage());
            holder.rightChatTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
        }else{
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(model.getMessage());
            holder.leftChatTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
        }
    }

//    @Override
//    protected void onBindViewHolder(@NonNull ChatRecyclerAdapter.ChatModelViewHolder holder, int position, @NonNull ChatMessage model) {
//        if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
//            holder.leftChatLayout.setVisibility(View.GONE);
//            holder.rightChatLayout.setVisibility(View.VISIBLE);
//            holder.rightChatTextView.setText( model.getMessage());
//            holder.rightChatTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
//        }else{
//            holder.leftChatLayout.setVisibility(View.VISIBLE);
//            holder.rightChatLayout.setVisibility(View.GONE);
//            holder.leftChatTextView.setText(model.getMessage());
//            holder.leftChatTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
//
//        }
//    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextview,rightChatTextview, leftChatTimestamp, rightChatTimestamp;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            leftChatTimestamp = itemView.findViewById(R.id.left_chat_timestamp);
            rightChatTimestamp = itemView.findViewById(R.id.right_chat_timestamp);
        }
    }
}