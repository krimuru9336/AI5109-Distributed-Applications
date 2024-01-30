package com.da.chitchat;

import android.content.Context;

public class WebSocketManagerSingleton {

    private static WebSocketManager instance;

    private WebSocketManagerSingleton() {
        // Private constructor to prevent instantiation
    }

    public static synchronized WebSocketManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new WebSocketManager(ctx);
        }
        return instance;
    }
}