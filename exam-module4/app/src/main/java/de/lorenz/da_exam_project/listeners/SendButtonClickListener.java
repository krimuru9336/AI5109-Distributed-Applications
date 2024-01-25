package de.lorenz.da_exam_project.listeners;

import android.view.View;
import android.widget.TextView;

import com.google.firebase.Timestamp;

import de.lorenz.da_exam_project.models.ChatMessage;
import de.lorenz.da_exam_project.models.ChatRoom;
import de.lorenz.da_exam_project.utils.AndroidUtil;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class SendButtonClickListener implements View.OnClickListener {

    TextView messageInput;
    String senderId;
    ChatRoom chatRoom;

    public SendButtonClickListener(TextView messageInput, String senderId, ChatRoom chatRoom) {
        this.messageInput = messageInput;
        this.senderId = senderId;
        this.chatRoom = chatRoom;
    }

    @Override
    public void onClick(View v) {
        String message = messageInput.getText().toString();

        // check if message is empty
        if (message.isEmpty()) {
            return;
        }

        sendMessage(message, messageInput);
    }

    /**
     * Sends a message to the chat room.
     */
    private void sendMessage(final String message, TextView messageInput) {
        Timestamp currentTimestamp = Timestamp.now();

        // send message by adding it to the chat room messages
        ChatMessage chatMessage = new ChatMessage(message, senderId, currentTimestamp);
        FirebaseUtil.getChatRoomMessagesReference(chatRoom.getId()).add(chatMessage).addOnCompleteListener(task -> {

            // check if message was sent successfully
            if (!task.isSuccessful()) {
                AndroidUtil.showToast(messageInput.getContext(), "Failed to send message!");
                return;
            }

            // get id of the sent message
            String firebaseMessageId = task.getResult().getId();

            // format message
            String formattedMessage = message.trim();

            // get message excerpt
            int maxStringLength = 25;
            String messageExcerpt = formattedMessage.length() > maxStringLength ? formattedMessage.substring(0, maxStringLength) + "..." : formattedMessage;

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
            messageInput.setText("");
        });
    }
}
