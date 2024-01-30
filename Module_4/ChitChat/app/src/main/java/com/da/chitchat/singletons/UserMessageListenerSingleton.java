package com.da.chitchat.singletons;

import com.da.chitchat.listeners.UserMessageListener;

public class UserMessageListenerSingleton {

    private static UserMessageListener instance;

    private UserMessageListenerSingleton() {
        // Private constructor to prevent instantiation
    }

    public static synchronized UserMessageListener getInstance() {
        if (instance == null) {
            instance = new UserMessageListener();
        }
        return instance;
    }
}