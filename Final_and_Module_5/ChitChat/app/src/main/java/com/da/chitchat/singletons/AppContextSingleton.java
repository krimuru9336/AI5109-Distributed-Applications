// Sven Schickentanz - fdai7287
package com.da.chitchat.singletons;

import android.content.Context;

/**
 * The AppContextSingleton class represents a singleton object that provides access to the application context.
 * It ensures that only one instance of the class is created and provides a global point of access to the application context.
 */
public class AppContextSingleton {

    private static AppContextSingleton instance;
    private Context applicationContext;

    private AppContextSingleton() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the instance of the AppContextSingleton class.
     * If the instance does not exist, a new instance is created.
     *
     * @return The instance of the AppContextSingleton class.
     */
    public static synchronized AppContextSingleton getInstance() {
        if (instance == null) {
            instance = new AppContextSingleton();
        }
        return instance;
    }

    /**
     * Initializes the AppContextSingleton with the application context.
     *
     * @param context The application context.
     */
    public void initialize(Context context) {
        applicationContext = context.getApplicationContext();
    }

    /**
     * Returns the string resource associated with the given resource ID.
     *
     * @param resId The resource ID of the string.
     * @return The string resource.
     */
    public String getString(int resId) {
        return applicationContext.getResources().getString(resId);
    }

    /**
     * Returns the application context.
     *
     * @return The application context.
     */
    public Context getContext() {
        return applicationContext;
    }
}