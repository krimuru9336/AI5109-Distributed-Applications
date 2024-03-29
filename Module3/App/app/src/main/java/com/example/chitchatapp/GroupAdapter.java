package com.example.chitchatapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private static Map<Integer, String> groupMap;

    public GroupAdapter(Map<Integer, String> groupMap) {
        GroupAdapter.groupMap = groupMap;
    }

    @NonNull
    @Override
    public GroupAdapter.GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new GroupAdapter.GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.GroupViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return groupMap.size();
    }

    public void addGroup(int groupId, String groupName) {
        groupMap.computeIfAbsent(groupId, k -> groupName);
        notifyItemInserted(groupId);
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {

        private final TextView usernameTextView;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    openMessageActivity(position);
                }
            });
        }

        public void bind(int groupId) {
            usernameTextView.setText(groupMap.get(groupId));
        }

        private void openMessageActivity(int clickedGroupId) {
            Intent intent = new Intent(itemView.getContext(), GroupMessageActivity.class);
            intent.putExtra("TARGET_GROUP", groupMap.get(clickedGroupId));
            intent.putExtra("GROUP_ID", clickedGroupId);
            itemView.getContext().startActivity(intent);
        }
    }
}
