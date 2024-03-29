package com.example.chitchatapp;

import android.content.Context;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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

    public MessageAdapter createAdapter(String username, OnDataChangedListener listener, MessageActivity ctx) {
        this.messageAdapter = new MessageAdapter(MessageStore.getUserMessages(username), username, listener, ctx);
        return this.messageAdapter;
    }

    public MessageAdapter createAdapter(int groupId, String groupName, OnDataChangedListener listener, MessageActivity ctx) {
        this.messageAdapter = new MessageAdapter(MessageStore.getGroupMessages(groupId), groupName, listener, ctx);
        return this.messageAdapter;
    }

    public void onMessageReceived(Message message, String action) {
        runOnUiThread(() -> {
            Log.d("onMessageReceived", message.toJSON().toString());
            String sender = message.getSender();
            String receiver = message.getReceiver();

            switch (action) {
                case ("message"):
                    if(message.getGroupId() != -1) {
                        Log.d("onMessage", "Got GroupChat MSG");
                        MessageStore.addMessageToGroup(message.getGroupId(), message);
                    }
                    else {
                        Log.d("onMessage", "Got Private MSG");
                        MessageStore.addMessageToUser(sender, message);
                    }

                    if (messageAdapter != null) {
                        if (messageAdapter.currentUser().equals(sender) || messageAdapter.currentUser().equals(receiver)) {
                            messageAdapter.showNewMessage();
                        }
                    }
                    break;
                case ("edit"):

                    if (messageAdapter != null) {
                        if (messageAdapter.currentUser().equals(sender) || messageAdapter.currentUser().equals(receiver)) {
                            MessageStore.editMessage(message);
                            messageAdapter.notifyMessageChanged(message);
                        }
                    }

                    break;
                case ("delete"):

                    if (messageAdapter != null) {
                        if (messageAdapter.currentUser().equals(sender) || messageAdapter.currentUser().equals(receiver)) {
                            MessageStore.deleteMessage(message);
                            messageAdapter.notifyMessageChanged(message);
                        }
                    }

                    break;

//                case("group"):
//                    int groupId = message.getGroupId();
//                    if (groupMessageAdapter != null) {
//                        if (groupMessageAdapter.currentGroup().equals(groupId)) {
//                            MessageStore.addMessageToUser(sender, message);
//                            groupMessageAdapter.notifyMessageChanged(message);
//                        }
//                    }
//
//                    break;

            }
        });
    }

    private void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }

}
