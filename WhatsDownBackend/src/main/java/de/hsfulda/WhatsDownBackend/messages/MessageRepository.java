package de.hsfulda.WhatsDownBackend.messages;

import de.hsfulda.WhatsDownBackend.groupchats.GroupChat;
import de.hsfulda.WhatsDownBackend.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    /*
     * Jonas Wagner - 1315578
     */
    List<Message> findBySenderAndReceiver(User sender, User receiver);
    @Query("SELECT m FROM Message m WHERE ((m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1)) AND m.timestamp >= :lastFetchedTimestamp")
    List<Message> findNewMessages(
            @Param("user1") User user1,
            @Param("user2") User user2,
            @Param("lastFetchedTimestamp") LocalDateTime lastFetchedTimestamp
    );

    @Query("SELECT m FROM Message m WHERE m.groupChat = :groupChat")
    List<Message> findGroupMessages(@Param("groupChat") GroupChat groupChat);

    @Query("SELECT m FROM Message m WHERE m.groupChat = :groupChat AND m.timestamp >= :lastFetchedTimestamp")
    List<Message> findGroupNewMessages(@Param("groupChat") GroupChat groupChat, @Param("lastFetchedTimestamp") LocalDateTime lastFetchedTimestamp);
}
