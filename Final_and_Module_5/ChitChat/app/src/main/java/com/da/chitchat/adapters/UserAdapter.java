package com.da.chitchat.adapters;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
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
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            if (payloads.get(0) instanceof String) {
                holder.changeColor(String.valueOf(payloads.get(0)));
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUser(String username) {
        if (userList.contains(username)) {
            notifyItemChanged(userList.indexOf(username), "online");
        } else {
            userList.add(0, username);
            notifyItemInserted(0);
        }
    }

    public void removeUser(String username) {
        int position = userList.indexOf(username);
        if (position >= 0) {
            //userList.remove(position);
            //notifyItemRemoved(position);
            notifyItemChanged(position, "offline");
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView usernameTextView;
        private final View usernameContainerView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            usernameContainerView = itemView.findViewById(R.id.usernameContainer);

            // Set click listener for the item view
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String clickedUser = userList.get(position);

                    // Handle click (e.g., open new activity for messages)
                    openMessageActivity(clickedUser);
                }
            });
        }

        public void bind(String username) {
            usernameTextView.setText(username);
            changeColor("online");
        }

        private void openMessageActivity(String clickedUser) {
            Intent intent = new Intent(itemView.getContext(), MessageActivity.class);
            intent.putExtra("TARGET_USER", clickedUser);
            itemView.getContext().startActivity(intent);
        }

        public void changeColor(String action) {
            int colorID = R.color.colorPrimary;
            if (action.equals("offline")) {
                colorID = R.color.colorOffline;
            }
            Drawable backgroundDrawable = usernameContainerView.getBackground();
            if (backgroundDrawable instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) backgroundDrawable;

                // Change the color of the solid part
                gradientDrawable.setColor(ContextCompat.getColor(itemView.getContext(), colorID));
            }
        }
    }
}
