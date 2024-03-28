// Sven Schickentanz - fdai7287
package com.da.chitchat.listeners;

import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import com.da.chitchat.adapters.UserAdapter;
import com.da.chitchat.interfaces.UserListener;

import java.util.List;

/**
 * This class is an implementation of the UserListener interface and is responsible for handling events related to the user list.
 * It provides methods to add or remove users from the user list based on the received events.
 */
public class UserListListener implements UserListener<String> {
    private final UserAdapter userAdapter;

    /**
     * Constructs a new UserListListener with the specified UserAdapter.
     *
     * @param adapter the UserAdapter to be updated
     */
    public UserListListener(UserAdapter adapter) {
        this.userAdapter = adapter;
    }

    /**
     * Called when a user event occurs (e.g., connected or disconnected).
     *
     * @param userName the name of the user
     * @param action   the action associated with the user event
     */
    @Override
    public void onEvent(String userName, String action) {
        runOnUiThread(() -> {
            switch (action) {
                case ("connected"):
                    userAdapter.addUser(userName);
                    break;
                case ("disconnected"):
                    userAdapter.removeUser(userName);
                    break;
            }
        });
    }

    /**
     * Called when a list of users is received.
     * 
     * @param userList the list of users
     * @param action   the action associated with the user event
     */
    @Override
    public void onEvent(List<Pair<String, Boolean>> userList, String action) {
        runOnUiThread(() -> {
            if (userAdapter != null) {
                for (Pair<String, Boolean> user : userList) {
                    userAdapter.addUser(user.first, user.second);
                }
            }
        });
    }

    /**
     * Runs the specified action on the UI thread.
     * Changes need to be executed on the UI thread to update the UI components.
     *
     * @param action the action to be executed
     */
    private void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }
}