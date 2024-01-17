package com.da.chitchat;

import android.os.Handler;
import android.os.Looper;

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
