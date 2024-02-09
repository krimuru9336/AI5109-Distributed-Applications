package de.hsfulda.WhatsDownBackend.messages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
@Slf4j
public class MessageController {
    /*
     * Jonas Wagner - 1315578
     */
    private final MessageService messageService;
    private final Map<String, String> mappingFileTypeToMediaType = new HashMap<>();

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
        mappingFileTypeToMediaType.put("image/jpeg", "Image");
        mappingFileTypeToMediaType.put("image/jpg", "Image");
        mappingFileTypeToMediaType.put("image/png", "Image");
        mappingFileTypeToMediaType.put("image/gif", "Gif");
        mappingFileTypeToMediaType.put("video/gif", "Gif");
        mappingFileTypeToMediaType.put("video/mp4", "Video");
        mappingFileTypeToMediaType.put("video/mov", "Video");
        mappingFileTypeToMediaType.put("video/avi", "Video");
        mappingFileTypeToMediaType.put("video/m4v", "Video");
    }

    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> sendMessage(@RequestParam Long senderId, @RequestParam(required = false) Long receiverId, @RequestParam(required = false) Long groupId, @RequestParam String content, @RequestParam(required = false) MultipartFile media) {
        MessageDTO message = new MessageDTO();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setGroupChatId(groupId);
        message.setContent(content);

        if(media == null) {
            message.setMediaType("Text");
        } else {
            String fileType = media.getContentType();
            message.setMediaType(mappingFileTypeToMediaType.get(fileType));
        }

        log.info("Message received: {}", message);
        Message sentMessage = messageService.sendMessage(message, media);
        if (sentMessage != null) {
            return ResponseEntity.ok(sentMessage);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/retrieve")
    public ResponseEntity<List<Message>> retrieveMessages(@RequestParam(required = false) Long user1, @RequestParam(required = false)  Long user2, @RequestParam(required = false) Long groupId, @RequestParam(required = false) String lastFetchedTimestamp) {
        List<Message> messages;
        if (groupId != null && user2 == null) {
            if (lastFetchedTimestamp == null) {
                messages = messageService.getGroupMessages(groupId);
            } else {
                LocalDateTime fetchedTimestamp = LocalDateTime.parse(lastFetchedTimestamp);
                messages = messageService.getNewGroupMessages(groupId, fetchedTimestamp, user1);
            }
        } else if (user2 != null && groupId == null) {
            if (lastFetchedTimestamp != null) {
                LocalDateTime fetchedTimestamp = LocalDateTime.parse(lastFetchedTimestamp);
                messages = messageService.getNewMessages(user1, user2, fetchedTimestamp);
            } else {
                messages = messageService.getEntireChat(user1, user2);
            }
        } else {
            return ResponseEntity.badRequest().body(null);
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
