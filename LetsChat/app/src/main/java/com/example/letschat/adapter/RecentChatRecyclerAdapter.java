package com.example.letschat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.ChatActivity;
import com.example.letschat.R;
import com.example.letschat.model.ChatRoom;
import com.example.letschat.model.User;
import com.example.letschat.util.AndroidUtil;
import com.example.letschat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoom, RecentChatRecyclerAdapter.ChatRoomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoom> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoom model) {
        FirebaseUtil.getOtherUserFromChatRoom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        boolean lastMessageSentByMe = model.getLastMsgSenderId().equals(FirebaseUtil.currentUserId());
                        boolean isDeletedLastMsg = model.getLastMsg().isDeleted();
                        String lastMessageText = "";

                        User otherUser = task.getResult().toObject(User.class);

                        if (otherUser != null) {
                            if (otherUser.getUserId().equals(FirebaseUtil.currentUserId())) {
                                holder.usernameText.setText(String.format("%s (Me)", otherUser.getUsername()));
                            } else {
                                holder.usernameText.setText(otherUser.getUsername());
                            }

                        }
                        if (isDeletedLastMsg) {
                            lastMessageText = "This message was deleted.";
                        } else {
                            lastMessageText = model.getLastMsgText();
                        }

                        if (lastMessageSentByMe) {
                            lastMessageText = isDeletedLastMsg ? "You deleted this message." : String.format("You: %s", lastMessageText);
                        }

                        holder.lastMsgText.setText(lastMessageText);

                        holder.lastMsgTime.setText(FirebaseUtil.timestampToString(model.getLastMsgTimestamp()));

                        holder.itemView.setOnClickListener(v -> {
                            //navigate to chat activity
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserDataAsIntent(intent, otherUser);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });

                    }
                });
    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_view, parent, false);
        return new ChatRoomModelViewHolder(view);
    }

    class ChatRoomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView lastMsgText;
        TextView lastMsgTime;
        ImageView profilePic;

        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.recent_username_text);
            lastMsgText = itemView.findViewById(R.id.last_msg_text);
            lastMsgTime = itemView.findViewById(R.id.last_msg_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_img_view);

        }


    }
}

