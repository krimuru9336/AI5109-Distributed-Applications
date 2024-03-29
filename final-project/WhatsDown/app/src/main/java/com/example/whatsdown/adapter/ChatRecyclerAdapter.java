package com.example.whatsdown.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsdown.R;
import com.example.whatsdown.model.ChatMessageModel;
import com.example.whatsdown.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


import java.text.SimpleDateFormat;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter <ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder>{

    Context context;
    private OnChatItemClickListener listener;
    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options,Context context) {
        super(options);
        this.context = context;
    }

    public interface OnChatItemClickListener {
        void onLongPress(int position, ChatMessageModel chatMessage);
    }

    public void setOnChatItemClickListener(OnChatItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRecyclerAdapter.ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        boolean isDeleted = model.isDeleted();
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.leftChatTimeLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTimeLayout.setVisibility(View.VISIBLE);
            switch (model.getMessageType()) {
                case IMAGE:
                    holder.rightChatImageView.setVisibility(View.VISIBLE);
                    holder.rightChatTextview.setVisibility(View.GONE);
                    Glide.with(holder.itemView.getContext()).load(model.getMessage()).into(holder.rightChatImageView);
                    break;

                case VIDEO:
                    holder.rightChatImageView.setVisibility(View.GONE);
                    holder.rightChatTextview.setVisibility(View.GONE);
                    holder.rightVideoView.setVisibility(View.VISIBLE);
                    String videoUrl = model.getMessage();
                    holder.rightVideoView.setVideoURI(Uri.parse(videoUrl));
                    holder.rightVideoView.start();
                    break;

                case GIF:
                    holder.rightChatImageView.setVisibility(View.VISIBLE);
                    holder.rightChatTextview.setVisibility(View.GONE);
                    Glide.with(holder.itemView.getContext()).asGif().load(model.getMessage()).into(holder.rightChatImageView);
                    break;

                default:
                    holder.rightChatImageView.setVisibility(View.GONE);
                    holder.rightChatTextview.setVisibility(View.VISIBLE);
                    String messageText = isDeleted ? "You deleted this message." : model.getMessage();
                    holder.rightChatTextview.setText(messageText);
                    break;
            }
            holder.rightChatTimeTextview.setText(new SimpleDateFormat("HH:mm").format(model.getTimestamp().toDate()));
        }else{
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.rightChatTimeLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTimeLayout.setVisibility(View.VISIBLE);
            switch (model.getMessageType()) {
                case IMAGE:
                    holder.leftChatImageView.setVisibility(View.VISIBLE);
                    holder.leftChatTextview.setVisibility(View.GONE);
                    Glide.with(holder.itemView.getContext()).load(model.getMessage()).into(holder.leftChatImageView);
                    break;

                case VIDEO:
                    holder.leftChatImageView.setVisibility(View.GONE);
                    holder.leftChatTextview.setVisibility(View.GONE);
                    holder.leftVideoView.setVisibility(View.VISIBLE);
                    String videoUrl = model.getMessage();
                    holder.leftVideoView.setVideoURI(Uri.parse(videoUrl));
                    holder.leftVideoView.start();
                    break;

                case GIF:
                    holder.leftChatImageView.setVisibility(View.VISIBLE);
                    holder.leftChatTextview.setVisibility(View.GONE);
                    Glide.with(holder.itemView.getContext()).asGif().load(model.getMessage()).into(holder.leftChatImageView);
                    break;

                default:
                    holder.leftChatImageView.setVisibility(View.GONE);
                    holder.leftChatTextview.setVisibility(View.VISIBLE);
                    String messageText = isDeleted ? "This message was deleted." : model.getMessage();
                    holder.leftChatTextview.setText(messageText);
                    break;
            }
            holder.leftChatTimeTextview.setText(new SimpleDateFormat("HH:mm").format(model.getTimestamp().toDate()));
        }
    }

    @NonNull
    @Override
    public ChatRecyclerAdapter.ChatModelViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row,parent,false);
        return new ChatRecyclerAdapter.ChatModelViewHolder (view);
    }
    class ChatModelViewHolder  extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout,rightChatLayout,leftChatTimeLayout,rightChatTimeLayout;
        TextView leftChatTextview,rightChatTextview,leftChatTimeTextview,rightChatTimeTextview;
        ImageView leftChatImageView, rightChatImageView;

        VideoView leftVideoView, rightVideoView;
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
            leftChatImageView = itemView.findViewById(R.id.left_chat_image_view);
            rightChatImageView = itemView.findViewById(R.id.right_chat_image_view);
            leftVideoView = itemView.findViewById(R.id.left_chat_video_view);
            rightVideoView = itemView.findViewById(R.id.right_chat_video_view);
            rightChatLayout.setOnLongClickListener(v->{
                if (listener != null) {
                    listener.onLongPress(getAdapterPosition(), getItem(getAbsoluteAdapterPosition()));
                    return true;
                }
                return false;
            });
        }
    }

}
