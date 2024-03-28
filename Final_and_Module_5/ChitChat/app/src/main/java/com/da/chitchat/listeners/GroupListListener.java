// Sven Schickentanz - fdai7287
package com.da.chitchat.listeners;

import android.os.Handler;
import android.os.Looper;

import com.da.chitchat.activities.ChatOverviewActivity;
import com.da.chitchat.adapters.GroupAdapter;
import com.da.chitchat.interfaces.GroupListener;

/**
 * This class implements the GroupListener interface and provides callbacks for group-related events.
 * It is responsible for updating the GroupAdapter based on the received events.
 */
public class GroupListListener implements GroupListener {
    private final GroupAdapter groupAdapter;

    /**
     * Constructs a new GroupListListener with the specified GroupAdapter.
     *
     * @param adapter the GroupAdapter to be updated
     */
    public GroupListListener(GroupAdapter adapter) {
        this.groupAdapter = adapter;
    }

    /**
     * Called when a new group is added.
     *
     * @param group the name of the added group
     */
    @Override
    public void onGroupAdded(String group) {
        runOnUiThread(() -> groupAdapter.addGroup(group));
    }

    /**
     * Called when a group is removed.
     *
     * @param group the name of the removed group
     */
    @Override
    public void onGroupRemoved(String group) {
        runOnUiThread(() -> groupAdapter.removeGroup(group));
    }

    /**
     * Called when an invalid group name is encountered.
     *
     * @param group    the invalid group name
     * @param activity the ChatOverviewActivity instance
     */
    @Override
    public void onShowToast(String group, ChatOverviewActivity activity) {
        activity.showInvalidGroupName(group, activity);
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