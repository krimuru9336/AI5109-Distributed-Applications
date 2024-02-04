package com.example.module3;

public class Message {
    private String messageId;
    private String message;
    private long timestamp;

    public Message() {
        // Default constructor required for Firebase
    }

    public Message(String messageId, String message, long timestamp) {
        this.messageId = messageId;
        this.message = message;
        this.timestamp = timestamp;
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
