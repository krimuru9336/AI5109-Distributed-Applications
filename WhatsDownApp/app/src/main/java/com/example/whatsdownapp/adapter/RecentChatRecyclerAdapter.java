package com.example.whatsdownapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsdownapp.ChatActivity;
import com.example.whatsdownapp.R;
import com.example.whatsdownapp.model.ChatMessageModel;
import com.example.whatsdownapp.model.ChatroomModel;
import com.example.whatsdownapp.model.UserModel;
import com.example.whatsdownapp.utils.AndroidUtil;
import com.example.whatsdownapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatRoomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatRoom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());
                        boolean isDeletedLastMsg = model.getlastMessage().isDeleted();
                        ChatMessageModel.MessageType type = model.getlastMessage().getMessageType();
                        String lastMessageText = model.getlastMessage().getMessage();

                        UserModel otherUser = task.getResult().toObject(UserModel.class);
                        if (otherUser != null) {
                            FirebaseUtil.getCurrentProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()){
                                            Uri uri = task1.getResult();
                                            AndroidUtil.setProfilePic(context, uri, holder.profilePic );
                                        }
                                    });


                            if (otherUser.getUserId().equals(FirebaseUtil.currentUserId())) {
                                holder.usernameText.setText(String.format("%s (Me)", otherUser.getUsername()));
                            } else {
                                holder.usernameText.setText(otherUser.getUsername());
                            }

                        }
                        if (isDeletedLastMsg) {
                            lastMessageText = "This message was deleted.";
                        }

                        if (type != ChatMessageModel.MessageType.TEXT) {
                            lastMessageText = "📷 Attachment";
                        }

                        if (lastMessageSentByMe) {
                            lastMessageText = isDeletedLastMsg ? "You deleted this message." : String.format("You: %s", lastMessageText);
                        }

                        holder.lastMsgText.setText(lastMessageText);

                        holder.lastMsgTime.setText(FirebaseUtil.timestampToString(model.getlastMessage().getTimestamp()));

                        holder.itemView.setOnClickListener(v -> {
                            //navigate to chat activity
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUser);
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
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);

        }


    }
}