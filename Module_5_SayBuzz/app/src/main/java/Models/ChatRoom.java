package Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatRoom {
    private String groupName;

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<ChatRoomUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<ChatRoomUser> users) {
        this.users = users;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private  String key;
    public ArrayList<ChatRoomUser> users = new ArrayList<>();
    public  ArrayList<Message> messages = new ArrayList<>();

    public ChatRoom() {
    }

    public ChatRoom(String username) {
        this.groupName = username;
    }

    public String getGroupName() {
        return groupName;
    }
    public static class  ChatRoomUser{
        String Key;
        String name;
        String uid;

        public  ChatRoomUser(){}

        public ChatRoomUser(String name, String uid) {

            this.name = name;
            this.uid = uid;
        }

        @Override
        public String toString() {
            return "ChatRoomUser{" +
                    "Key='" + Key + '\'' +
                    ", name='" + name + '\'' +
                    ", uid='" + uid + '\'' +
                    '}';
        }

        public String getKey() {
            return Key;
        }

        public void setKey(String key) {
            Key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
    }


}

