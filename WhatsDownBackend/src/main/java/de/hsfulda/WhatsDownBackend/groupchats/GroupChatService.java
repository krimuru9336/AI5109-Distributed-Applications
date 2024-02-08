package de.hsfulda.WhatsDownBackend.groupchats;

import de.hsfulda.WhatsDownBackend.users.User;
import de.hsfulda.WhatsDownBackend.users.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GroupChatService {
    /*
     * Jonas Wagner - 1315578
     */
    private final GroupChatRepository groupChatRepository;
    private final UserRepository userRepository;

    public GroupChatService(GroupChatRepository groupChatRepository, UserRepository userRepository) {
        this.groupChatRepository = groupChatRepository;
        this.userRepository = userRepository;
    }

    public GroupChat createGroupChat(GroupChatDTO groupChatDTO) {
        String name = groupChatDTO.getName();
        List<Long> memberIds = groupChatDTO.getMemberIds();
        GroupChat groupChat = new GroupChat();
        groupChat.setName(name);

        Set<User> members = new HashSet<>(userRepository.findAllById(memberIds));
        groupChat.setMembers(members);

        GroupChat createdGroupChat = groupChatRepository.save(groupChat);

        members.forEach(user -> user.getGroupChats().add(createdGroupChat));
        userRepository.saveAll(members);

        return createdGroupChat;
    }

    public List<GroupChat> getGroupsForUser(Long userId) {
        return groupChatRepository.findByMembers_userId(userId);
    }
}
