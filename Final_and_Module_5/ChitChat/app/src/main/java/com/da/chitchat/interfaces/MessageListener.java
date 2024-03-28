package com.da.chitchat.interfaces;

import android.net.Uri;

import com.da.chitchat.Message;

import java.util.Date;
import java.util.UUID;

public interface MessageListener {
    void onMessageReceived(Message message);
    void onMessageDelete(String target, UUID messageId, boolean isGroup);
    void onMessageEdit(String target, UUID messageId, String newInput, Date editDate, boolean isGroup);
    void onMediaReceived(String target, UUID messageId, Uri mediaUri, boolean isGroup);
    void onTimestampReceived(UUID messageId, long timestamp, boolean isEditTimestamp);
}