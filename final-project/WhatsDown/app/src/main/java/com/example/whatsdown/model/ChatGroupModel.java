package com.example.whatsdown.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatGroupModel implements Parcelable {
    String groupId;
    String  groupName;
    List<String> userIds;
    Timestamp createdTimestamp;
    String lastMessageSenderId;
    ChatMessageModel lastMessage;
    public ChatGroupModel() {
    }

    public ChatGroupModel(String groupId, String groupName, List<String> userIds, Timestamp createdTimestamp, String lastMessageSenderId, ChatMessageModel lastMessage) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.userIds = userIds;
        this.createdTimestamp = createdTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessage = lastMessage;
    }

    public ChatGroupModel(String groupId, String name, List<String> users, Timestamp createdTime) {
        this.groupId = groupId;
        this.groupName = name;
        this.userIds = users;
        this.createdTimestamp = createdTime;
    }
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public ChatMessageModel getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(ChatMessageModel lastMessage) {
        this.lastMessage = lastMessage;
    }
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(groupId);
        dest.writeStringList(userIds);
        dest.writeParcelable(createdTimestamp, flags);
        dest.writeString(lastMessageSenderId);
        dest.writeString(groupName);
        dest.writeParcelable(lastMessage, flags);
    }

    protected ChatGroupModel(Parcel in) {
        groupId = in.readString();
        userIds = in.createStringArrayList();
        createdTimestamp = in.readParcelable(Timestamp.class.getClassLoader());
        lastMessageSenderId = in.readString();
        groupName = in.readString();
        lastMessage = in.readParcelable(ChatMessageModel.class.getClassLoader() );
    }

    public static final Creator<ChatGroupModel> CREATOR = new Creator<ChatGroupModel>() {
        @Override
        public ChatGroupModel createFromParcel(Parcel in) {
            return new ChatGroupModel(in);
        }

        @Override
        public ChatGroupModel[] newArray(int size) {
            return new ChatGroupModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
