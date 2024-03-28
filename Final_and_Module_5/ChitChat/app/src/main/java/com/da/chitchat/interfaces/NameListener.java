// Sven Schickentanz - fdai7287
package com.da.chitchat.interfaces;

import com.da.chitchat.activities.MainActivity;

/**
 * This interface represents a listener for name events.
 * It provides a callback method that will be called when a name event occurs.
 *
 * @param <T> the type of the data associated with the event
 * @param <U> the type of the action associated with the event
 */
public interface NameListener<T, U> {
    /**
     * Called when a name event occurs.
     *
     * @param data     the data associated with the event
     * @param action   the action associated with the event
     * @param name     the name associated with the event
     * @param activity the MainActivity instance associated with the event
     */
    void onEvent(T data, U action, U name, MainActivity activity);
}
