package com.example.chatapp.model;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class ChatMessageModel {

    private String message ;
    private String documentId;
    private String senderId;
    private Timestamp timestamp;
    private boolean isDeleted;
    private String imageUrl; // Add this field to store the image URL
    private String videoUrl;



    public ChatMessageModel(String message, String senderId, Timestamp timestamp, String documentId, Boolean isDeleted, String imageUrl, String videoUrl) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.documentId = documentId;
        this.isDeleted = isDeleted();
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}


