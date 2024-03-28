// Sven Schickentanz - fdai7287
package com.da.chitchat.singletons;

import com.da.chitchat.listeners.UserMessageListener;

/**
 * The UserMessageListenerSingleton class represents a singleton object that provides
 * a single instance of the UserMessageListener class.
 */
public class UserMessageListenerSingleton {

    private static UserMessageListener instance;

    private UserMessageListenerSingleton() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the instance of the UserMessageListener class.
     * If the instance does not exist, a new instance is created.
     *
     * @return The instance of the UserMessageListener class.
     */
    public static synchronized UserMessageListener getInstance() {
        if (instance == null) {
            instance = new UserMessageListener();
        }
        return instance;
    }
}