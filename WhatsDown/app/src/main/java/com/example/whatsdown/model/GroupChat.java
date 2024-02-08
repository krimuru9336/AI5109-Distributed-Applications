package com.example.whatsdown.model;

import java.io.Serializable;
import java.util.List;

public class GroupChat implements Serializable {
    /*
     * Jonas Wagner - 1315578
     */
    private int id;
    private String name;
    private List<User> users;

    public GroupChat(int id, String name, List<User> users) {
        this.id = id;
        this.name = name;
        this.users = users;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<User> getUsers() {
        return users;
    }
}
