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

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for displaying user data in a RecyclerView.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private static List<String> userList;
    private static List<String> onlineStatePayloadList;

    /**
     * Constructor for the UserAdapter class.
     * 
     * @param userList List of usernames to be displayed.
     */
    public UserAdapter(List<String> userList) {
        UserAdapter.userList = userList;
        if (onlineStatePayloadList == null)
            onlineStatePayloadList = new ArrayList<>();
    }

    /**
     * Creates a new view holder for the user item view.
     * 
     * @param parent The parent view group.
     * @param viewType The type of view.
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_overview, parent, false);
        return new UserViewHolder(view);
    }

    /**
     * Binds the user data to the view holder.
     * 
     * @param holder The view holder.
     * @param position The position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String username = userList.get(position);
        String payload = onlineStatePayloadList.get(position);
        holder.bind(username, payload);
    }

    /**
     * Binds the user data to the view holder with payloads.
     * Changes the color of the user item view based on the payload.
     * Can include online or offline status, changing the color of the item view.
     * Blue for online, red for offline.
     * 
     * @param holder The view holder.
     * @param position The position of the item in the list.
     * @param payloads The payloads to be bound.
     */
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

    /**
     * Returns the number of items in the list.
     * 
     * @return The number of items in the list.
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * Adds a user to the list of users.
     * 
     * @param username The username of the user to be added.
     * @param isOnline The online status of the user.
     */
    public void addUser(String username, boolean isOnline) {
        String payload = "online";
        if (!isOnline) payload = "offline";
        if (userList.contains(username)) {
            notifyItemChanged(userList.indexOf(username), payload);
        } else {
            userList.add(0, username);
            onlineStatePayloadList.add(0, payload);
            notifyItemInserted(0);
        }
    }

    /**
     * Adds a user to the list of users.
     * 
     * @param username The username of the user to be added.
     */
    public void addUser(String username) {
        addUser(username, true);
    }

    /**
     * Removes a user from the list of users.
     * 
     * @param username The username of the user to be removed.
     */
    public void removeUser(String username) {
        int position = userList.indexOf(username);
        if (position >= 0) {
            notifyItemChanged(position, "offline");
        }
    }

    /**
     * Returns the list of users.
     * 
     * @return The list of users.
     */
    public static String[] getUsers() {
        return userList.toArray(new String[0]);
    }

    /**
     * View holder class for the user item view.
     */
    static class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView usernameTextView;
        private final View usernameContainerView;

        /**
         * Constructor for the UserViewHolder class.
         * 
         * @param itemView The item view.
         */
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.overviewItemTextView);
            usernameContainerView = itemView.findViewById(R.id.overviewItemContainer);

            // Set click listener for the item view
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String clickedUser = userList.get(position);

                    // Opens the message activity with the clicked user
                    openMessageActivity(clickedUser);
                }
            });
        }

        /**
         * Binds the user data to the view holder.
         * 
         * @param username The username of the user.
         * @param payload The payload of the user.
         */
        public void bind(String username, String payload) {
            usernameTextView.setText(username);
            changeColor(payload);
        }

        /**
         * Opens the message activity with the clicked user.
         * 
         * @param clickedUser The username of the clicked user.
         */
        private void openMessageActivity(String clickedUser) {
            Intent intent = new Intent(itemView.getContext(), MessageActivity.class);
            // Pass the clicked chat partner to the message activity
            intent.putExtra("TARGET_PARTNER", clickedUser);
            itemView.getContext().startActivity(intent);
        }

        /**
         * Changes the color of the user item view based on the action.
         * 
         * @param action The action to be performed.
         */
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
