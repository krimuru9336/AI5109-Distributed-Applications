package de.hsfulda.WhatsDownBackend.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    /*
     * Jonas Wagner - 1315578
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;
    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;
    @Column(name = "media_type", nullable = false)
    private String mediaType;
    @Column(name = "media_url")
    private String mediaUrl;
    @Column(name = "timestamp", columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime timestamp;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User sender;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User receiver;
}

/*
CREATE TABLE message (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    content TEXT NOT NULL,
    media_type VARCHAR(255) NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES user(user_id),
    FOREIGN KEY (receiver_id) REFERENCES user(user_id)
);

ALTER TABLE message
ADD COLUMN media_url VARCHAR(255) NULL;
 */