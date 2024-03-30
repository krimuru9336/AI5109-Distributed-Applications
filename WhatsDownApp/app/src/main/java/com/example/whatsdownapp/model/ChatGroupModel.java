package com.example.whatsdownapp.model;

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
    String lastMsgSenderId;
    ChatMessageModel lastMsg;


    public ChatGroupModel() {
    }

    public ChatGroupModel(String groupId, String groupName, List<String> userIds, Timestamp createdTimestamp, String lastMsgSenderId, ChatMessageModel lastMsg) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.userIds = userIds;
        this.createdTimestamp = createdTimestamp;
        this.lastMsgSenderId = lastMsgSenderId;
        this.lastMsg = lastMsg;
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

    public String getLastMsgSenderId() {
        return lastMsgSenderId;
    }

    public void setLastMsgSenderId(String lastMsgSenderId) {
        this.lastMsgSenderId = lastMsgSenderId;
    }
    public ChatMessageModel getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(ChatMessageModel lastMsg) {
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

    protected ChatGroupModel(Parcel in) {
        groupId = in.readString();
        userIds = in.createStringArrayList();
        createdTimestamp = in.readParcelable(Timestamp.class.getClassLoader());
        lastMsgSenderId = in.readString();
        groupName = in.readString();
        lastMsg = in.readParcelable(ChatMessageModel.class.getClassLoader() );
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