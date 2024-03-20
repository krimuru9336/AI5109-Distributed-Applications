package com.da.chitchat;

import java.util.Date;
import java.util.UUID;

public class Message {
    public enum State {
        UNMODIFIED,
        EDITED,
        DELETED
    }

    private final UUID id;
    private String text;
    private final String sender;
    private final boolean isIncoming;
    private final Date timestamp;
    private Date editTimestamp;
    private State state;

    public Message(String text, String sender, boolean isIncoming) {
        this.id = UUID.randomUUID();
        this.text = text;
        this.sender = sender;
        this.isIncoming = isIncoming;
        this.state = State.UNMODIFIED;

        long currentTimeMillis = System.currentTimeMillis();
        this.timestamp = new Date(currentTimeMillis);
    }

    public Message(String text, String sender, boolean isIncoming, UUID id) {
        this.id = id;
        this.text = text;
        this.sender = sender;
        this.isIncoming = isIncoming;
        this.state = State.UNMODIFIED;

        long currentTimeMillis = System.currentTimeMillis();
        this.timestamp = new Date(currentTimeMillis);
    }

    public void setText(String text) {
        this.text = text;
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

    public void setEditTimestamp(Date timestamp) {
        editTimestamp = timestamp;
    }

    public Date getEditTimestamp() {
        return editTimestamp;
    }

    public UUID getID() {
        return id;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }
}
