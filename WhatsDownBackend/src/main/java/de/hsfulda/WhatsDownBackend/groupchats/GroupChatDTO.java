package de.hsfulda.WhatsDownBackend.groupchats;

import lombok.Getter;

import java.util.List;

@Getter
public class GroupChatDTO {
    /*
     * Jonas Wagner - 1315578
     */
    private String name;
    private List<Long> memberIds;
}
