package com.example.chitchatapp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;

public class UserHelper {

    private static UserHelper instance;
    private UserAdapter userAdapter;

    private UserHelper() {
    }

    public static synchronized UserHelper getInstance() {
        if (instance == null) {
            instance = new UserHelper();
        }
        return instance;
    }

    public UserAdapter createAdapter(List<String> userList) {
        this.userAdapter = new UserAdapter(userList);
        return this.userAdapter;
    }

    public void onUser(String userName, String action) {
        Log.d("user", action);
        runOnUiThread(() -> {
            switch (action) {
                case ("connected"):
                    userAdapter.addUser(userName);
                    break;
                case ("disconnected"):
                    MessageStore.clearMessagesFromUser(userName);
                    userAdapter.removeUser(userName);
                    break;
            }
        });
    }

    public void initializeUserList(List<String> userList, String action) {
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
