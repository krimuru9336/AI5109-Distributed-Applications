package com.example.chatapplication;

public class Message {
    private String id;
    private String text;
    private long timestamp;
    private boolean isSent;
    private String senderId;
    private String sender;

    // No-argument constructor needed for Firebase
    public Message() {
    }

    // Argument constructor
    public Message(String id, String text, long timestamp, boolean isSent, String senderId) {
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
        this.isSent = isSent;
        this.senderId = senderId;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public boolean isSent() { return isSent; }
    public void setSent(boolean sent) { isSent = sent; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
