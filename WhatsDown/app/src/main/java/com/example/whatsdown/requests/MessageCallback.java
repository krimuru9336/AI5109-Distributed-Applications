package com.example.whatsdown.requests;

import com.example.whatsdown.model.ChatMessage;

import java.util.List;

public interface MessageCallback {
    /*
     * Jonas Wagner - 1315578
     */
    void onMessagesReceived(List<ChatMessage> messages);
    void onFailure(Throwable t);
}
