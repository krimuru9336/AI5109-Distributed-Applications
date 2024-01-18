package de.lorenz.da_exam_project.models;

import com.google.firebase.Timestamp;

public class ChatMessage {

    private String message;
    private String senderId;
    private Timestamp timestamp;

    public ChatMessage() {
        // need empty constructor for firestore
    }

    public ChatMessage(String message, String senderId, Timestamp timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
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
}
