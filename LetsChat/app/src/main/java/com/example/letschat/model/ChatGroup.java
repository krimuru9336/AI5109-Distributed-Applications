package com.example.letschat.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatGroup implements Parcelable {

    String groupId;
    String  groupName;
    List<String> userIds;
    Timestamp createdTimestamp;
    String lastMsgSenderId;

    ChatMessage lastMsg;


    public ChatGroup() {
    }

    public ChatGroup(String groupId, String groupName, List<String> userIds, Timestamp createdTimestamp, String lastMsgSenderId, ChatMessage lastMsg) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.userIds = userIds;
        this.createdTimestamp = createdTimestamp;
        this.lastMsgSenderId = lastMsgSenderId;
        this.lastMsg = lastMsg;
    }

    public ChatGroup(String groupId, String name, List<String> users, Timestamp createdTime) {
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

    public String getLastMsgSenderId() {
        return lastMsgSenderId;
    }

    public void setLastMsgSenderId(String lastMsgSenderId) {
        this.lastMsgSenderId = lastMsgSenderId;
    }
    public ChatMessage getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(ChatMessage lastMsg) {
        this.lastMsg = lastMsg;
    }


    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(groupId);
        dest.writeStringList(userIds);
        dest.writeParcelable(createdTimestamp, flags);
        dest.writeString(lastMsgSenderId);
        dest.writeString(groupName);
        dest.writeParcelable(lastMsg, flags);
    }

    protected ChatGroup(Parcel in) {
        groupId = in.readString();
        userIds = in.createStringArrayList();
        createdTimestamp = in.readParcelable(Timestamp.class.getClassLoader());
        lastMsgSenderId = in.readString();
        groupName = in.readString();
        lastMsg = in.readParcelable(ChatMessage.class.getClassLoader() );
    }

    public static final Creator<ChatGroup> CREATOR = new Creator<ChatGroup>() {
        @Override
        public ChatGroup createFromParcel(Parcel in) {
            return new ChatGroup(in);
        }

        @Override
        public ChatGroup[] newArray(int size) {
            return new ChatGroup[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
