// Sven Schickentanz - fdai7287
package com.da.chitchat.interfaces;

import android.view.MenuItem;

/**
 * This interface defines the contract for a listener that is notified when data has changed.
 */
public interface OnDataChangedListener {
    /**
     * Called when a context menu item is selected.
     *
     * @param item The selected menu item.
     * @return true if the event is consumed, false otherwise.
     */
    boolean onContextItemSelected(MenuItem item);

    /**
     * Called when the data has changed.
     */
    void onDataChanged();
}
