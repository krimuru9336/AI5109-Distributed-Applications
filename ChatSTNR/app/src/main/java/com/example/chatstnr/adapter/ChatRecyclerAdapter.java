package com.example.chatstnr.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatstnr.ChatActivity;
import com.example.chatstnr.R;
import com.example.chatstnr.models.ChatMessageModel;
import com.example.chatstnr.utils.AndroidUtil;
import com.example.chatstnr.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;
    private OnItemClickListener onItemClickListener;
    private int selectedItemPosition = RecyclerView.NO_POSITION;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    // Interface for item click handling
    public interface OnItemClickListener {
        void onItemClick(ChatMessageModel chatMessage);
    }
    // Setter method for setting the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        Log.i("haushd","asjd");
        if(model.getSenderId().equals(FirebaseUtil.currentUserid())){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(model.getMessage());
            holder.rightChatTimeview.setText(FirebaseUtil.timestampToString(model.getTimestamp()));

        }else{
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(model.getMessage());
            holder.leftChatTimeview.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
        }

    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row,parent,false);
        ChatModelViewHolder viewHolder = new ChatModelViewHolder(view);

        // Set the click listener for each ViewHolder
        viewHolder.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ChatMessageModel chatMessage) {
                // Handle item click
                if (onItemClickListener != null) {
//                    viewHolder.rightChatLayout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.chat_color_editing)));
                    onItemClickListener.onItemClick(chatMessage);
                }
            }
        });

        return viewHolder;

//        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextview,rightChatTextview, leftChatTimeview, rightChatTimeview;

        ImageButton deleteMessageBtn;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            leftChatTimeview = itemView.findViewById(R.id.left_chat_timeview);
            rightChatTimeview = itemView.findViewById(R.id.right_chat_timeview);

        }

        // Method to set the click listener for each ViewHolder
        public void setOnItemClickListener(OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }
}
