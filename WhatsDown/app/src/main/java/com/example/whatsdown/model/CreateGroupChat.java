package com.example.whatsdown.model;

import java.util.List;

public class CreateGroupChat {
    /*
     * Jonas Wagner - 1315578
     */
    private String name;
    private List<Integer> memberIds;

    public CreateGroupChat(String name, List<Integer> memberIds) {
        this.name = name;
        this.memberIds = memberIds;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getMemberIds() {
        return memberIds;
    }
}
