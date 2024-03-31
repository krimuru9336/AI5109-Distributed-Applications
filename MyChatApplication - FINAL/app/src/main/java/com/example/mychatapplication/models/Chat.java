package com.example.mychatapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

public class Chat implements Parcelable {
    private String id;
    private String name;
    private String lastMessage;
    private Date timestamp;
    private String image; // User or group image URL
    private List<String> participants; // List of participant IDs
    private String type; // "user" or "group"

    public Chat() {
        // Empty constructor required by Firestore
    }

    protected Chat(Parcel in) {
        id = in.readString();
        name = in.readString();
        lastMessage = in.readString();
        timestamp = new Date(in.readLong());
        image = in.readString();
        participants = in.createStringArrayList();
        type = in.readString();
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getImage() {
        return image;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public String getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(lastMessage);
        parcel.writeLong(timestamp.getTime());
        parcel.writeString(image);
        parcel.writeStringList(participants);
        parcel.writeString(type);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public void setType(String type) {
        this.type = type;
    }
}
