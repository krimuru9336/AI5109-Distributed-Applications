package com.example.letschat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.MessageOptionsBottomSheet;
import com.example.letschat.R;
import com.example.letschat.model.ChatMessage;
import com.example.letschat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessage, ChatRecyclerAdapter.ChatModelViewHolder> {
    Context context;

    private OnChatItemClickListener listener;

    public interface OnChatItemClickListener {
        void onLongPress(int position, ChatMessage chatMessage);
    }

    public void setOnChatItemClickListener(OnChatItemClickListener listener) {
        this.listener = listener;
    }

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRecyclerAdapter.ChatModelViewHolder holder, int position, @NonNull ChatMessage model) {

        boolean isCurrentUserId = model.getSenderId().equals(FirebaseUtil.currentUserId());
        boolean isDeletedMessage = model.isDeleted();

        if (isCurrentUserId) {
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);

            switch (model.getMessageType()) {
                case IMAGE:
                    holder.rightChatImageView.setVisibility(View.VISIBLE);
                    holder.rightChatTextView.setVisibility(View.GONE);
                    Glide.with(holder.itemView.getContext()).load(model.getMessage()).into(holder.rightChatImageView);
                    break;

                case VIDEO:
                    holder.rightChatImageView.setVisibility(View.GONE);
                    holder.rightChatTextView.setVisibility(View.GONE);
                    holder.rightVideoView.setVisibility(View.VISIBLE);
                    String videoUrl = model.getMessage();
                    holder.rightVideoView.setVideoURI(Uri.parse(videoUrl));
                    MediaController mediaController = new MediaController(context);
                    mediaController.setAnchorView(holder.rightVideoView);
                    mediaController.setMediaPlayer(holder.rightVideoView);
                    holder.rightVideoView.setMediaController(mediaController);
                    holder.rightVideoView.start();
                    break;

                case GIF:
                    holder.rightChatImageView.setVisibility(View.VISIBLE);
                    holder.rightChatTextView.setVisibility(View.GONE);
                    Glide.with(holder.itemView.getContext()).asGif().load(model.getMessage()).into(holder.rightChatImageView);
                    break;

                default:
                    holder.rightChatImageView.setVisibility(View.GONE);
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
                    Glide.with(holder.itemView.getContext()).load(model.getMessage()).into(holder.leftChatImageView);
                    break;

                case VIDEO:
                    holder.leftChatImageView.setVisibility(View.GONE);
                    holder.leftChatTextView.setVisibility(View.GONE);
                    holder.leftVideoView.setVisibility(View.VISIBLE);
                    String videoUrl = model.getMessage();
                    holder.leftVideoView.setVideoURI(Uri.parse(videoUrl));
                    MediaController mediaController = new MediaController(context);
                    mediaController.setAnchorView(holder.leftVideoView);
                    holder.leftVideoView.setMediaController(mediaController);
                    holder.leftVideoView.start();
                    break;

                case GIF:
                    holder.leftChatImageView.setVisibility(View.VISIBLE);
                    holder.leftChatTextView.setVisibility(View.GONE);
                    Glide.with(holder.itemView.getContext()).asGif().load(model.getMessage()).into(holder.leftChatImageView);
                    break;

                default:
                    holder.leftChatImageView.setVisibility(View.GONE);
                    holder.leftChatTextView.setVisibility(View.VISIBLE);
                    String messageText = isDeletedMessage ? "This message was deleted." : model.getMessage();
                    holder.leftChatTextView.setText(messageText);
                    break;
            }

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
