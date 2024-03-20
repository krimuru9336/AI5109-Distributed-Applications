package com.da.chitchat.interfaces;

import java.util.List;

public interface UserListener<T> {
    void onEvent(T data, T action);
    void onEvent(List<T> data, T action);
}
