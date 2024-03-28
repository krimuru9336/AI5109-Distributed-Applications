// Sven Schickentanz - fdai7287
package com.da.chitchat.interfaces;

import com.da.chitchat.activities.ChatOverviewActivity;

/**
 * The GroupListener interface provides callbacks for group-related events.
 */
public interface GroupListener {
    /**
     * Called when a group is added.
     *
     * @param group The name of the added group.
     */
    void onGroupAdded(String group);

    /**
     * Called when a group is removed.
     *
     * @param group The name of the removed group.
     */
    void onGroupRemoved(String group);

    /**
     * Called to show a toast message related to a group.
     *
     * @param group    The name of the group.
     * @param activity The ChatOverviewActivity instance.
     */
    void onShowToast(String group, ChatOverviewActivity activity);
}
