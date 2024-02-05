package com.example.chatapp.model;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class ChatMessageModel {
    private String message ;
    private String documentId;
    private String senderId;
    private Timestamp timestamp;
    private boolean isDeleted;


    public ChatMessageModel(String message, String senderId, Timestamp timestamp, String documentId, Boolean isDeleted) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.documentId = documentId;
        this.isDeleted = isDeleted();
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public ChatMessageModel() {
    }
    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}


