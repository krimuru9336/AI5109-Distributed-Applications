package com.example.chitchatapp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;
import java.util.Map;

public class GroupHelper {

    private static GroupHelper instance;
    private GroupAdapter groupAdapter;

    private GroupHelper() {
    }

    public static synchronized GroupHelper getInstance() {
        if (instance == null) {
            instance = new GroupHelper();
        }
        return instance;
    }

    public GroupAdapter createAdapter(Map<Integer, String> groupMap) {
        this.groupAdapter = new GroupAdapter(groupMap);
        return this.groupAdapter;
    }

    private void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }
}
