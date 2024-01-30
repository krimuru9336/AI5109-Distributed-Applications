package com.da.chitchat.interfaces;

import com.da.chitchat.activities.MainActivity;

public interface NameListener<T, U> {
    void onEvent(T data, U action, U name, MainActivity activity);
}
