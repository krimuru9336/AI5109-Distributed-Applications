// Sven Schickentanz - fdai7287
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

/**
 * Adapter class for displaying a list of groups in a RecyclerView.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private static List<String> groupList;

    /**
     * Constructs a GroupAdapter with the given list of groups.
     *
     * @param groupList The list of groups to be displayed.
     */
    public GroupAdapter(List<String> groupList) {
        GroupAdapter.groupList = groupList;
    }

    /**
     * Creates a new view holder for the group item view.
     * 
     * @param parent The parent view group.
     * @param viewType The type of view.
     */
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_overview, parent, false);
        return new GroupViewHolder(view);
    }

    /**
     * Binds the group name to the view holder at the given position.
     * 
     * @param holder The view holder to bind the group name to.
     * @param position The position of the group in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        String groupName = groupList.get(position);
        holder.bind(groupName);
    }

    /**
     * Returns the number of groups in the list.
     * 
     * @return The number of groups in the list.
     */
    @Override
    public int getItemCount() {
        return groupList.size();
    }

    /**
     * Adds a new group to the beginning of the list.
     *
     * @param groupName The name of the group to be added.
     */
    public void addGroup(String groupName) {
        groupList.add(0, groupName);
        notifyItemInserted(0);
    }

    /**
     * Removes a group from the list.
     *
     * @param groupName The name of the group to be removed.
     */
    public void removeGroup(String groupName) {
        int position = groupName.indexOf(groupName);
        if (position >= 0) {
            groupList.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * View holder class for the group item view.
     */
    static class GroupViewHolder extends RecyclerView.ViewHolder {

        private final TextView overviewItemTextView;
        private final View overviewItemContainerView;

        /**
         * Constructs a GroupViewHolder with the given item view.
         * 
         * @param itemView The item view for the group.
         */
        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            overviewItemTextView = itemView.findViewById(R.id.overviewItemTextView);
            overviewItemContainerView = itemView.findViewById(R.id.overviewItemContainer);

            // Set click listener for the item view
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String clickedGroup = groupList.get(position);

                    // Handle click
                    openMessageActivity(clickedGroup);
                }
            });
        }

        /**
         * Binds the group name to the view holder.
         * 
         * @param groupName The name of the group to be displayed.
         */
        public void bind(String groupName) {
            overviewItemTextView.setText(groupName);
            // Set color for the group item
            setColor();
        }

        /**
         * Opens the message activity for the clicked group.
         * 
         * @param clickedGroup The name of the clicked group.
         */
        private void openMessageActivity(String clickedGroup) {
            Intent intent = new Intent(itemView.getContext(), MessageActivity.class);
            // Pass the target partner to the message activity
            intent.putExtra("TARGET_PARTNER", clickedGroup);
            // Pass the group flag to the message activity
            intent.putExtra("IS_GROUP", true);
            itemView.getContext().startActivity(intent);
        }

        /**
         * Sets the color of the group item.
         */
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
