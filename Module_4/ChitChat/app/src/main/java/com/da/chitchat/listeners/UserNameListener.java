package com.da.chitchat.listeners;

import com.da.chitchat.activities.MainActivity;
import com.da.chitchat.interfaces.NameListener;

public class UserNameListener implements NameListener<Boolean, String> {

    public UserNameListener() {

    }

    @Override
    public void onEvent(Boolean data, String action, String name, MainActivity activity) {
        if (!data) {
            activity.nextActivity(name);
        } else {
            activity.showInvalidUsernameToast(name, activity);
        }
    }
}