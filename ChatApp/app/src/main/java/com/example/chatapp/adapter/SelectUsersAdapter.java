package com.example.chatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class SelectUsersAdapter extends RecyclerView.Adapter<SelectUsersAdapter.UserViewHolder> {

    private List<UserModel> userList;
    private List<UserModel> selectedUsers;

    public SelectUsersAdapter(List<UserModel> userList) {
        this.userList = userList;
        this.selectedUsers = new ArrayList<>();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.usernameTextView.setText(user.getUsername());
        holder.checkBox.setChecked(user.isSelected());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            user.setSelected(isChecked);
            if (isChecked) {
                selectedUsers.add(user);
            } else {
                selectedUsers.remove(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        CheckBox checkBox;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_text_view);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }

    // Method to get selected users
    public List<UserModel> getSelectedUsers() {
        return selectedUsers;
    }
}
