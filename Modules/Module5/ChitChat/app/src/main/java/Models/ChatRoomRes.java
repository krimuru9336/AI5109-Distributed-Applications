package Models;

import android.telephony.TelephonyCallback;


import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Models.ChatRoom;
import Models.Message;

public  class  ChatRoomRes{
    private String groupName;
    private  String key;


    public ChatRoomRes() {
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<ChatRoom.ChatRoomUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<ChatRoom.ChatRoomUser> users) {
        this.users = users;
    }

    public MessageRes getMessages() {
        return messages;
    }

    public void setMessages(MessageRes messages) {
        this.messages = messages;
    }



    public ArrayList<ChatRoom.ChatRoomUser> users = new ArrayList<>();
    public  MessageRes messages = new MessageRes();

    public ChatRoom  getRoom(){
        ChatRoom room = new ChatRoom();
        room.setGroupName(groupName);
        for (Map.Entry<String,ArrayList<Message>> m:messages.messages.entrySet()
             ) {
            room.setMessages(m.getValue());
        }
        room.setKey(key);
        room.setUsers(users);
        return room;
    }
}

