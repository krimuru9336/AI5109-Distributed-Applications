package com.da.chitchat.adapters;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.da.chitchat.R;
import com.da.chitchat.activities.MessageActivity;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private static List<String> groupList;

    public GroupAdapter(List<String> groupList) {
        GroupAdapter.groupList = groupList;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_overview, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        String groupName = groupList.get(position);
        holder.bind(groupName);
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public void addGroup(String groupName) {
        groupList.add(0, groupName);
        notifyItemInserted(0);
    }

    public void removeGroup(String groupName) {
        int position = groupName.indexOf(groupName);
        if (position >= 0) {
            groupList.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {

        private final TextView overviewItemTextView;
        private final View overviewItemContainerView;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            overviewItemTextView = itemView.findViewById(R.id.overviewItemTextView);
            overviewItemContainerView = itemView.findViewById(R.id.overviewItemContainer);

            // Set click listener for the item view
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String clickedGroup = groupList.get(position);

                    // Handle click
                    openMessageActivity(clickedGroup);
                }
            });
        }

        public void bind(String groupName) {
            overviewItemTextView.setText(groupName);
            setColor();
        }

        private void openMessageActivity(String clickedGroup) {
            Intent intent = new Intent(itemView.getContext(), MessageActivity.class);
            intent.putExtra("TARGET_PARTNER", clickedGroup);
            intent.putExtra("IS_GROUP", true);
            itemView.getContext().startActivity(intent);
        }

        // If UserAdapter gets recycled for GroupAdapter
        public void setColor() {
            int colorID = R.color.colorPrimary;
            Drawable backgroundDrawable = overviewItemContainerView.getBackground();
            if (backgroundDrawable instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) backgroundDrawable;

                // Change the color of the solid part
                gradientDrawable.setColor(ContextCompat.getColor(itemView.getContext(), colorID));
            }
        }
    }
}
