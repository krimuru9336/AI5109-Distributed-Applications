package com.da.chitchat.interfaces;

import android.util.Pair;

import java.util.List;

public interface UserListener<T> {
    void onEvent(T data, T action);
    void onEvent(List<Pair<T, Boolean>> data, T action);
}
