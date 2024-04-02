package com.example.myapplication5;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class ChatMessageModel {
    private String message;
    private String senderId;
    private Timestamp timestamp;

    String messageId;

    public ChatMessageModel() {
    }
//messageid
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public ChatMessageModel(String message, String senderId, Timestamp timestamp, String messageId) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.messageId = messageId;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("senderId", senderId);
        map.put("timestamp", timestamp);
        map.put("messageId", messageId);


        return map;
    }
    //messageid
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
