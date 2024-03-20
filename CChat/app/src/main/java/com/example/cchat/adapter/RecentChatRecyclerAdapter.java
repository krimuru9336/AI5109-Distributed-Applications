package com.example.cchat.adapter;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cchat.ChatActivity;
import com.example.cchat.GroupChatActivity;
import com.example.cchat.R;
import com.example.cchat.model.ChatRoomModel;
import com.example.cchat.model.UserModel;
import com.example.cchat.utils.AndroidUtil;
import com.example.cchat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecyclerAdapter.ChatRoomModelViewHolder> {

    Context context;
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());
                        boolean isMediaMessage = model.getLastMessageType().equals("media");
                        boolean isGroup = model.getChatroomType().equals("group");
                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);


                        if(!isGroup) {
                            holder.groupIcon.setVisibility(View.GONE);
                            FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                    .addOnCompleteListener(task1 -> {
                                        Log.e("picture", "here");
                                        if(task1.isSuccessful()) {
                                            Uri uri = task1.getResult();
                                            AndroidUtil.setProfilePicture(context, uri, holder.profilePicture);
                                        }
                                    });

                            holder.usernameText.setText(otherUserModel.getUsername());
                        } else {
                            holder.profilePicture.setVisibility(View.GONE);
                            holder.usernameText.setText(model.getGroupName());
                        }

                        if(lastMessageSentByMe) {
                            if(isMediaMessage)
                                holder.lastMessageText.setText("You: Image");
                            else
                                holder.lastMessageText.setText("You: " + model.getLastMessage());
                        } else {
                            if(isMediaMessage)
                                holder.lastMessageText.setText("Image");
                            else
                                holder.lastMessageText.setText(model.getLastMessage());
                        }

                        holder.lastMessageTimeText.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        holder.itemView.setOnClickListener(v -> {
                            //Navigate to chat activity
                            Intent intent;
                            if(!isGroup) {
                                intent = new Intent(context, ChatActivity.class);
                                AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                            } else {
                                intent = new Intent(context, GroupChatActivity.class);
                                intent.putExtra("chatroomId", model.getChatroomId());
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
                    }
                });
    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent, false);
        return new ChatRoomModelViewHolder(view);
    }

    class ChatRoomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTimeText;
        ImageView profilePicture;
        ImageView groupIcon;

        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            lastMessageText = itemView.findViewById(R.id.last_msg_text);
            lastMessageTimeText = itemView.findViewById(R.id.timestamp_text);
            profilePicture = itemView.findViewById(R.id.profile_image_view);
            groupIcon = itemView.findViewById(R.id.group_image_view);
        }
    }
}
