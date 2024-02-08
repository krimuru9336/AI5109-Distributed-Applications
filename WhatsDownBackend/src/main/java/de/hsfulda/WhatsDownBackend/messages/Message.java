package de.hsfulda.WhatsDownBackend.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.hsfulda.WhatsDownBackend.groupchats.GroupChat;
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
    @Column(name = "receiver_id")
    private Long receiverId;
    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;
    @Column(name = "media_type", nullable = false)
    private String mediaType;
    @Column(name = "media_url")
    private String mediaUrl;
    @Column(name = "timestamp", columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime timestamp;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_chat_id", referencedColumnName = "id")
    private GroupChat groupChat;
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
    receiver_id INT,
    content TEXT NOT NULL,
    media_type VARCHAR(255) NOT NULL,
    media_url VARCHAR(255),
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    group_chat_id INT,
    FOREIGN KEY (sender_id) REFERENCES user(user_id),
    FOREIGN KEY (receiver_id) REFERENCES user(user_id)
);

CREATE TABLE group_chat (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE group_chat_member (
    group_chat_id INT,
    user_id INT,
    PRIMARY KEY (group_chat_id, user_id),
    FOREIGN KEY (group_chat_id) REFERENCES group_chat(id),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

ALTER TABLE message
ADD CONSTRAINT fk_message_group_chat
FOREIGN KEY (group_chat_id) REFERENCES group_chat(id);

create table user
(
    user_id int auto_increment
        primary key,
    name    varchar(255) null
);
 */