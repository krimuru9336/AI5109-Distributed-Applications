package com.example.cchat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cchat.R;
import com.example.cchat.model.ChatRoomModel;
import com.example.cchat.model.UserModel;
import com.example.cchat.utils.AndroidUtil;
import com.example.cchat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class AddUsersRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, AddUsersRecyclerAdapter.AddUsersModelViewHolder> {

    Context context;
    ArrayList<String> selectedUsers = new ArrayList<>();
    private AddUsersRecyclerAdapter.OnClickListener clickListener;

    public AddUsersRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context, AddUsersRecyclerAdapter.OnClickListener clickListener) {
        super(options);
        this.context = context;
        this.clickListener = clickListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull AddUsersModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                        FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()) {
                                        Uri uri = task1.getResult();
                                        AndroidUtil.setProfilePicture(context, uri, holder.profilePicture);
                                    }
                                });
                        holder.usernameText.setText(otherUserModel.getUsername());
                        holder.phoneNumberText.setText(otherUserModel.getPhone());
                        holder.userId = otherUserModel.getUserId();
                        holder.itemView.setOnClickListener(v -> {
                            //Select users
                            if(clickListener != null)
                                if(selectedUsers.contains(holder.userId)) {
                                    holder.itemView.setBackgroundColor(Color.WHITE);
                                    selectedUsers.remove(holder.userId);
                                } else {
                                    holder.itemView.setBackgroundColor(Color.LTGRAY);
                                    selectedUsers.add(holder.userId);
                                }
                                clickListener.onUserSelected(selectedUsers);
                        });
                    }
                });
    }

    @NonNull
    @Override
    public AddUsersRecyclerAdapter.AddUsersModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_users_recycler_row,parent, false);
        return new AddUsersRecyclerAdapter.AddUsersModelViewHolder(view);
    }

    public interface OnClickListener {
        void onUserSelected(ArrayList<String> users);
    }

    class AddUsersModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView phoneNumberText;
        ImageView profilePicture;
        String userId;
        public AddUsersModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            phoneNumberText = itemView.findViewById(R.id.phone_text);
            profilePicture = itemView.findViewById(R.id.profile_image_view);
            userId = "";
        }
    }
}
