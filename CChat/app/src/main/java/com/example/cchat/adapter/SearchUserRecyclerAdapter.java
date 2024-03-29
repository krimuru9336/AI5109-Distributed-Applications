package com.example.cchat.adapter;

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

import com.example.cchat.ChatActivity;
import com.example.cchat.R;
import com.example.cchat.model.UserModel;
import com.example.cchat.utils.AndroidUtil;
import com.example.cchat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.concurrent.locks.ReentrantLock;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;
    private final ReentrantLock viewHolderLock = new ReentrantLock();
    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        try {
            viewHolderLock.lock();

            holder.usernameText.setText(model.getUsername());
            holder.phoneText.setText(model.getPhone());
            holder.groupIcon.setVisibility(View.GONE);
            if (model.getUserId().equals(FirebaseUtil.currentUserId())) {
                holder.usernameText.setText(model.getUsername() + " (YOU)");
            }

            FirebaseUtil.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl()
                    .addOnCompleteListener(task1 -> {
                        Uri uri = task1.getResult();
                        AndroidUtil.setProfilePicture(context, uri, holder.profilePicture);
                    });

            holder.itemView.setOnClickListener(v -> {
                //Navigate to chat activity
                Intent intent = new Intent(context, ChatActivity.class);
                AndroidUtil.passUserModelAsIntent(intent, model);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });
        } finally {
            viewHolderLock.unlock();
        }
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seach_user_recycler_row,parent, false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView phoneText;
        ImageView profilePicture;
        ImageView groupIcon;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilePicture = itemView.findViewById(R.id.profile_image_view);
            groupIcon = itemView.findViewById(R.id.group_image_view);
        }
    }
}
