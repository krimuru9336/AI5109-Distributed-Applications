package com.da.chitchat.interfaces;

import com.da.chitchat.Message;

public interface MessageListener {
    void onMessageReceived(Message message);
}