/*  Christian Jumtow
    Matr. Nr: 1166358
 */

package com.example.chitchatapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

public class Message {
    private String message;
    private final UUID id;
    private final String sender;
    private final String receiverId;
    private final boolean isIncoming;
    private final MessageType type;
    private final Date timestamp;
    private Date editTimeStamp;

    private MessageState messageState;

    public Message(String message, String sender, String receiverId, boolean isIncoming, MessageType type) {
        this.message = message;
        this.sender = sender;
        this.receiverId = receiverId;
        this.isIncoming = isIncoming;
        this.type = type;

        long currentTimeMillis = System.currentTimeMillis();
        this.timestamp = new Date(currentTimeMillis);
        this.id = UUID.randomUUID();
        this.messageState = MessageState.UNMODIFIED;
    }

    public Message(String message, UUID id, String sender, String receiverId, boolean isIncoming, MessageType type) {
        this.message = message;
        this.sender = sender;
        this.receiverId = receiverId;
        this.isIncoming = isIncoming;
        this.type = type;
        this.id = id;

        long currentTimeMillis = System.currentTimeMillis();
        this.timestamp = new Date(currentTimeMillis);
        this.messageState = MessageState.UNMODIFIED;
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

    public Date getEditTimeStamp() {
        return editTimeStamp;
    }

    public MessageState getMessageState() {
        return messageState;
    }

    public void setMessage(String text) {
        message = text;
    }

    public void setState(MessageState state) {
        messageState = state;
    }

    public void setEditTimeStamp(Date date) {
        editTimeStamp = date;
    }

    public JSONObject toJSON() {
        JSONObject jsonMessage = new JSONObject();

        try {
            jsonMessage.put("message", message);
            jsonMessage.put("sender", sender);
            jsonMessage.put("targetUserId", receiverId);
            jsonMessage.put("id", id.toString());
            jsonMessage.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonMessage;
    }

}

