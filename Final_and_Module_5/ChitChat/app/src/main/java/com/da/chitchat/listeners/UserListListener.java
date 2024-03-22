package com.da.chitchat.listeners;

import android.os.Handler;
import android.os.Looper;

import com.da.chitchat.adapters.UserAdapter;
import com.da.chitchat.UserMessageStore;
import com.da.chitchat.interfaces.UserListener;

import java.util.List;

public class UserListListener implements UserListener<String> {
    private final UserAdapter userAdapter;

    public UserListListener(UserAdapter adapter) {
        this.userAdapter = adapter;
    }

    @Override
    public void onEvent(String userName, String action) {
        runOnUiThread(() -> {
            switch (action) {
                case ("connected"):
                    userAdapter.addUser(userName);
                    break;
                case ("disconnected"):
                    //UserMessageStore.clearMessagesFromUser(userName);
                    userAdapter.removeUser(userName);
                    break;
            }
        });
    }

    @Override
    public void onEvent(List<String> userList, String action) {
        runOnUiThread(() -> {
            if (userAdapter != null) {
                for (String user : userList) {
                    userAdapter.addUser(user);
                }
            }
        });
    }

    private void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }
}