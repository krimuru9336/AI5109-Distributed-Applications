package com.da.chitchat.interfaces;

import com.da.chitchat.Message;

import java.util.Date;
import java.util.UUID;

public interface MessageListener {
    void onMessageReceived(Message message);
    void onMessageDelete(String target, UUID messageId);
    void onMessageEdit(String target, UUID messageId, String newInput, Date editDate);
    void onTimestampReceived(UUID messageId, long timestamp, boolean isEditTimestamp);
}