package com.example.chitchat;

import android.os.Handler;
import android.os.Looper;

import java.util.UUID;

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

    public void onMessageEdit(String chatDest, UUID msgID, String newContent, long newTimestamp) {

        new Handler(Looper.getMainLooper()).post(() -> {
            if (chatAdapter != null && chatAdapter.currentUsername().equals(chatDest)) {
                chatAdapter.editMsg(msgID, newContent, newTimestamp);
            } else {
                MessageStore.editMsg(chatDest, msgID, newContent, newTimestamp);
            }
        });
    }

    public void onMessageDelete(String chatDest, UUID msgID, long newTimestamp) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if(this.chatAdapter != null && chatAdapter.currentUsername().equals(chatDest)){
                this.chatAdapter.deleteMsg(msgID,newTimestamp);
            }
            else{
                MessageStore.deleteMsg(chatDest,msgID,newTimestamp);
            }
        });
    }
}
