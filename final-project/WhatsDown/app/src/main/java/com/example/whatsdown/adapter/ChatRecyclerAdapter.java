package com.example.whatsdown.adapter;

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

import com.example.whatsdown.ChatActivity;
import com.example.whatsdown.R;
import com.example.whatsdown.model.ChatMessageModel;
import com.example.whatsdown.utils.AndroidUtil;
import com.example.whatsdown.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter <ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder>{

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
            holder.leftChatTimeLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTimeLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(model.getMessage());
            holder.rightChatTimeTextview.setText(new SimpleDateFormat("HH:mm").format(model.getTimestamp().toDate()));
        }else{
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.rightChatTimeLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTimeLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(model.getMessage());
            holder.leftChatTimeTextview.setText(new SimpleDateFormat("HH:mm").format(model.getTimestamp().toDate()));
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row,parent,false);
        return new ChatModelViewHolder (view);
    }
    class ChatModelViewHolder  extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout,rightChatLayout,leftChatTimeLayout,rightChatTimeLayout;
        TextView leftChatTextview,rightChatTextview,leftChatTimeTextview,rightChatTimeTextview;
        public ChatModelViewHolder (@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            leftChatTimeLayout = itemView.findViewById(R.id.left_chat_time_layout);
            rightChatTimeLayout = itemView.findViewById(R.id.right_chat_time_layout);
            leftChatTimeTextview = itemView.findViewById(R.id.left_chat_time_textview);
            rightChatTimeTextview = itemView.findViewById(R.id.right_chat_time_textview);
        }
    }

}
