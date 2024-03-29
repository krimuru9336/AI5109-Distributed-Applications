/*  Christian Jumtow
    Matr. Nr: 1166358
 */

package com.example.chitchatapp;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

public class Message {
    private String message;
    private final UUID id;
    private final String sender;
    private final String receiver;
    private final boolean isIncoming;
    private final MessageType type;
    private final Date timestamp;
    private Date editTimeStamp;
    private int groupId = -1;
    private Uri mediaUri = null;
    private String mimeType = "";

    private MessageState messageState;

    public Message(String message, String sender, String receiver, boolean isIncoming, MessageType type) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.isIncoming = isIncoming;
        this.type = type;

        long currentTimeMillis = System.currentTimeMillis();
        this.timestamp = new Date(currentTimeMillis);
        this.id = UUID.randomUUID();
        this.messageState = MessageState.UNMODIFIED;
    }

    public Message(String message, UUID id, String sender, String receiver, boolean isIncoming, MessageType type, int groupId, Uri mediaUri, String fileType) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.isIncoming = isIncoming;
        this.type = type;
        this.id = id;
        this.groupId = groupId;
        this.mediaUri = mediaUri;
        this.mimeType = fileType;

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

    public String getReceiver() {
        return receiver;
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

    public Uri getMediaUri() {
        return mediaUri;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getMimeType() {
        return mimeType;
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

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setMediaUri(Uri mediaUri) {
        this.mediaUri = mediaUri;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public JSONObject toJSON() {
        JSONObject jsonMessage = new JSONObject();

        try {
            jsonMessage.put("message", message);
            jsonMessage.put("sender", sender);
            jsonMessage.put("receiver", receiver);
            jsonMessage.put("id", id.toString());
            jsonMessage.put("type", type);
            jsonMessage.put("groupId", groupId);
            jsonMessage.put("fileType", mimeType);
            jsonMessage.put("mediaUri", mediaUri == null ? null : mediaUri.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonMessage;
    }

    public static Message fromJSON(JSONObject msg) {
        try {
            String message = msg.getString("message");
            UUID id = UUID.fromString(msg.getString("id"));
            String sender =  msg.getString("sender");
            String receiver = msg.getString("receiver");
            MessageType type = MessageType.valueOf(msg.getString("type"));
            int groupId = msg.getInt("groupId");
            String fileType = msg.getString("fileType");
            Uri mediaUri = null;
            try {
                mediaUri = Uri.parse(msg.getString("mediaUri"));
            }catch (JSONException ignored) {}

            return new Message(message, id, sender, receiver, true, type, groupId, mediaUri, fileType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}

