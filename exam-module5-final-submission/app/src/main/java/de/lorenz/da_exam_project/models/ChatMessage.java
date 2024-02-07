package de.lorenz.da_exam_project.models;

public class ChatMessage {

    public enum Type {
        TEXT,
        IMAGE,
        VIDEO
    }

    private String id;
    private String message;
    private String senderId;
    private long timestamp;
    private Type type;

    public ChatMessage() {
        // need empty constructor for firestore
    }

    public ChatMessage(String message, Type type, String senderId, long timestamp) {
        this.message = message;
        this.type = type;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
