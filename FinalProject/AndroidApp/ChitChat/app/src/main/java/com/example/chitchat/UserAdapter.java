package com.example.chitchat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private static List<String> userList;

    public final static int countChatrooms = 3;
    private TextView uotv;
    public UserAdapter(List<String> ul,TextView uotv){
        userList = ul;
        this.uotv = uotv;
        uotv.setText(getUsersOnlineText());
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user, parent, false);
        return new UserViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder uvh, int pos) {
        String username = userList.get(pos);
        uvh.bind(username);
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    public String getUsersOnlineText(){
        int usersOnline = getItemCount();
        int countUsers = Math.max(usersOnline-countChatrooms,0);
        return "Users online ("+countUsers+")";
    }
    public void addUser(String username){
        userList.add(0, username);
        notifyItemInserted(0);
        uotv.setText(getUsersOnlineText());
    }
    public void removeUser(String username){
        int pos = userList.indexOf(username);
        if (pos >= 0) {
            userList.remove(pos);
            notifyItemRemoved(pos);
            uotv.setText(getUsersOnlineText());
        }
    }
    static class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView usernameTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    String clickedUser = userList.get(pos);

                    openMessageActivity(clickedUser);
                }
            });
        }

        public void bind(String username) {
            usernameTextView.setText(username);
        }

        private void openMessageActivity(String clickedUser) {
            Intent intent = new Intent(itemView.getContext(), ChatActivity.class);
            intent.putExtra("USERDEST", clickedUser);
            itemView.getContext().startActivity(intent);
        }
    }
}

