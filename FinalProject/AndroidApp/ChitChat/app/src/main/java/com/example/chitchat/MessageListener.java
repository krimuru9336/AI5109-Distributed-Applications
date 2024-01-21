package com.example.chitchat;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
public class MessageListener {
    private ChatAdapter chatAdapter;
    private static MessageListener ml;

    private MessageListener(){

    }
    public static synchronized MessageListener getInstance(){
        if(ml == null){
            ml = new MessageListener();
        }
        return ml;
    }

    public ChatAdapter createChatAdapter(String username, DataChangedListener dcl){
        this.chatAdapter = new ChatAdapter(MessageStore.getMessages(username),username,dcl);
        return this.chatAdapter;
    }
    public void onMessageReceived(Message msg){
        new Handler(Looper.getMainLooper()).post(() -> {
            String sender = msg.getSendername();
            MessageStore.addMessage(sender, msg);
            if (this.chatAdapter != null) {
                if (this.chatAdapter.currentUsername().equals(sender)) {
                    this.chatAdapter.showNewMessage();
                }
            }
        });
    }
}
