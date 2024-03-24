package com.da.chitchat.listeners;

import android.os.Handler;
import android.os.Looper;

import com.da.chitchat.Message;
import com.da.chitchat.adapters.MessageAdapter;
import com.da.chitchat.UserMessageStore;
import com.da.chitchat.interfaces.MessageListener;
import com.da.chitchat.interfaces.OnDataChangedListener;
import com.da.chitchat.database.messages.MessageRepository;
import com.da.chitchat.singletons.AppContextSingleton;

import java.util.Date;
import java.util.UUID;

public class UserMessageListener implements MessageListener {
    private MessageAdapter messageAdapter;
    private final MessageRepository messageDB;

    public UserMessageListener() {
        messageDB = new MessageRepository(AppContextSingleton.getInstance().getContext());
    }

    public MessageAdapter createAdapter(String partnerName, boolean isGroup, OnDataChangedListener listener) {
        if (isGroup) {
            this.messageAdapter = new MessageAdapter(UserMessageStore.getGroupMessages(partnerName),
                    partnerName, listener, true);
        } else {
            this.messageAdapter = new MessageAdapter(UserMessageStore.getUserMessages(partnerName),
                    partnerName, listener, false);
        }
        return this.messageAdapter;
    }

    @Override
    public void onMessageReceived(Message message) {
        runOnUiThread(() -> {
            String sender = message.getSender();
            boolean isGroup = (message.getChatGroup() != null);
            if (isGroup) {
                UserMessageStore.addMessageToGroup(message.getChatGroup(), message);
            } else {
                UserMessageStore.addMessageToUser(sender, message);
            }
            messageDB.addMessage(message, sender);
            if (messageAdapter != null) {
                if (isGroup && messageAdapter.currentUser().equals(message.getChatGroup()) ||
                    !isGroup && messageAdapter.currentUser().equals(sender)) {
                    messageAdapter.showNewMessage();
                }
            }
        });
    }

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

    private void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }
}
