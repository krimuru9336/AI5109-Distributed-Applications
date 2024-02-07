package de.lorenz.da_exam_project.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.lorenz.da_exam_project.ChatRoomActivity;
import de.lorenz.da_exam_project.models.ChatMessage;
import de.lorenz.da_exam_project.models.ChatRoom;

public class ChatRoomUtil {

    /**
     * Sends a message in the chatroom (also media).
     */
    public static void sendMessage(Context context, ChatRoom chatRoom, String message, ChatMessage.Type type, String senderId, TextView messageInput) {
        long currentTimestamp = System.currentTimeMillis();

        // prepare message
        message = message.trim();

        // send message by adding it to the chat room messages
        ChatMessage chatMessage = new ChatMessage(message, type, senderId, currentTimestamp);

        String finalMessage = message;
        FirebaseUtil.getChatRoomMessagesReference(chatRoom.getId()).add(chatMessage).addOnCompleteListener(task -> {

            // check if message was sent successfully
            if (!task.isSuccessful()) {
                AndroidUtil.showToast(context, "Failed to send message!");
                return;
            }

            // get id of the sent message
            String firebaseMessageId = task.getResult().getId();

            // format message
            String formattedMessage = finalMessage;

            // get message excerpt
            int maxStringLength = 25;
            String messageExcerpt = formattedMessage.length() > maxStringLength ? formattedMessage.substring(0, maxStringLength) + "..." : formattedMessage;

            // special case for media
            if (type == ChatMessage.Type.IMAGE || type == ChatMessage.Type.VIDEO) {
                messageExcerpt = "*" + type.toString().toLowerCase() + "*";
            }

            // update chat room
            chatRoom.setLastMessageSenderId(senderId);
            chatRoom.setLastMessageTimestamp(currentTimestamp);
            chatRoom.setLastMessage(messageExcerpt);
            chatRoom.setLastMessageId(firebaseMessageId);
            FirebaseUtil.getChatRoomReference(chatRoom.getId()).set(chatRoom);

            // update chat message id and save it to the database (again)
            chatMessage.setId(firebaseMessageId);
            FirebaseUtil.getChatRoomMessagesReference(chatRoom.getId()).document(firebaseMessageId).set(chatMessage);

            // clear input field
            if (messageInput != null) {
                messageInput.setText("");
            }
        });
    }


    /**
     * This function uploads an image to the firebase storage.
     */
    public static void uploadMedia(Context context, ChatRoom chatRoom, Uri selectedMediaUri, ChatMessage.Type type) {
        String filePath = getFirebaseFilePath(type);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference(filePath);

        storageRef.putFile(selectedMediaUri).addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();

            // send message
            ChatRoomUtil.sendMessage(context, chatRoom, downloadUrl, type, FirebaseUtil.getCurrentUserId(), null);

        })).addOnFailureListener(e -> AndroidUtil.showToast(context, "Media upload failed"));
    }

    /**
     * Generates a unique id.
     */
    private static String getRandomUid() {
        return "_" + (int) (Math.random() * 1000000);
    }

    private static String getFirebaseFilePath(ChatMessage.Type type) {
        long timestamp = System.currentTimeMillis();
        String userId = FirebaseUtil.getCurrentUserId();
        String randomUid = getRandomUid();

        if (type == ChatMessage.Type.IMAGE) {
            return "images/" + userId + "/" + timestamp + randomUid;
        } else if (type == ChatMessage.Type.VIDEO) {
            return "videos/" + userId + "/" + timestamp + randomUid;
        }
        return "unknown/" + userId + "/" + timestamp + randomUid;
    }

    /**
     * Returns the partner id of a chat room (only for single chats).
     */
    public static String getPartnerId(List<String> userIds) {
        String currentUserId = FirebaseUtil.getCurrentUserId();
        if (userIds.get(0).equals(currentUserId)) {
            return userIds.get(1);
        } else {
            return userIds.get(0);
        }
    }

    /**
     * Opens the chat room.
     */
    public static void openChatRoom(Context context, ChatRoom model) {
        Intent intent = new Intent(context, ChatRoomActivity.class);
        AndroidUtil.passChatRoomAsIntent(intent, model);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
