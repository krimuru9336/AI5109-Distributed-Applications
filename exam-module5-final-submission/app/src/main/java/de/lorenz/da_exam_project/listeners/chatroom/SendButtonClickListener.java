package de.lorenz.da_exam_project.listeners.chatroom;

import android.view.View;
import android.widget.TextView;

import de.lorenz.da_exam_project.models.ChatMessage;
import de.lorenz.da_exam_project.models.ChatRoom;
import de.lorenz.da_exam_project.utils.ChatRoomUtil;

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

        ChatRoomUtil.sendMessage(v.getContext(), chatRoom, message, ChatMessage.Type.TEXT, senderId, messageInput);
    }
}
