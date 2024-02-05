package de.hsfulda.WhatsDownBackend.groupchats;

import lombok.Getter;

import java.util.List;

@Getter
public class GroupChatDTO {
    private String name;
    private List<Long> memberIds;
}
