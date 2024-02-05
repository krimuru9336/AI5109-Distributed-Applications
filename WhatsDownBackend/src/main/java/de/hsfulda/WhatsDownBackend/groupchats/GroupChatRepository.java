package de.hsfulda.WhatsDownBackend.groupchats;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
}
