package com.da.chitchat.adapters;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.da.chitchat.R;
import com.da.chitchat.activities.MessageActivity;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private static List<String> userList;
    private static List<String> onlineStatePayloadList;

    public UserAdapter(List<String> userList) {
        UserAdapter.userList = userList;
        if (onlineStatePayloadList == null)
            onlineStatePayloadList = new ArrayList<>();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_overview, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String username = userList.get(position);
        String payload = onlineStatePayloadList.get(position);
        holder.bind(username, payload);
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

    public void addUser(String username, boolean isOnline) {
        String payload = "online";
        if (!isOnline) payload = "offline";
        Log.d("OnlineLog", "" + payload);
        if (userList.contains(username)) {
            notifyItemChanged(userList.indexOf(username), payload);
        } else {
            userList.add(0, username);
            onlineStatePayloadList.add(0, payload);
            notifyItemInserted(0);
        }
    }

    public void addUser(String username) {
        addUser(username, true);
    }

    public void removeUser(String username) {
        int position = userList.indexOf(username);
        if (position >= 0) {
            notifyItemChanged(position, "offline");
        }
    }

    public static String[] getUsers() {
        return userList.toArray(new String[0]);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView usernameTextView;
        private final View usernameContainerView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.overviewItemTextView);
            usernameContainerView = itemView.findViewById(R.id.overviewItemContainer);

            // Set click listener for the item view
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String clickedUser = userList.get(position);

                    // Handle click
                    openMessageActivity(clickedUser);
                }
            });
        }

        public void bind(String username, String payload) {
            usernameTextView.setText(username);
            changeColor(payload);
        }

        private void openMessageActivity(String clickedUser) {
            Intent intent = new Intent(itemView.getContext(), MessageActivity.class);
            intent.putExtra("TARGET_PARTNER", clickedUser);
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
