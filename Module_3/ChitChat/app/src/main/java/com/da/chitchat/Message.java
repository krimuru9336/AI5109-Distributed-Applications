package com.da.chitchat;

import java.util.Date;

public class Message {
    private final String text;
    private final String sender;
    private final boolean isIncoming;
    private final Date timestamp;

    public Message(String text, String sender, boolean isIncoming) {
        this.text = text;
        this.sender = sender;
        this.isIncoming = isIncoming;

        long currentTimeMillis = System.currentTimeMillis();
        this.timestamp = new Date(currentTimeMillis);
    }

    public String getText() {
        return text;
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
}
