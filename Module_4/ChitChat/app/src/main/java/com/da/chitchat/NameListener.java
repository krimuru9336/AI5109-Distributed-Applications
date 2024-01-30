package com.da.chitchat;

public interface NameListener<T, U> {
    void onEvent(T data, U action, U name, com.da.chitchat.MainActivity activity);
}
