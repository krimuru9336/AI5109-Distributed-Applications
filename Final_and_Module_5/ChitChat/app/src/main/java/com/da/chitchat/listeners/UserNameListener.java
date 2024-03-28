// Sven Schickentanz - fdai7287
package com.da.chitchat.listeners;

import com.da.chitchat.activities.MainActivity;
import com.da.chitchat.interfaces.NameListener;

/**
 * This class is an implementation of the NameListener interface.
 * It listens for events related to user names and performs actions based on the event data.
 */
public class UserNameListener implements NameListener<Boolean, String> {

    /**
     * Constructs a new UserNameListener object.
     */
    public UserNameListener() {

    }

    /**
     * Called when an event related to user names occurs.
     * Performs actions based on the event data.
     *
     * @param data     The data associated with the event.
     * @param action   The action associated with the event.
     * @param name     The name associated with the event.
     * @param activity The MainActivity instance associated with the event.
     */
    @Override
    public void onEvent(Boolean data, String action, String name, MainActivity activity) {
        if (!data) {
            activity.nextActivity(name);
        } else {
            activity.showInvalidUsernameToast(name, activity);
        }
    }
}