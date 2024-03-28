// Sven Schickentanz - fdai7287
package com.da.chitchat.interfaces;

import android.net.Uri;

import com.da.chitchat.Message;

import java.util.Date;
import java.util.UUID;

/**
 * The MessageListener interface provides callback methods for receiving and handling messages.
 */
public interface MessageListener {
    /**
     * Called when a new message is received.
     *
     * @param message The received message.
     */
    void onMessageReceived(Message message);

    /**
     * Called when a message is deleted.
     *
     * @param target   The target of the message (e.g., user ID or group ID).
     * @param messageId The ID of the deleted message.
     * @param isGroup  Indicates whether the message is from a group chat.
     */
    void onMessageDelete(String target, UUID messageId, boolean isGroup);

    /**
     * Called when a message is edited.
     *
     * @param target    The target of the message (e.g., user ID or group ID).
     * @param messageId The ID of the edited message.
     * @param newInput  The new content of the message.
     * @param editDate  The date when the message was edited.
     * @param isGroup   Indicates whether the message is from a group chat.
     */
    void onMessageEdit(String target, UUID messageId, String newInput, Date editDate, boolean isGroup);

    /**
     * Called when a media message is received.
     *
     * @param target    The target of the message (e.g., user ID or group ID).
     * @param messageId The ID of the media message.
     * @param mediaUri  The URI of the media file.
     * @param isGroup   Indicates whether the message is from a group chat.
     * @param isVideo   Indicates whether the media is a video.
     */
    void onMediaReceived(String target, UUID messageId, Uri mediaUri, boolean isGroup, boolean isVideo);

    /**
     * Called when the timestamp of a message is received.
     *
     * @param messageId      The ID of the message.
     * @param timestamp      The timestamp of the message.
     * @param isEditTimestamp Indicates whether the timestamp is for an edited message.
     */
    void onTimestampReceived(UUID messageId, long timestamp, boolean isEditTimestamp);
}