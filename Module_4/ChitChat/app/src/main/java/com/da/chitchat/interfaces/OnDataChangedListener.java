package com.da.chitchat.interfaces;

import android.view.MenuItem;

public interface OnDataChangedListener {
    boolean onContextItemSelected(MenuItem item);

    void onDataChanged();
}
