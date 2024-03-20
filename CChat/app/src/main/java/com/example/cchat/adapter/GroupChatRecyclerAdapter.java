package com.example.cchat.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cchat.R;
import com.example.cchat.model.ChatMessageModel;
import com.example.cchat.model.ChatRoomModel;
import com.example.cchat.utils.AndroidUtil;
import com.example.cchat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class GroupChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, GroupChatRecyclerAdapter.ChatModelViewHolder> {
    Context context;
    private GroupChatRecyclerAdapter.OnChatMessageClickListener clickListener;
    public GroupChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context, GroupChatRecyclerAdapter.OnChatMessageClickListener clickListener) {
        super(options);
        this.context = context;
        this.clickListener = clickListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupChatRecyclerAdapter.ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        int currentPosition = position + 1;

        if(model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            holder.receivedMsgLayout.setVisibility(View.GONE);
            holder.sentMsgLayout.setVisibility(View.VISIBLE);
            if(model.getMsgType().equals("text")) {
                holder.sentMediaView.setVisibility(View.GONE);
                holder.sentMsgView.setText(model.getMessage());
            } else {
                holder.sentMsgView.setVisibility(View.GONE);
                Uri uri = Uri.parse(model.getMessage());
                Glide.with(context).load(uri).apply(RequestOptions.noTransformation()).into(holder.sentMediaView);
            }

            holder.sentMsgTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));

            holder.sentMsgLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
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
            holder.senderPlaceHolder.setText(model.getSenderName());
            FirebaseUtil.getOtherProfilePicStorageRef(model.getSenderId()).getDownloadUrl()
                    .addOnCompleteListener(task1 -> {
                        Log.e("picture", "here");
                        if(task1.isSuccessful()) {
                            Uri uri = task1.getResult();
                            AndroidUtil.setProfilePicture(context, uri, holder.senderProfilePicture);
                        }
                    });

            if(currentPosition < getItemCount()) {
                String previousSenderId = (String) getSnapshots().getSnapshot(currentPosition).get("senderId");
                if(model.getSenderId().equals(previousSenderId)) {
                    holder.senderPlaceHolder.setVisibility(View.GONE);
                    holder.senderProfilePicture.setVisibility(View.GONE);
                }
            }

            if(model.getMsgType().equals("text")) {
                holder.receivedMediaView.setVisibility(View.GONE);
                holder.receivedMsgView.setText(model.getMessage());
            } else {
                holder.receivedMsgView.setVisibility(View.GONE);
                Uri uri = Uri.parse(model.getMessage());
                Glide.with(context).load(uri).apply(RequestOptions.noTransformation()).into(holder.receivedMediaView);
            }
            holder.receivedMsgTimestamp.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
        }
    }

    @NonNull
    @Override
    public GroupChatRecyclerAdapter.ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.groupchat_recycler_row,parent, false);
        return new GroupChatRecyclerAdapter.ChatModelViewHolder(view);
    }

    public interface OnChatMessageClickListener {
        void onChatMessageClicked(ChatMessageModel chatMessage, String messageId);
    }
    class ChatModelViewHolder extends RecyclerView.ViewHolder {

        LinearLayout receivedMsgLayout;
        LinearLayout sentMsgLayout;

        ImageView senderProfilePicture;
        TextView senderPlaceHolder;
        TextView receivedMsgView;
        ImageView receivedMediaView;
        TextView receivedMsgTimestamp;

        TextView sentMsgView;
        ImageView sentMediaView;
        TextView sentMsgTimestamp;


        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            receivedMsgLayout = itemView.findViewById(R.id.received_msg_layout);
            sentMsgLayout = itemView.findViewById(R.id.sent_msg_layout);
            senderProfilePicture = itemView.findViewById(R.id.profile_image_view);
            senderPlaceHolder = itemView.findViewById(R.id.sender_placeholder);
            receivedMsgView = itemView.findViewById(R.id.received_msg_textview);
            receivedMediaView = itemView.findViewById(R.id.received_media_view);
            receivedMsgTimestamp = itemView.findViewById(R.id.received_msg_timestamp);
            sentMsgView = itemView.findViewById(R.id.sent_msg_textview);
            sentMediaView = itemView.findViewById(R.id.sent_media_view);
            sentMsgTimestamp = itemView.findViewById(R.id.sent_msg_timestamp);
        }
    }
}
