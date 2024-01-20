package com.example.chitchatapp;

import android.os.Handler;
import android.os.Looper;

public class MessageHelper {
    private MessageAdapter messageAdapter;

    private static MessageHelper instance;

    private MessageHelper() {
    }

    public static synchronized MessageHelper getInstance() {
        if (instance == null) {
            instance = new MessageHelper();
        }
        return instance;
    }

    public MessageAdapter createAdapter(String username, OnDataChangedListener listener) {
        this.messageAdapter = new MessageAdapter(MessageStore.getUserMessages(username), username, listener);
        return this.messageAdapter;
    }

    public void onMessageReceived(Message message, String action) {
        runOnUiThread(() -> {
            switch (action) {
                case ("message"):
                    String sender = message.getSender();
                    MessageStore.addMessageToUser(sender, message);

                    if (messageAdapter != null) {
                        if (messageAdapter.currentUser().equals(sender)) {
                            messageAdapter.showNewMessage();
                        }
                    }
                    break;
                case ("edit"):
                    break;
                case ("delete"):
                    break;
            }
        });
    }

    private void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }

}
