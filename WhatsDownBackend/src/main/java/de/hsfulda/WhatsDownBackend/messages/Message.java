package de.hsfulda.WhatsDownBackend.messages;

import de.hsfulda.WhatsDownBackend.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Getter
@Setter
@ToString
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;
    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;
    @Column(name = "timestamp", columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime timestamp;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User sender;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User receiver;
}
