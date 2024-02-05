package de.hsfulda.WhatsDownBackend.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.hsfulda.WhatsDownBackend.groupchats.GroupChat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@ToString(exclude = "groupChats")
public class User {
    /*
     * Jonas Wagner - 1315578
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "name")
    private String name;
    @JsonIgnore
    @ManyToMany(mappedBy = "members")
    private Set<GroupChat> groupChats = new HashSet<>();
}
