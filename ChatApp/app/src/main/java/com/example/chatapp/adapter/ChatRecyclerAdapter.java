package com.example.chatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.ChatActivity;
import com.example.chatapp.R;
import com.example.chatapp.model.ChatMessageModel;
import com.example.chatapp.utils.AndroidUtil;
import com.example.chatapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.bumptech.glide.Glide;
import org.w3c.dom.Text;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;
    public interface OnChatItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
    public ChatMessageModel getMessageAtPosition(int position) {
        return getItem(position);
    }

    private OnChatItemLongClickListener longClickListener;

    public void setOnChatItemLongClickListener(OnChatItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        holder.leftChatImageView.setVisibility(View.GONE);
        holder.rightChatImageView.setVisibility(View.GONE);
        holder.leftChatVideoView.setVisibility(View.GONE); // Hide VideoViews initially
        holder.rightChatVideoView.setVisibility(View.GONE);

        if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            // Message sent by the current user
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(model.getMessage());
            holder.rightChatTimestamp.setText(FirebaseUtil.timeStampToString(model.getTimestamp()));
            holder.leftChatTimestamp.setVisibility(View.GONE);
            holder.rightChatTimestamp.setVisibility(View.VISIBLE);

            // Check if the message contains an image URL
            if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
                // If image URL exists, load and display the image using Glide in the rightChatImageView
                Glide.with(context)
                        .load(model.getImageUrl())
                        .into(holder.rightChatImageView);
                holder.rightChatImageView.setVisibility(View.VISIBLE);
            }
            // Check if the message contains a video URL
            if (model.getVideoUrl() != null && !model.getVideoUrl().isEmpty()) {
                Log.d("VideoURL", "Video URL: " + model.getVideoUrl());
                // If video URL exists, set the video URL to the VideoView and make it visible
                try {
                holder.rightChatVideoView.setVideoURI(Uri.parse(model.getVideoUrl()));
                holder.rightChatVideoView.setVisibility(View.VISIBLE);
                // Pause the video initially
                holder.rightChatVideoView.pause();
                // Show play button
                holder.rightPlayButton.setVisibility(View.VISIBLE);
                // Set an OnClickListener for the play button
                holder.rightPlayButton.setOnClickListener(v -> {
                    // Start playing the video when the play button is clicked
                    holder.rightChatVideoView.start();
                    // Hide the play button
                    holder.rightPlayButton.setVisibility(View.GONE);
                });
                } catch (Exception e) {
                    // Log any exceptions that occur during video playback or URI setting
                    Log.e("VideoPlaybackError", "Error setting video URI or playing video: " + e.getMessage());
                    // You can also display a toast or handle the error in another way
                    // showToast("Error playing video.");
                }
            }

        } else {
            // Message sent by the other user
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(model.getMessage());
            holder.leftChatTimestamp.setText(FirebaseUtil.timeStampToString(model.getTimestamp()));
            holder.rightChatTimestamp.setVisibility(View.GONE);
            holder.leftChatTimestamp.setVisibility(View.VISIBLE);

            // Check if the message contains an image URL
            if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
                // If image URL exists, load and display the image using Glide in the leftChatImageView
                Glide.with(context)
                        .load(model.getImageUrl())
                        .into(holder.leftChatImageView);
                holder.leftChatImageView.setVisibility(View.VISIBLE);
            }
            // Check if the message contains a video URL
            if (model.getVideoUrl() != null && !model.getVideoUrl().isEmpty()) {
                // Log the video URL
                Log.d("VideoURL", "Video URL: " + model.getVideoUrl());
                // If video URL exists, set the video URL to the VideoView and make it visible
                try{
                holder.leftChatVideoView.setVideoURI(Uri.parse(model.getVideoUrl()));
                holder.leftChatVideoView.setVisibility(View.VISIBLE);
                // Pause the video initially
                holder.leftChatVideoView.pause();
                // Show play button
                holder.leftPlayButton.setVisibility(View.VISIBLE);
                // Set an OnClickListener for the play button
                holder.leftPlayButton.setOnClickListener(v -> {
                    // Start playing the video when the play button is clicked
                    holder.leftChatVideoView.start();
                    // Hide the play button
                    holder.leftPlayButton.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                // Log any exceptions that occur during video playback or URI setting
                Log.e("VideoPlaybackError", "Error setting video URI or playing video: " + e.getMessage());
                // You can also display a toast or handle the error in another way
                // showToast("Error playing video.");
            }
            }
        }
    }



    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row,parent, false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{
        ImageView leftChatImageView, rightChatImageView;
        ImageButton leftPlayButton, rightPlayButton;
        VideoView leftChatVideoView, rightChatVideoView;

        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextview, rightChatTextview, leftChatTimestamp, rightChatTimestamp;
        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            leftChatTimestamp = itemView.findViewById(R.id.left_chat_timestamp);
            rightChatTimestamp = itemView.findViewById(R.id.right_chat_timestamp);
            leftChatImageView = itemView.findViewById(R.id.left_chat_imageview);
            rightChatImageView = itemView.findViewById(R.id.right_chat_imageview);
            leftChatVideoView = itemView.findViewById(R.id.left_chat_video_view);
            rightChatVideoView = itemView.findViewById(R.id.right_chat_video_view);
            leftPlayButton = itemView.findViewById(R.id.left_play_button);
            rightPlayButton = itemView.findViewById(R.id.right_play_button);


            rightChatTextview.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(itemView, getAdapterPosition());
                }
                return true;
            });
        }
    }
}
