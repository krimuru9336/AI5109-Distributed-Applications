package de.hsfulda.WhatsDownBackend.groupchats;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/group-chats")
public class GroupChatController {

    private final GroupChatService groupChatService;

    public GroupChatController(GroupChatService GroupChatService) {
        this.groupChatService = GroupChatService;
    }

    @PostMapping
    public ResponseEntity<GroupChat> createGroupChat(@RequestBody GroupChatDTO groupChatDTO) {
        GroupChat createdGroupChat = groupChatService.createGroupChat(groupChatDTO);
        return ResponseEntity.ok(createdGroupChat);
    }
}
