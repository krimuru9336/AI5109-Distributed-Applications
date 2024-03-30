package com.example.chatstnr.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatstnr.ChatActivity;
import com.example.chatstnr.GroupChatActivity;
import com.example.chatstnr.R;
import com.example.chatstnr.models.GroupModel;
import com.example.chatstnr.models.GroupModel;
import com.example.chatstnr.models.UserModel;
import com.example.chatstnr.utils.AndroidUtil;
import com.example.chatstnr.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class RecentGroupChatRecyclerAdapter extends FirestoreRecyclerAdapter<GroupModel, RecentGroupChatRecyclerAdapter.GroupModelViewHolder> {

    Context context;
    UserModel currentUser;

    public RecentGroupChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<GroupModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupModelViewHolder holder, int position, @NonNull GroupModel model) {

        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserid());

        holder.groupnameText.setText(model.getGroupName());
        holder.lastMessageText.setText(model.getLastMessage());

        String lastMessage = model.getLastMessage();
        if (lastMessage != null && lastMessage.length() > 25) {
            // Truncate the message to 25 characters
            lastMessage = lastMessage.substring(0, 25) + "...";
        }

        if (lastMessageSentByMe) {
            holder.lastMessageText.setText("You : " + lastMessage);
        } else {
            holder.lastMessageText.setText(lastMessage);
        }

//        if(holder.lastMessageTime !=null){
//            holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));
//        }

        holder.itemView.setOnClickListener(v -> {
            //navigate to chat activity
            Intent intent = new Intent(context, GroupChatActivity.class);
            AndroidUtil.passGroupModelAsIntent(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });




    }

    @NonNull
    @Override
    public GroupModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new GroupModelViewHolder(view);
    }

    class GroupModelViewHolder extends RecyclerView.ViewHolder {
        TextView groupnameText;
        TextView lastMessageText;
        TextView lastMessageTime;

        public GroupModelViewHolder(@NonNull View itemView) {
            super(itemView);
            groupnameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
        }
    }
}