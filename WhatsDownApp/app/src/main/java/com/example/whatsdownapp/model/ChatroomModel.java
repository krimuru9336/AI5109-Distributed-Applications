package com.example.whatsdownapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatroomModel implements Parcelable {
    String chatroomId;
    List<String> userIds;
    Timestamp lastMessageTimestamp;
    String lastMessageSenderId;
    String lastMessageText;
    ChatMessageModel lastMessage;

    public ChatroomModel() {
    }

    public ChatroomModel(String chatroomId, List<String> userIds, Timestamp lastMessageTimestamp, String lastMessageSenderId) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getlastMessageText() {
        return lastMessageText;
    }

    public void setlastMessageText(String lastMessageText) {
        this.lastMessageText = lastMessageText;
    }

    public ChatMessageModel getlastMessage() {
        return lastMessage;
    }

    public void setlastMessage(ChatMessageModel lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(chatroomId);
        dest.writeStringList(userIds);
        dest.writeParcelable(lastMessageTimestamp, flags);
        dest.writeString(lastMessageSenderId);
        dest.writeString(lastMessageText);
        dest.writeParcelable(lastMessage, flags);
    }

    // Parcelable creator
    public static final Creator<ChatroomModel> CREATOR = new Creator<ChatroomModel>() {
        @Override
        public ChatroomModel createFromParcel(Parcel in) {
            return new ChatroomModel(in);
        }

        @Override
        public ChatroomModel[] newArray(int size) {
            return new ChatroomModel[size];
        }
    };

    // Parcelable constructor
    protected ChatroomModel(Parcel in) {
        chatroomId = in.readString();
        userIds = in.createStringArrayList();
        lastMessageTimestamp = in.readParcelable(Timestamp.class.getClassLoader());
        lastMessageSenderId = in.readString();
        lastMessageText = in.readString();
        lastMessage = in.readParcelable(ChatMessageModel.class.getClassLoader() );
    }
}