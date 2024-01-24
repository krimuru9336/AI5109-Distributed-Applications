// Message.java
package com.shafi.chatapp;

public class Message {

    private String sender;
    private String content;
    private long timestamp;

    // Constructors, getters, and setters

    // Default constructor required for Firebase
    public Message() {
    }

    // Other constructors

    public Message(String sender, String content, long timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters and setters

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
