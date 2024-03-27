package com.example.module5.model;

public class Message {

    private String messageId;
    private String senderId;
    private String type;
    private String message;
    private long timestamp;

    public Message() {
        // Default constructor required for Firebase
    }

    public Message(String messageId,String senderId, String message,String type, long timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getType() {
        return type;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
