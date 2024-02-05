package com.example.chatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.ChatActivity;
import com.example.chatapp.R;
import com.example.chatapp.model.ChatMessageModel;
import com.example.chatapp.model.ChatroomModel;
import com.example.chatapp.model.UserModel;
import com.example.chatapp.utils.AndroidUtil;
import com.example.chatapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    private Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                        FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if(t.isSuccessful()){
                                        Uri uri = t.getResult();
                                        AndroidUtil.setProfilePic(context,uri,holder.profilePic);
                                    }
                                });

                        holder.usernameText.setText(otherUserModel.getUsername());


                        // Fetch the last sent and undeleted message
                        FirebaseUtil.getChatroomMessageReference(model.getChatroomId())
                                .whereEqualTo("senderId", FirebaseUtil.currentUserId())
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .limit(1)
                                .get()
                                .addOnCompleteListener(messageTask -> {
                                    if (messageTask.isSuccessful()) {
                                        for (DocumentSnapshot document : messageTask.getResult()) {
                                            ChatMessageModel lastMessage = document.toObject(ChatMessageModel.class);
                                            if (lastMessage != null && !lastMessage.isDeleted()) {
                                                if (lastMessageSentByMe) {
                                                    holder.lastMessageText.setText("You: " + lastMessage.getMessage());
                                                } else {
                                                    holder.lastMessageText.setText(lastMessage.getMessage());
                                                }
                                                holder.lastMessageTime.setText(FirebaseUtil.timeStampToString(lastMessage.getTimestamp()));
                                                break;
                                            }
                                        }
                                    } else {
                                        // Handle the case where fetching the last message was not successful
                                        holder.lastMessageText.setText("Error loading message"+ messageTask.getException());
                                        holder.lastMessageTime.setText("");
                                    }
                                });

                        holder.itemView.setOnClickListener(v -> {
                            // Navigate to chat activity
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
                    } else {
                        // Handle the case where the task was not successful
                        // For example, log an error or set default values in your views
                        holder.usernameText.setText("Unknown User");
                        holder.lastMessageText.setText("Error loading data");
                        holder.lastMessageTime.setText("");
                        holder.itemView.setOnClickListener(null); // Disable click
                    }
                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    // ViewHolder class
    class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
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

    // Callbacks for handling dataset changes and errors
    @Override
    public void onDataChanged() {
        // Handle UI updates when the data set changes
        notifyDataSetChanged(); // For example, you might call notifyDataSetChanged()
    }

    @Override
    public void onError(FirebaseFirestoreException e) {
        // Handle errors when there is an issue fetching the data
        e.printStackTrace();
    }
}
