package de.hsfulda.WhatsDownBackend.groupchats;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
    /*
     * Jonas Wagner - 1315578
     */
    List<GroupChat> findByMembers_userId(Long userId);
}
