// Sven Schickentanz - fdai7287
package com.da.chitchat.singletons;

import com.da.chitchat.services.MediaConverter;

/**
 * The MediaConverterSingleton class represents a singleton instance of the MediaConverter class.
 * It ensures that only one instance of MediaConverter is created and provides a global point of access to it.
 */
public class MediaConverterSingleton {

    private static MediaConverter instance;

    private MediaConverterSingleton() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the singleton instance of MediaConverter.
     * If the instance does not exist, it creates a new instance and returns it.
     *
     * @return The singleton instance of MediaConverter.
     */
    public static synchronized MediaConverter getInstance() {
        if (instance == null) {
            instance = new MediaConverter();
        }
        return instance;
    }
}