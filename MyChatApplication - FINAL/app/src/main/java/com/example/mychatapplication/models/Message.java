package com.example.mychatapplication.models;

import java.util.Date;
import java.util.Objects;

public class Message {
    private String messageId;
    private String senderId;
    private String contentType;
    private String senderName;  // Added field for sender's name
    private String senderProfileImageUrl;  // Added field for sender's profile image URL
    private String content;
    private Date timestamp;

    public Message() {
        // Default constructor required for Firestore
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderProfileImageUrl() {
        return senderProfileImageUrl;
    }

    public void setSenderProfileImageUrl(String senderProfileImageUrl) {
        this.senderProfileImageUrl = senderProfileImageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Message otherMessage = (Message) obj;
        // Compare messages based on their unique identifiers (e.g., message ID or timestamp)
        // Return true if the unique identifiers are equal, indicating that the messages are the same
        return Objects.equals(this.messageId, otherMessage.messageId); // Replace messageId with actual unique identifier
    }
}
