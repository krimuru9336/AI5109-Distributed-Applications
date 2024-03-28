// Sven Schickentanz - fdai7287
package com.da.chitchat;

import android.net.Uri;

import java.util.Date;
import java.util.UUID;

/**
 * Represents a message object.
 */
public class Message {
    /**
     * Represents the state of a message.
     */
    public enum State {
        UNMODIFIED,
        EDITED,
        DELETED
    }

    private final UUID id;
    private String text;
    private final String sender;
    private final boolean isIncoming;
    private Date timestamp;
    private Date editTimestamp;
    private State state;
    private String chatGroup = null;
    private Uri mediaUri = null;
    private boolean isVideo = false;

    /**
     * Constructs a new Message object with the given text, sender, and incoming status.
     *
     * @param text       The text content of the message.
     * @param sender     The sender of the message.
     * @param isIncoming Indicates whether the message is incoming or outgoing.
     */
    public Message(String text, String sender, boolean isIncoming) {
        this.id = UUID.randomUUID();
        this.text = text;
        this.sender = sender;
        this.isIncoming = isIncoming;
        this.state = State.UNMODIFIED;

        long currentTimeMillis = System.currentTimeMillis();
        this.timestamp = new Date(currentTimeMillis);
    }

    /**
     * Constructs a new Message object with the given text, sender, incoming status, timestamp, and ID.
     *
     * @param text       The text content of the message.
     * @param sender     The sender of the message.
     * @param isIncoming Indicates whether the message is incoming or outgoing.
     * @param timestamp  The timestamp of the message.
     * @param id         The ID of the message.
     */
    public Message(String text, String sender, boolean isIncoming, long timestamp, UUID id) {
        this.id = id;
        this.text = text;
        this.sender = sender;
        this.isIncoming = isIncoming;
        this.state = State.UNMODIFIED;

        this.timestamp = new Date(timestamp);
    }

    /**
     * Constructs a new Message object with the given text, sender, incoming status, timestamp, ID, state, and edit timestamp.
     *
     * @param text          The text content of the message.
     * @param sender        The sender of the message.
     * @param isIncoming    Indicates whether the message is incoming or outgoing.
     * @param timestamp     The timestamp of the message.
     * @param id            The ID of the message.
     * @param state         The state of the message.
     * @param editTimestamp The edit timestamp of the message.
     */
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

    /**
     * Sets the text content of the message.
     *
     * @param text The text content of the message.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the text content of the message.
     *
     * @return The text content of the message.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the sender of the message.
     *
     * @return The sender of the message.
     */
    public String getSender() {
        return sender;
    }

    /**
     * Returns whether the message is incoming or outgoing.
     *
     * @return true if the message is incoming, false if it is outgoing.
     */
    public boolean isIncoming() {
        return isIncoming;
    }

    /**
     * Returns the timestamp of the message.
     *
     * @return The timestamp of the message.
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the message.
     *
     * @param timestamp The timestamp of the message.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = new Date(timestamp);
    }

    /**
     * Sets the edit timestamp of the message.
     *
     * @param timestamp The edit timestamp of the message.
     */
    public void setEditTimestamp(Date timestamp) {
        editTimestamp = timestamp;
    }

    /**
     * Returns the edit timestamp of the message.
     *
     * @return The edit timestamp of the message.
     */
    public Date getEditTimestamp() {
        return editTimestamp;
    }

    /**
     * Returns the ID of the message.
     *
     * @return The ID of the message.
     */
    public UUID getID() {
        return id;
    }

    /**
     * Sets the state of the message.
     *
     * @param state The state of the message.
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Returns the state of the message.
     *
     * @return The state of the message.
     */
    public State getState() {
        return state;
    }

    /**
     * Returns whether the message is deleted.
     *
     * @return true if the message is deleted, false otherwise.
     */
    public boolean isDeleted() {
        return state == State.DELETED;
    }

    /**
     * Sets the chat group of the message.
     *
     * @param name The name of the chat group.
     */
    public void setChatGroup(String name) {
        chatGroup = name;
    }

    /**
     * Returns the chat group of the message.
     *
     * @return The chat group of the message.
     */
    public String getChatGroup() {
        return chatGroup;
    }

    /**
     * Returns whether the message is part of a group chat.
     *
     * @return true if the message is part of a group chat, false otherwise.
     */
    public boolean isGroup() {
        return chatGroup != null;
    }

    /**
     * Sets the media URI of the message.
     *
     * @param uri The media URI of the message.
     */
    public void setMediaUri(Uri uri) {
        mediaUri = uri;
    }

    /**
     * Returns the media URI of the message.
     *
     * @return The media URI of the message.
     */
    public Uri getMediaUri() {
        return mediaUri;
    }

    /**
     * Sets whether the message is a video.
     *
     * @param val true if the message is a video, false otherwise.
     */
    public void setIsVideo(boolean val) {
        isVideo = val;
    }

    /**
     * Returns whether the message is a video.
     *
     * @return true if the message is a video, false otherwise.
     */
    public boolean isVideo() {
        return isVideo;
    }
}
