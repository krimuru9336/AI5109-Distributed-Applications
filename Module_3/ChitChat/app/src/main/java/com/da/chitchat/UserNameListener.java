package com.da.chitchat;

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