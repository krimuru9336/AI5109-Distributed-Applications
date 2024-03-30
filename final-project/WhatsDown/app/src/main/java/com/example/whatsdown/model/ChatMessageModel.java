package com.example.whatsdown.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

public class ChatMessageModel implements Parcelable{
    private String id;
    private String message;
    private String senderId;
    private Timestamp timestamp;
    private boolean isDeleted;
    private MessageType messageType;
    private String senderName;

    public ChatMessageModel() {
    }

    public ChatMessageModel( String message, String senderId, Timestamp timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }
    public ChatMessageModel(String id, String message, String senderId, Timestamp timestamp) {
        this.id = id;
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }
    public ChatMessageModel(String id, String message, String senderId, Timestamp timestamp, boolean isDeleted) {
        this.id = id;
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.isDeleted = isDeleted;
    }

    protected ChatMessageModel(Parcel in) {
        id = in.readString();
        message = in.readString();
        senderId = in.readString();
        senderName = in.readString();
        timestamp = in.readParcelable(Timestamp.class.getClassLoader());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isDeleted = in.readBoolean();
        }
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
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
        dest.writeString(id);
        dest.writeString(message);
        dest.writeString(senderId);
        dest.writeString(senderName);
        dest.writeParcelable(timestamp, flags);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(isDeleted);
        }
    }

    public enum MessageType {
        TEXT,
        IMAGE,
        VIDEO,
        GIF
    }
}
