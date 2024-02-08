package de.hsfulda.WhatsDownBackend.groupchats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.hsfulda.WhatsDownBackend.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "group_chat")
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class GroupChat {
    /*
     * Jonas Wagner - 1315578
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @JsonIgnoreProperties("groupChats")
    @ManyToMany
    @JoinTable(
            name = "group_chat_member",
            joinColumns = @JoinColumn(name = "group_chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();
}
