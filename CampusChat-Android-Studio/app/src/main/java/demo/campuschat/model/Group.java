package demo.campuschat.model;

import java.util.List;

public class Group {

    private String groupId;
    private String groupName;
    private List<String> memberIds;
    private String groupIcon;

    public Group() {
    }

    public Group(String groupId, String groupName, List<String> memberIds) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.memberIds = memberIds;
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

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }
}
