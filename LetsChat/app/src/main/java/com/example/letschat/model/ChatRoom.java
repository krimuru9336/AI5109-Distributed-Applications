package com.example.letschat.model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatRoom implements Parcelable {

    String chatRoomId;
    List<String> userIds;
    Timestamp lastMsgTimestamp;
    String lastMsgSenderId;
    String lastMsgText;

    ChatMessage lastMsg;

    public ChatRoom() {
    }

    public ChatRoom(String chatRoomId, List<String> userIds, Timestamp lastMsgTimestamp, String lastMsgSenderId) {
        this.chatRoomId = chatRoomId;
        this.userIds = userIds;
        this.lastMsgTimestamp = lastMsgTimestamp;
        this.lastMsgSenderId = lastMsgSenderId;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMsgTimestamp() {
        return lastMsgTimestamp;
    }

    public void setLastMsgTimestamp(Timestamp lastMsgTimestamp) {
        this.lastMsgTimestamp = lastMsgTimestamp;
    }

    public String getLastMsgSenderId() {
        return lastMsgSenderId;
    }

    public void setLastMsgSenderId(String lastMsgSenderId) {
        this.lastMsgSenderId = lastMsgSenderId;
    }

    public String getLastMsgText() {
        return lastMsgText;
    }

    public void setLastMsgText(String lastMsgText) {
        this.lastMsgText = lastMsgText;
    }

    public ChatMessage getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(ChatMessage lastMsg) {
        this.lastMsg = lastMsg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(chatRoomId);
        dest.writeStringList(userIds);
        dest.writeParcelable(lastMsgTimestamp, flags);
        dest.writeString(lastMsgSenderId);
        dest.writeString(lastMsgText);
        dest.writeParcelable(lastMsg, flags);
    }

    // Parcelable creator
    public static final Creator<ChatRoom> CREATOR = new Creator<ChatRoom>() {
        @Override
        public ChatRoom createFromParcel(Parcel in) {
            return new ChatRoom(in);
        }

        @Override
        public ChatRoom[] newArray(int size) {
            return new ChatRoom[size];
        }
    };

    // Parcelable constructor
    protected ChatRoom(Parcel in) {
        chatRoomId = in.readString();
        userIds = in.createStringArrayList();
        lastMsgTimestamp = in.readParcelable(Timestamp.class.getClassLoader());
        lastMsgSenderId = in.readString();
        lastMsgText = in.readString();
        lastMsg = in.readParcelable(ChatMessage.class.getClassLoader() );
    }
}
