// Sven Schickentanz - fdai7287
package com.da.chitchat.listeners;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.da.chitchat.Message;
import com.da.chitchat.activities.MessageActivity;
import com.da.chitchat.adapters.MessageAdapter;
import com.da.chitchat.UserMessageStore;
import com.da.chitchat.interfaces.MessageListener;
import com.da.chitchat.interfaces.OnDataChangedListener;
import com.da.chitchat.database.messages.MessageRepository;
import com.da.chitchat.singletons.AppContextSingleton;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * This class implements the MessageListener interface and serves as a listener for user messages.
 * It provides methods to handle received messages, message deletion, message editing, media reception,
 * and timestamp reception. It also provides a method to create a message adapter for displaying messages.
 */
public class UserMessageListener implements MessageListener {
    private MessageAdapter messageAdapter;
    private final MessageRepository messageDB;

    /**
     * Constructs a new UserMessageListener.
     */
    public UserMessageListener() {
        messageDB = new MessageRepository(AppContextSingleton.getInstance().getContext());
    }

    /**
     * Creates a message adapter for displaying messages.
     *
     * @param partnerName the name of the partner user or group
     * @param isGroup     indicates whether the partner is a group
     * @param listener    the listener for data changes
     * @param ctx         the MessageActivity context
     * @return the created message adapter
     */
    public MessageAdapter createAdapter(String partnerName, boolean isGroup,
                                        OnDataChangedListener listener, MessageActivity ctx) {
        List<Message> messages;
        if (isGroup) {
            messages = UserMessageStore.getGroupMessages(partnerName);
        } else {
            messages = UserMessageStore.getUserMessages(partnerName);
        }
        this.messageAdapter = new MessageAdapter(messages, partnerName, listener, ctx);
        return this.messageAdapter;
    }

    /**
     * Called when a new message is received.
     * 
     * @param message The received message.
     */
    @Override
    public void onMessageReceived(Message message) {
        runOnUiThread(() -> {
            String sender = message.getSender();
            boolean isGroup = (message.getChatGroup() != null);
            // Add message to UserMessageStore
            if (isGroup) {
                UserMessageStore.addMessageToGroup(message.getChatGroup(), message);
            } else {
                UserMessageStore.addMessageToUser(sender, message);
            }
            // Add message to database
            messageDB.addMessage(message, sender);
            if (messageAdapter != null) {
                // Show new message only if the current user is the sender or receiver
                if (isGroup && messageAdapter.currentUser().equals(message.getChatGroup()) ||
                    !isGroup && messageAdapter.currentUser().equals(sender)) {
                    messageAdapter.showNewMessage();
                }
            }
        });
    }

    /**
     * Called when a message is deleted.
     *
     * @param target   The target of the message (e.g., user ID or group ID).
     * @param messageId The ID of the deleted message.
     * @param isGroup  Indicates whether the message is from a group chat.
     */
    @Override
    public void onMessageDelete(String target, UUID messageId, boolean isGroup) {
        runOnUiThread(() -> {
            messageDB.deleteMessage(messageId);
            if (messageAdapter != null && messageAdapter.currentUser().equals(target)) {
                messageAdapter.deleteMessage(messageId);
            } else {
                if (isGroup) {
                    UserMessageStore.deleteMessageFromGroup(target, messageId);
                } else {
                    UserMessageStore.deleteMessageFromUser(target, messageId);
                }
            }
        });
    }

    /**
     * Called when a message is edited.
     *
     * @param target    The target of the message (e.g., user ID or group ID).
     * @param messageId The ID of the edited message.
     * @param newInput  The new content of the message.
     * @param editDate  The date when the message was edited.
     * @param isGroup   Indicates whether the message is from a group chat.
     */
    @Override
    public void onMessageEdit(String target, UUID messageId, String newInput, Date editDate, boolean isGroup) {
        runOnUiThread(() -> {
            messageDB.editMessage(messageId, newInput, editDate);
            if (messageAdapter != null && messageAdapter.currentUser().equals(target)) {
                messageAdapter.editMessage(messageId, newInput, editDate);
            } else {
                if (isGroup) {
                    UserMessageStore.editMessageFromGroup(target, messageId, newInput, editDate);
                } else {
                    UserMessageStore.editMessageFromUser(target, messageId, newInput, editDate);
                }
            }
        });
    }

    /**
     * Called when a media message is received.
     *
     * @param target    The target of the message (e.g., user ID or group ID).
     * @param messageId The ID of the media message.
     * @param mediaUri  The URI of the media file.
     * @param isGroup   Indicates whether the message is from a group chat.
     * @param isVideo   Indicates whether the media is a video.
     */
    @Override
    public void onMediaReceived(String target, UUID messageId, Uri mediaUri, boolean isGroup, boolean isVideo) {
        runOnUiThread(() -> {
            try {
                messageDB.updateMedia(messageId, mediaUri, isVideo);
                if (messageAdapter != null) {
                    messageAdapter.addMedia(messageId, mediaUri, isVideo);
                } else {
                    if (isGroup) {
                        UserMessageStore.editMediaFromGroup(target, messageId, mediaUri, isVideo);
                    } else {
                        UserMessageStore.editMediaFromUser(target, messageId, mediaUri, isVideo);
                    }
                }
            } catch (Exception e) {
                // Ignore - no message for media found
            }
        });
    }

    /**
     * Called when the timestamp of a message is received.
     *
     * @param messageId      The ID of the message.
     * @param timestamp      The timestamp of the message.
     * @param isEditTimestamp Indicates whether the timestamp is for an edited message.
     */
    @Override
    public void onTimestampReceived(UUID messageId, long timestamp, boolean isEditTimestamp) {
        runOnUiThread(() -> {
            if (messageAdapter != null) {
                messageAdapter.addTimestamp(messageId, timestamp, isEditTimestamp);
                if (!isEditTimestamp) {
                    messageDB.updateTimestamp(messageId, timestamp);
                }
            }
        });
    }

    /**
     * Runs the specified action on the UI thread.
     * Changes need to be executed on the UI thread to update the UI components.
     *
     * @param action the action to be executed
     */
    private void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }
}
