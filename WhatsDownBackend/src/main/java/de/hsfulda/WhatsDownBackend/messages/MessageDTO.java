package de.hsfulda.WhatsDownBackend.messages;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MessageDTO {
    /*
     * Jonas Wagner - 1315578
     */
    private Long senderId;
    private Long receiverId;
    private String content;
}
