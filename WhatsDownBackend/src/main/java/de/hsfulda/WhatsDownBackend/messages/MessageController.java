package de.hsfulda.WhatsDownBackend.messages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/messages")
@Slf4j
public class MessageController {
    /*
     * Jonas Wagner - 1315578
     */
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody MessageDTO message) {
        log.info("Message received: {}", message);
        Message sentMessage = messageService.sendMessage(message);
        if (sentMessage != null) {
            return ResponseEntity.ok(sentMessage);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/retrieve")
    public ResponseEntity<List<Message>> retrieveMessages(@RequestParam Long user1, @RequestParam Long user2, @RequestParam(required = false) String lastFetchedTimestamp) {

        List<Message> messages;
        if (lastFetchedTimestamp != null) {
            LocalDateTime fetchedTimestamp = LocalDateTime.parse(lastFetchedTimestamp);
            messages = messageService.getNewMessages(user1, user2, fetchedTimestamp);
        } else {
            log.info("Retrieving all messages between {} and {}", user1, user2);
            messages = messageService.getEntireChat(user1, user2);
        }
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/edit/{messageId}")
    public ResponseEntity<Message> editMessage(@PathVariable Long messageId, @RequestBody EditMessageDTO updatedMessage) {
        Message editedMessage = messageService.editMessage(messageId, updatedMessage);
        if (editedMessage != null) {
            return ResponseEntity.ok(editedMessage);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/delete/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        boolean isDeleted = messageService.deleteMessage(messageId);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
