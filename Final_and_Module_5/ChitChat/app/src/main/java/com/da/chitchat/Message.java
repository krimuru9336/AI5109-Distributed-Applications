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
    private String sender;
    private final boolean isIncoming;
    private Date timestamp;
    private Date editTimestamp;
    private State state;
    private String chatGroup = null;

    public Message(String text, String sender, boolean isIncoming) {
        this.id = UUID.randomUUID();
        this.text = text;
        this.sender = sender;
        this.isIncoming = isIncoming;
        this.state = State.UNMODIFIED;

        long currentTimeMillis = System.currentTimeMillis();
        this.timestamp = new Date(currentTimeMillis);
    }

    public Message(String text, String sender, boolean isIncoming, long timestamp, UUID id) {
        this.id = id;
        this.text = text;
        this.sender = sender;
        this.isIncoming = isIncoming;
        this.state = State.UNMODIFIED;

        this.timestamp = new Date(timestamp);
    }

    public Message(String text, String sender, boolean isIncoming, long timestamp, UUID id,
                   State state, long editTimestamp) {
        this.id = id;
        this.text = text;
        this.sender = sender;
        this.isIncoming = isIncoming;
        this.timestamp = new Date(timestamp);
        this.state = state;
        if (editTimestamp > 0)
            this.editTimestamp = new Date(editTimestamp);
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

    public void setTimestamp(long timestamp) {
        this.timestamp = new Date(timestamp);
    }

    public void setSender(String sender) {
        this.sender = sender;
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

    public boolean isDeleted() {
        return state == State.DELETED;
    }

    public boolean isEdited() {
        return state == State.EDITED;
    }

    public void setChatGroup(String name) {
        chatGroup = name;
    }

    public String getChatGroup() {
        return chatGroup;
    }

    public boolean isGroup() {
        return chatGroup != null;
    }
}
