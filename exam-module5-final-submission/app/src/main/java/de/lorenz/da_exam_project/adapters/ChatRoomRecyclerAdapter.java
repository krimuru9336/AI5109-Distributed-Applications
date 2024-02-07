package de.lorenz.da_exam_project.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.HashMap;

import de.lorenz.da_exam_project.R;
import de.lorenz.da_exam_project.listeners.chatroom.ChatMessageLongClickListener;
import de.lorenz.da_exam_project.models.ChatMessage;
import de.lorenz.da_exam_project.models.ChatRoom;
import de.lorenz.da_exam_project.utils.AndroidUtil;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class ChatRoomRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessage, ChatRoomRecyclerAdapter.ChatRoomModelViewHolder> {

    Context context;
    ChatRoom chatRoom;
    HashMap<String, String> colorCodes;

    public ChatRoomRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options, Context context, ChatRoom chatRoom) {
        super(options);
        this.context = context;
        this.chatRoom = chatRoom;
        this.colorCodes = new HashMap<>();
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatMessage model) {

        // decide whether to show the message on the left or right side based on the sender id
        if (model.getSenderId().equals(FirebaseUtil.getCurrentUserId())) {
            setChatMessageToRight(holder, model);
        } else {
            setChatMessageToLeft(holder, model);
        }

        // enable/add long click listener on chat messages for context menu
        holder.rightChatLayout.setLongClickable(true);
        ChatMessageLongClickListener chatMessageLongClickListener = new ChatMessageLongClickListener(context, holder.rightChatLayout, model);
        holder.rightChatLayout.setOnLongClickListener(chatMessageLongClickListener);
    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_recycler_view, parent, false);
        return new ChatRoomModelViewHolder(view);
    }

    /**
     * Sets the given chat message to the left side of the chat room.
     */
    private void setChatMessageToLeft(ChatRoomModelViewHolder holder, ChatMessage model) {
        holder.leftChatLayout.setVisibility(View.VISIBLE);
        holder.rightChatLayout.setVisibility(View.GONE);

        holder.leftChatImage.setVisibility(View.GONE);
        holder.leftChatTextView.setVisibility(View.GONE);
        holder.leftChatVideo.setVisibility(View.GONE);

        // hide username if the chat is a private chat
        setUsernames(holder, model);

        if (model.getType() == ChatMessage.Type.IMAGE) {
            downloadImageBitmapAndSetIntoImageView(model.getMessage(), holder.leftChatImage);
            holder.leftChatImage.setVisibility(View.VISIBLE);
        } else if (model.getType() == ChatMessage.Type.VIDEO) {
            downloadVideoAndSetIntoVideoView(model.getMessage(), holder.leftChatVideo);
            holder.leftChatVideo.setVisibility(View.VISIBLE);
        } else {    // text message
            holder.leftChatTextView.setText(model.getMessage());
            holder.leftChatTextView.setVisibility(View.VISIBLE);
        }

        holder.leftChatTimestampTextView.setText(AndroidUtil.getFormattedDate(context, model.getTimestamp()));
    }

    private void setUsernames(ChatRoomModelViewHolder holder, ChatMessage model) {
        if (!chatRoom.isGroup()) {
            holder.leftChatUsername.setVisibility(View.GONE);
            return;
        }

        String senderId = model.getSenderId();

        // get user from sender id
        FirebaseUtil.getUser(senderId).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }

            // get color for user
            if (!this.colorCodes.containsKey(senderId)) {
                this.colorCodes.put(senderId, AndroidUtil.generateRandomHexColor());
            }
            String colorCode = this.colorCodes.get(senderId);

            String username = task.getResult().getString("username");
            holder.leftChatUsername.setText(username);
            holder.leftChatUsername.setTextColor(Color.parseColor(colorCode));
            holder.leftChatUsername.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Sets the given chat message to the right side of the chat room.
     */
    private void setChatMessageToRight(ChatRoomModelViewHolder holder, ChatMessage model) {
        holder.leftChatLayout.setVisibility(View.GONE);
        holder.rightChatLayout.setVisibility(View.VISIBLE);

        holder.rightChatImage.setVisibility(View.GONE);
        holder.rightChatTextView.setVisibility(View.GONE);
        holder.rightChatVideo.setVisibility(View.GONE);

        if (model.getType() == ChatMessage.Type.IMAGE) {
            downloadImageBitmapAndSetIntoImageView(model.getMessage(), holder.rightChatImage);
            holder.rightChatImage.setVisibility(View.VISIBLE);
        } else if (model.getType() == ChatMessage.Type.VIDEO) {
            downloadVideoAndSetIntoVideoView(model.getMessage(), holder.rightChatVideo);
            holder.rightChatVideo.setVisibility(View.VISIBLE);
        } else {    // text message
            holder.rightChatTextView.setText(model.getMessage());
            holder.rightChatTextView.setVisibility(View.VISIBLE);
        }

        holder.rightChatTimestampTextView.setText(AndroidUtil.getFormattedDate(context, model.getTimestamp()));
    }

    /**
     * This function downloads a bitmap from the given url and sets it to the given image view.
     */
    private void downloadImageBitmapAndSetIntoImageView(String url, ImageView view) {
        Glide.with(this.context)
                .load(url)
                .into(view);
    }

    private void downloadVideoAndSetIntoVideoView(String url, VideoView view) {
        Uri videoUri = Uri.parse(url);
        view.setVideoURI(videoUri);

        MediaController mediaController = new MediaController(this.context);
        mediaController.setAnchorView(view);
        view.setMediaController(mediaController);

        view.start();
    }

    static class ChatRoomModelViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout;
        LinearLayout rightChatLayout;
        TextView leftChatTextView;
        TextView rightChatTextView;
        TextView leftChatUsername;
        TextView leftChatTimestampTextView;
        TextView rightChatTimestampTextView;
        ImageView leftChatImage;
        ImageView rightChatImage;
        VideoView leftChatVideo;
        VideoView rightChatVideo;

        public ChatRoomModelViewHolder(View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextView = itemView.findViewById(R.id.left_chat_text_view);
            rightChatTextView = itemView.findViewById(R.id.right_chat_text_view);
            leftChatUsername = itemView.findViewById(R.id.left_chat_username);
            leftChatTimestampTextView = itemView.findViewById(R.id.left_chat_timestamp);
            rightChatTimestampTextView = itemView.findViewById(R.id.right_chat_timestamp);
            leftChatImage = itemView.findViewById(R.id.left_chat_image);
            rightChatImage = itemView.findViewById(R.id.right_chat_image);
            leftChatVideo = itemView.findViewById(R.id.left_chat_video);
            rightChatVideo = itemView.findViewById(R.id.right_chat_video);
        }
    }
}
