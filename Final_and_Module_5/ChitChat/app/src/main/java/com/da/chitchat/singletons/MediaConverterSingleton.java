package com.da.chitchat.singletons;

import android.content.Context;

import com.da.chitchat.services.MediaConverter;

public class MediaConverterSingleton {

    private static MediaConverter instance;

    private MediaConverterSingleton() {
        // Private constructor to prevent instantiation
    }

    public static synchronized MediaConverter getInstance() {
        if (instance == null) {
            instance = new MediaConverter();
        }
        return instance;
    }
}