package de.hsfulda.WhatsDownBackend.messages;

import de.hsfulda.WhatsDownBackend.users.User;
import de.hsfulda.WhatsDownBackend.users.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.scheduling.annotation.Scheduled;

@Service
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final Map<String, Set<Long>> fetchedMessages = new HashMap<>();

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public Message sendMessage(MessageDTO message) {
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
            return null;
        }
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
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
        return false;
    }

    @Scheduled(fixedRate = 300000)
    public void scheduledCleanUp() {
        log.info("Running scheduled clean up");
        fetchedMessages.clear();
    }
}

