// Sven Schickentanz - fdai7287
package com.da.chitchat.singletons;

import android.annotation.SuppressLint;
import android.content.Context;

import com.da.chitchat.WebSocketManager;

/**
 * The WebSocketManagerSingleton class represents a singleton instance of the WebSocketManager class.
 * It provides a single instance of the WebSocketManager that can be accessed globally.
 */
public class WebSocketManagerSingleton {

    @SuppressLint("StaticFieldLeak")
    private static WebSocketManager instance;

    private WebSocketManagerSingleton() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the instance of the WebSocketManager class.
     * If the instance does not exist, a new instance is created.
     *
     * @param ctx The application context.
     * @return The instance of the WebSocketManager class.
     */
    public static synchronized WebSocketManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new WebSocketManager(ctx);
        }
        return instance;
    }
}