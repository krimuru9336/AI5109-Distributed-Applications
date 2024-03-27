package com.example.whatsdown.model;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessageModel {
    private String message;
    private String senderId;
    private String chatroomID;
    private Timestamp timestamp;

    private String mediaType = "gif";

    public ChatMessageModel() {
    }

    public ChatMessageModel(String message, String senderId, Timestamp timestamp, String chatroomId, String mediaType) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.chatroomID = chatroomId;
        this.mediaType = mediaType;
    }

    public String getChatroomID() {
        return chatroomID;
    }

    public void setChatroomID(String chatroomID) {
        this.chatroomID = chatroomID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestampAsString() {
        Timestamp timestamp = this.getTimestamp();
        Date timestampDate = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(timestampDate);
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
