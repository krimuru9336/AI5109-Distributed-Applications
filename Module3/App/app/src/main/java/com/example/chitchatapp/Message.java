package com.example.chitchatapp;

import java.util.Date;
import java.util.UUID;

public class Message {
    private final String message;
    private final UUID id;
    private final String sender;
    private final boolean isIncoming;
    private final MessageType type;
    private final Date timestamp;

    public Message(String message, String sender, boolean isIncoming, MessageType type) {
        this.message = message;
        this.sender = sender;
        this.isIncoming = isIncoming;
        this.type = type;

        long currentTimeMillis = System.currentTimeMillis();
        this.timestamp = new Date(currentTimeMillis);
        this.id = UUID.randomUUID();
    }

    public Message(String message, UUID id,  String sender, boolean isIncoming, MessageType type) {
        this.message = message;
        this.sender = sender;
        this.isIncoming = isIncoming;
        this.type = type;
        this.id = id;

        long currentTimeMillis = System.currentTimeMillis();
        this.timestamp = new Date(currentTimeMillis);
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public UUID getId() {
        return id;
    }

    public MessageType getType() {
        return type;
    }

}

