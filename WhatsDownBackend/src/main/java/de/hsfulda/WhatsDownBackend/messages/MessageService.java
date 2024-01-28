package de.hsfulda.WhatsDownBackend.messages;

import de.hsfulda.WhatsDownBackend.users.User;
import de.hsfulda.WhatsDownBackend.users.UserRepository;
import de.hsfulda.WhatsDownBackend.util.AzureBlobStorageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class MessageService {
    /*
     * Jonas Wagner - 1315578
     */
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final AzureBlobStorageUtil azureBlobStorageUtil;
    private final Map<String, Set<Long>> fetchedMessages = new HashMap<>();
    private final Map<String, String> mappingMediaTypeToContainerName = new HashMap<>();

    public MessageService(MessageRepository messageRepository, UserRepository userRepository, AzureBlobStorageUtil azureBlobStorageUtil) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.azureBlobStorageUtil = azureBlobStorageUtil;

        mappingMediaTypeToContainerName.put("Image", "images");
        mappingMediaTypeToContainerName.put("Video", "videos");
        mappingMediaTypeToContainerName.put("Gif", "gifs");
    }

    public Message sendMessage(MessageDTO message, MultipartFile media) {
        Optional<User> senderOptional = userRepository.findById(message.getSenderId());
        Optional<User> receiverOptional = userRepository.findById(message.getReceiverId());

        if (senderOptional.isEmpty() || receiverOptional.isEmpty()) {
            return null;
        }

        User sender = senderOptional.get();
        User receiver = receiverOptional.get();

        Message newMessage = new Message();
        newMessage.setSender(sender);
        newMessage.setReceiver(receiver);
        newMessage.setContent(message.getContent());
        newMessage.setTimestamp(LocalDateTime.now());
        newMessage.setSenderId(sender.getUserId());
        newMessage.setReceiverId(receiver.getUserId());
        newMessage.setMediaType(message.getMediaType());

        String containerName = mappingMediaTypeToContainerName.get(message.getMediaType());

        if (media != null && !media.isEmpty()) {
            String mediaUrl = azureBlobStorageUtil.uploadMedia(media, containerName);
            newMessage.setMediaUrl(mediaUrl);
        }

        return messageRepository.save(newMessage);
    }

    public List<Message> getEntireChat(Long userId1, Long userId2) {
        Optional<User> user1Optional = userRepository.findById(userId1);
        Optional<User> user2Optional = userRepository.findById(userId2);

        if (user1Optional.isEmpty() || user2Optional.isEmpty()) {
            return Collections.emptyList();
        }

        User user1 = user1Optional.get();
        User user2 = user2Optional.get();

        List<Message> messagesFromUser1ToUser2 = messageRepository.findBySenderAndReceiver(user1, user2);
        List<Message> messagesFromUser2ToUser1 = messageRepository.findBySenderAndReceiver(user2, user1);

        List<Message> entireChat = new ArrayList<>(messagesFromUser1ToUser2);
        entireChat.addAll(messagesFromUser2ToUser1);

        entireChat.sort(Comparator.comparing(Message::getTimestamp));

        entireChat.forEach(message -> {
            if (message.getMediaUrl() != null) {
                String sasToken = azureBlobStorageUtil.generateSasTokenFromUrl(message.getMediaUrl(), mappingMediaTypeToContainerName.get(message.getMediaType()));
                String sasMediaUrl = message.getMediaUrl() + "?" + sasToken;
                message.setMediaUrl(sasMediaUrl);
            }
        });

        log.info("Found {} messages between {} and {}", entireChat.size(), user1, user2);

        return entireChat;
    }

    public List<Message> getNewMessages(Long userId1, Long userId2, LocalDateTime lastFetchedTimestamp) {
        String pairKey = userId1 + "-" + userId2;
        Set<Long> fetchedMessageIds = fetchedMessages.getOrDefault(pairKey, new HashSet<>());

        Optional<User> user1Optional = userRepository.findById(userId1);
        Optional<User> user2Optional = userRepository.findById(userId2);

        if (user1Optional.isEmpty() || user2Optional.isEmpty()) {
            return Collections.emptyList();
        }

        User user1 = user1Optional.get();
        User user2 = user2Optional.get();

        List<Message> newMessages = messageRepository.findNewMessages(user1, user2, lastFetchedTimestamp);

        List<Message> unfetchedMessages = new ArrayList<>(newMessages.stream()
                .filter(message -> !fetchedMessageIds.contains(message.getId()))
                .peek(message -> {
                    if (message.getMediaUrl() != null) {
                        String sasToken = azureBlobStorageUtil.generateSasTokenFromUrl(message.getMediaUrl(), mappingMediaTypeToContainerName.get(message.getMediaType()));
                        String sasMediaUrl = message.getMediaUrl() + "?" + sasToken;
                        message.setMediaUrl(sasMediaUrl);
                    }
                })
                .toList());

        if (!unfetchedMessages.isEmpty()) {
            unfetchedMessages.forEach(message -> fetchedMessageIds.add(message.getId()));
            fetchedMessages.put(pairKey, fetchedMessageIds);
        }

        unfetchedMessages.sort(Comparator.comparing(Message::getTimestamp));

        log.info("Current fetched messages: {}", fetchedMessages);

        log.info("Found {} new messages between {} and {} since {}", unfetchedMessages.size(), user1, user2, lastFetchedTimestamp);

        return unfetchedMessages;
    }

    public Message editMessage(Long messageId, EditMessageDTO updatedMessage) {
        if (updatedMessage.getContent() == null || updatedMessage.getContent().isEmpty()) {
            log.info("Content of updated message is empty");
            return null;
        }
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            log.info("Updating message with id {} with content {}", messageId, updatedMessage.getContent());
            Message message = optionalMessage.get();
            message.setContent(updatedMessage.getContent());
            return messageRepository.save(message);
        }
        return null;
    }

    public boolean deleteMessage(Long messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            messageRepository.deleteById(messageId);
            log.info("Deleted message with id {}", messageId);
            return true;
        }
        log.info("Message with id {} not found", messageId);
        return false;
    }

    @Scheduled(fixedRate = 300000)
    public void scheduledCleanUp() {
        log.info("Running scheduled clean up");
        fetchedMessages.clear();
    }
}

