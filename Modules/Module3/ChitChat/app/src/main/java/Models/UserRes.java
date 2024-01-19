package Models;

import java.util.ArrayList;

public class UserRes{
    public ArrayList<ChatRoom.ChatRoomUser> users;

    public ArrayList<ChatRoom.ChatRoomUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<ChatRoom.ChatRoomUser> users) {
        this.users = users;
    }

    public UserRes() {
    }
}