package com.da.chitchat.listeners;

import android.os.Handler;
import android.os.Looper;

import com.da.chitchat.activities.ChatOverviewActivity;
import com.da.chitchat.adapters.GroupAdapter;
import com.da.chitchat.interfaces.GroupListener;

import java.util.List;

public class GroupListListener implements GroupListener {
    private final GroupAdapter groupAdapter;

    public GroupListListener(GroupAdapter adapter) {
        this.groupAdapter = adapter;
    }

    @Override
    public void onGroupAdded(String group) {
        runOnUiThread(() -> {
            groupAdapter.addGroup(group);
        });
    }

    @Override
    public void onGroupRemoved(String group) {
        runOnUiThread(() -> {
            groupAdapter.removeGroup(group);
        });
    }

    @Override
    public void onShowToast(String group, ChatOverviewActivity activity) {
        activity.showInvalidGroupName(group, activity);
    }

    private void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }
}