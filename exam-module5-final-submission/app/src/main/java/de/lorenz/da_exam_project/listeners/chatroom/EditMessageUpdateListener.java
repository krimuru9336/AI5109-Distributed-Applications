package de.lorenz.da_exam_project.listeners.chatroom;

import android.content.DialogInterface;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentReference;

import de.lorenz.da_exam_project.ChatRoomActivity;
import de.lorenz.da_exam_project.models.ChatRoom;
import de.lorenz.da_exam_project.utils.AndroidUtil;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class EditMessageUpdateListener implements DialogInterface.OnClickListener {

    private final ChatRoomActivity activity;
    private final String chatRoomId;
    private final String chatMessageId;
    private final EditText editMessageInput;

    public EditMessageUpdateListener(ChatRoomActivity activity, String chatRoomId, String chatMessageId, EditText editMessageInput) {
        this.activity = activity;
        this.chatRoomId = chatRoomId;
        this.chatMessageId = chatMessageId;
        this.editMessageInput = editMessageInput;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String newMessage = editMessageInput.getText().toString();

        // update the long clicked chat message in the database
        DocumentReference chatMessageReference = FirebaseUtil.getChatRoomMessagesReference(chatRoomId).document(chatMessageId);
        chatMessageReference.update("message", newMessage);

        // update chat room in database (only if last message was edited)
        FirebaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }

            // get chat room
            ChatRoom chatRoom = task.getResult().toObject(ChatRoom.class);

            // check if chat room exists
            if (chatRoom == null) {
                return;
            }

            if (chatRoom.getLastMessageId().equals(chatMessageId)) {
                task.getResult().getReference().update("lastMessage", newMessage);
            }
        });


        AndroidUtil.showToast(activity, "Message updated");
    }
}
