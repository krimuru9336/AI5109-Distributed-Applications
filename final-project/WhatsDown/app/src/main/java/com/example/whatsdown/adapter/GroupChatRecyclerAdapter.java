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

public class GroupChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, GroupChatRecyclerAdapter.ChatModelViewHolder> {
    Context context;

    private OnChatItemClickListener listener;

    public interface OnChatItemClickListener {

        void onLongPress(int position, ChatMessageModel chatMessage);
    }

    public void setOnChatItemClickListener(OnChatItemClickListener listener) {
        this.listener = listener;
    }

    public GroupChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupChatRecyclerAdapter.ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        boolean isCurrentUserId = model.getSenderId().equals(FirebaseUtil.currentUserId());
        boolean isDeletedMessage = model.isDeleted();

        if (isCurrentUserId) {
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);

            switch (model.getMessageType()) {
                case IMAGE:
                    holder.rightChatImageView.setVisibility(View.VISIBLE);
                    holder.rightChatTextView.setVisibility(View.GONE);
                    holder.rightVideoView.setVisibility(View.GONE);
                    Glide.with(holder.itemView.getContext()).load(model.getMessage()).into(holder.rightChatImageView);
                    break;

                case VIDEO:
                    holder.rightChatImageView.setVisibility(View.GONE);
                    holder.rightChatTextView.setVisibility(View.GONE);
                    holder.rightVideoView.setVisibility(View.VISIBLE);
                    String videoUrl = model.getMessage();
                    holder.rightVideoView.setVideoURI(Uri.parse(videoUrl));
                    holder.rightVideoView.start();
                    break;

                case GIF:
                    holder.rightChatImageView.setVisibility(View.VISIBLE);
                    holder.rightChatTextView.setVisibility(View.GONE);
                    holder.rightVideoView.setVisibility(View.GONE);
                    Glide.with(holder.itemView.getContext()).asGif().load(model.getMessage()).into(holder.rightChatImageView);
                    break;

                default:
                    holder.rightChatImageView.setVisibility(View.GONE);
                    holder.rightVideoView.setVisibility(View.GONE);
                    holder.rightChatTextView.setVisibility(View.VISIBLE);
                    String messageText = isDeletedMessage ? "You deleted this message." : model.getMessage();
                    holder.rightChatTextView.setText(messageText);
                    break;
            }

            holder.rightChatTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
        } else {
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatLayout.setVisibility(View.GONE);

            switch (model.getMessageType()) {
                case IMAGE:
                    holder.leftChatImageView.setVisibility(View.VISIBLE);
                    holder.leftChatTextView.setVisibility(View.GONE);
                    holder.leftVideoView.setVisibility(View.GONE);
                    Glide.with(holder.itemView.getContext()).load(model.getMessage()).into(holder.leftChatImageView);
                    break;

                case VIDEO:
                    holder.leftChatImageView.setVisibility(View.GONE);
                    holder.leftChatTextView.setVisibility(View.GONE);
                    holder.leftVideoView.setVisibility(View.VISIBLE);
                    String videoUrl = model.getMessage();
                    holder.leftVideoView.setVideoURI(Uri.parse(videoUrl));
                    holder.leftVideoView.start();
                    break;

                case GIF:
                    holder.leftChatImageView.setVisibility(View.VISIBLE);
                    holder.leftChatTextView.setVisibility(View.GONE);
                    holder.leftVideoView.setVisibility(View.GONE);
                    Glide.with(holder.itemView.getContext()).asGif().load(model.getMessage()).into(holder.leftChatImageView);
                    break;

                default:
                    holder.leftChatImageView.setVisibility(View.GONE);
                    holder.leftChatTextView.setVisibility(View.VISIBLE);
                    holder.leftVideoView.setVisibility(View.GONE);
                    String messageText = isDeletedMessage ? "This message was deleted." : model.getMessage();
                    holder.leftChatTextView.setText(messageText);
                    break;
            }

            holder.leftChatTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
            holder.leftChatUserNameView.setText(model.getSenderName());

        }
    }


    @NonNull
    @Override
    public GroupChatRecyclerAdapter.ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_chat_message_recycler_view, parent, false);
        return new GroupChatRecyclerAdapter.ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextView, rightChatTextView, leftChatTimestamp, rightChatTimestamp;
        TextView leftChatUserNameView;
        ImageView leftChatImageView, rightChatImageView;

        VideoView  leftVideoView, rightVideoView;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextView = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextView = itemView.findViewById(R.id.right_chat_textview);
            rightChatTimestamp = itemView.findViewById(R.id.right_chat_timestamp);
            leftChatTimestamp = itemView.findViewById(R.id.left_chat_timestamp);
            leftChatImageView = itemView.findViewById(R.id.left_chat_image_view);
            rightChatImageView = itemView.findViewById(R.id.right_chat_image_view);
            leftVideoView = itemView.findViewById(R.id.left_chat_video_view);
            rightVideoView = itemView.findViewById(R.id.right_chat_video_view);
            leftChatUserNameView = itemView.findViewById(R.id.left_chat_username_view);

            rightChatLayout.setOnLongClickListener(v->{
                Log.d("Click", "Long Clicked message");
                if (listener != null) {
                    listener.onLongPress(getAdapterPosition(), getItem(getAbsoluteAdapterPosition()));
                    return true;
                }
                return false;

            });

        }

    }
}