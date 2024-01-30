package com.da.chitchat.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.da.chitchat.activities.MessageActivity;
import com.da.chitchat.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private static List<String> userList;

    public UserAdapter(List<String> userList) {
        UserAdapter.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String username = userList.get(position);
        holder.bind(username);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUser(String username) {
        userList.add(0, username);
        notifyItemInserted(0);
    }

    public void removeUser(String username) {
        int position = userList.indexOf(username);
        if (position >= 0) {
            userList.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView usernameTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String clickedUser = userList.get(position);
                    
                    openMessageActivity(clickedUser);
                }
            });
        }

        public void bind(String username) {
            usernameTextView.setText(username);
        }

        private void openMessageActivity(String clickedUser) {
            Intent intent = new Intent(itemView.getContext(), MessageActivity.class);
            intent.putExtra("TARGET_USER", clickedUser);
            itemView.getContext().startActivity(intent);
        }
    }
}
