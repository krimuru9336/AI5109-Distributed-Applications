package com.da.chitchat.listeners;

import android.os.Handler;
import android.os.Looper;

import com.da.chitchat.Message;
import com.da.chitchat.UserMessageStore;
import com.da.chitchat.adapters.MessageAdapter;
import com.da.chitchat.interfaces.MessageListener;
import com.da.chitchat.interfaces.OnDataChangedListener;

public class UserMessageListener implements MessageListener {
    private MessageAdapter messageAdapter;

    public UserMessageListener() {

    }

    public MessageAdapter createAdapter(String username, OnDataChangedListener listener) {
        this.messageAdapter = new MessageAdapter(UserMessageStore.getUserMessages(username), username, listener);
        return this.messageAdapter;
    }

    @Override
    public void onMessageReceived(Message message) {
        runOnUiThread(() -> {
            String sender = message.getSender();
            UserMessageStore.addMessageToUser(sender, message);
            if (messageAdapter != null) {
                if (messageAdapter.currentUser().equals(sender)) {
                    messageAdapter.showNewMessage();
                }
            }
        });
    }

    private void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }
}
