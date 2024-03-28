// Sven Schickentanz - fdai7287
package com.da.chitchat.interfaces;

import android.util.Pair;

import java.util.List;

/**
 * The UserListener interface defines the contract for classes that listen for user events.
 * It provides methods to handle different types of events related to users.
 *
 * @param <T> the type of data associated with the events
 */
public interface UserListener<T> {
    /**
     * Called when a user event occurs.
     *
     * @param data   the data associated with the event
     * @param action the action associated with the event
     */
    void onEvent(T data, T action);

    /**
     * Called when a user event occurs with a list of data and an action.
     *
     * @param data   the list of data associated with the event
     * @param action the action associated with the event
     */
    void onEvent(List<Pair<T, Boolean>> data, T action);
}
