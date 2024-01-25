package de.lorenz.da_exam_project;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import de.lorenz.da_exam_project.listeners.chatroom.EditMessageCancelListener;
import de.lorenz.da_exam_project.listeners.chatroom.EditMessageUpdateListener;
import de.lorenz.da_exam_project.models.ChatRoom;
import de.lorenz.da_exam_project.utils.AndroidUtil;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class ChatRoomActions {

    private ChatRoomActivity activity;
    private Context context;
    String chatRoomId;

    public ChatRoomActions(Context context, String chatRoomId) {
        this.context = context;
        this.chatRoomId = chatRoomId;

        if (context instanceof ChatRoomActivity) {
            this.activity = (ChatRoomActivity) context;
        }
    }

    /**
     * This function creates an AlertDialog to edit a chat message.
     */
    public boolean editMessage(MenuItem item) {
        String chatMessageId = this.activity.getLongClickedChatMessageModel().getId();

        View dialogView = this.activity.getLayoutInflater().inflate(R.layout.edit_message_layout, null);

        EditText editMessageInput = dialogView.findViewById(R.id.edit_message_text);
        editMessageInput.setText(this.activity.getLongClickedChatMessageModel().getMessage());

        // create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit message");
        builder.setView(dialogView);

        EditMessageUpdateListener editMessageUpdateListener = new EditMessageUpdateListener(this.activity, chatRoomId, chatMessageId, editMessageInput);
        builder.setPositiveButton(R.string.update, editMessageUpdateListener);

        EditMessageCancelListener messageDeleteListener = new EditMessageCancelListener();
        builder.setNegativeButton(R.string.cancel, messageDeleteListener);

        builder.create().show();

        return true;
    }

    /**
     * This function removes the current longClickedChatMessage from the firestore database.
     */
    public boolean deleteMessage(MenuItem item) {
        String chatMessageId = this.activity.getLongClickedChatMessageModel().getId();

        // remove the long clicked chat message from the database
        FirebaseUtil.getChatRoomMessagesReference(this.chatRoomId).document(chatMessageId).delete();

        // show a notification that the message was deleted
        AndroidUtil.showToast(this.activity, "Message deleted");

        // update chat room in database (only if last message was deleted)
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
                task.getResult().getReference().update("lastMessage", "*deleted*");
            }
        });

        return true;
    }
}
