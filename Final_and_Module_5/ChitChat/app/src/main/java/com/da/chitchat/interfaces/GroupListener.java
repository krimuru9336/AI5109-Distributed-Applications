package com.da.chitchat.interfaces;

import com.da.chitchat.activities.ChatOverviewActivity;

import java.util.List;

public interface GroupListener {
    void onGroupAdded(String group);
    void onGroupRemoved(String group);
    void onShowToast(String group, ChatOverviewActivity activity);
}
