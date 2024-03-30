package com.example.whatsdownapp.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

public class ChatMessageModel implements Parcelable {
    private String message;
    private String senderId;
    private Timestamp timestamp;
    private String id;
    private boolean deleted;
    private MessageType messageType;
    private String senderName;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String message, String senderId, Timestamp timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public ChatMessageModel(String message, String senderId, Timestamp timestamp, String id) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.id = id;
    }

    public ChatMessageModel(String message, String senderId, Timestamp timestamp, String id, boolean isDeleted) {
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

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(senderId);
        dest.writeString(senderName);
        dest.writeParcelable(timestamp, flags);
        dest.writeString(id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(deleted);
        }
    }

    // Parcelable creator
    public static final Creator<ChatMessageModel> CREATOR = new Creator<ChatMessageModel>() {
        @Override
        public ChatMessageModel createFromParcel(Parcel in) {
            return new ChatMessageModel(in);
        }

        @Override
        public ChatMessageModel[] newArray(int size) {
            return new ChatMessageModel[size];
        }
    };

    protected ChatMessageModel(Parcel in) {
        message = in.readString();
        senderId = in.readString();
        senderName = in.readString();
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
