package com.example.whatsdown.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsdown.ChatActivity;
import com.example.whatsdown.R;
import com.example.whatsdown.model.ChatroomModel;
import com.example.whatsdown.model.UserModel;
import com.example.whatsdown.utils.AndroidUtil;
import com.example.whatsdown.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter <ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder>{

    Context context;
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());
                        boolean isLastMessageDeleted = model.getLastMessage().isDeleted();
                        String lastMessageText = model.getLastMessage().getMessage();

                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                        holder.usernameText.setText(otherUserModel.getUsername());
                        if (isLastMessageDeleted) {
                            lastMessageText = "This message was deleted.";
                        }

                        if (lastMessageSentByMe)
                            lastMessageText = isLastMessageDeleted ? "You deleted this message." : String.format("You: %s", lastMessageText);
                        holder.lastMessageText.setText(lastMessageText);

                        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessage().getTimestamp()));

                        holder.itemView.setOnClickListener(v -> {
                            //navigate to chat activity
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.PassUserModelAsIntent(intent, otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
                    }
                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }
    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }

}
