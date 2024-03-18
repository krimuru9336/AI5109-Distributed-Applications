package com.example.chatstnr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatstnr.R;
import com.example.chatstnr.models.UserModel;
import java.util.List;

public class UserlistAdapter extends RecyclerView.Adapter<UserlistAdapter.UserModelViewHolder> {

    private List<UserModel> userList;
    private Context context;

    public UserlistAdapter(List<UserModel> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserModelViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.usernameText.setText(user.getUsername());
        holder.phoneText.setText(user.getPhone());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserModelViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePic;
        TextView usernameText;
        TextView phoneText;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
