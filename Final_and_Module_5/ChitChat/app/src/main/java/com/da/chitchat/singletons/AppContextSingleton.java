package com.da.chitchat.singletons;

import android.content.Context;

public class AppContextSingleton {

    private static AppContextSingleton instance;
    private Context applicationContext;

    private AppContextSingleton() {
        // Private constructor to prevent instantiation
    }

    public static synchronized AppContextSingleton getInstance() {
        if (instance == null) {
            instance = new AppContextSingleton();
        }
        return instance;
    }

    public void initialize(Context context) {
        applicationContext = context.getApplicationContext();
    }

    public String getString(int resId) {
        return applicationContext.getResources().getString(resId);
    }
}