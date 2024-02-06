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
            String sender = message.getSender();

            switch (action) {
                case ("message"):
                    MessageStore.addMessageToUser(sender, message);

                    if (messageAdapter != null) {
                        if (messageAdapter.currentUser().equals(sender)) {
                            messageAdapter.showNewMessage();
                        }
                    }
                    break;
                case ("edit"):

                    if (messageAdapter != null) {
                        if (messageAdapter.currentUser().equals(sender)) {
                            MessageStore.editMessageFromUser(message);
                            messageAdapter.notifyMessageChanged(message);
                        }
                    }

                    break;
                case ("delete"):

                    if (messageAdapter != null) {
                        if (messageAdapter.currentUser().equals(sender)) {
                            MessageStore.deleteMessageFromUser(message);
                            messageAdapter.notifyMessageChanged(message);
                        }
                    }

                    break;
            }
        });
    }

    private void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }

}
