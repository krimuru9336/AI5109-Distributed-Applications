package com.example.letschat.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

public class ChatMessage implements Parcelable {
    private String message;
    private String senderId;
    private Timestamp timestamp;

    private String id;
    private boolean deleted;

    private MessageType messageType;


    public ChatMessage() {
    }

    public ChatMessage(String message, String senderId, Timestamp timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public ChatMessage(String message, String senderId, Timestamp timestamp, String id) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.id = id;
    }

    public ChatMessage(String message, String senderId, Timestamp timestamp, String id, boolean isDeleted) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.id = id;
        this.deleted = isDeleted;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(senderId);
        dest.writeParcelable(timestamp, flags);
        dest.writeString(id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(deleted);
        }
    }

    // Parcelable creator
    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    // Parcelable constructor
    protected ChatMessage(Parcel in) {
        message = in.readString();
        senderId = in.readString();
        timestamp = in.readParcelable(Timestamp.class.getClassLoader());
        id = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deleted = in.readBoolean();
        }
    }

    public enum MessageType {
        TEXT,
        IMAGE,
        VIDEO,
        GIF
    }
}
