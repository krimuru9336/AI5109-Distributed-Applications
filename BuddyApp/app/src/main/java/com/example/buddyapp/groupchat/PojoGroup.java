package com.example.buddyapp.groupchat;

import java.io.Serializable;
import java.util.ArrayList;

public class PojoGroup implements Serializable {
    ArrayList<String> memberid;
    String createdby,groupName;

    public PojoGroup() {
    }

    public PojoGroup(ArrayList<String> memberid, String createdby, String groupName) {
        this.memberid = memberid;
        this.createdby = createdby;
        this.groupName = groupName;
    }

    public ArrayList<String> getMemberid() {
        return memberid;
    }

    public void setMemberid(ArrayList<String> memberid) {
        this.memberid = memberid;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
