package com.example.chatapplication;


import java.util.Date;

public class Message {
    private String id;
    private String text;
    private String sender;
    private long timestamp;
    private boolean isCurrentUser;


    public Message() {
    }

    // Constructor
    public Message(String id, String text, String sender) {
        this.text = text;
        this.sender = sender;
        this.timestamp = new Date().getTime();
        this.id = id;
    }

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    // Getters
    public String getId() { return id; }
    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setText(String text) {
        this.text = text;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

