package com.da.chitchat;

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