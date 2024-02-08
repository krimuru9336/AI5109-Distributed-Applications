package de.hsfulda.WhatsDownBackend.groupchats;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group-chats")
public class GroupChatController {
    /*
     * Jonas Wagner - 1315578
     */
    private final GroupChatService groupChatService;

    public GroupChatController(GroupChatService GroupChatService) {
        this.groupChatService = GroupChatService;
    }

    @PostMapping
    public ResponseEntity<GroupChat> createGroupChat(@RequestBody GroupChatDTO groupChatDTO) {
        GroupChat createdGroupChat = groupChatService.createGroupChat(groupChatDTO);
        return ResponseEntity.ok(createdGroupChat);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<GroupChat>> getGroupsForUser(@PathVariable Long userId) {
        List<GroupChat> groups = groupChatService.getGroupsForUser(userId);
        return ResponseEntity.ok(groups);
    }
}
