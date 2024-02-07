package de.lorenz.da_exam_project.listeners.user_list;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.List;

import de.lorenz.da_exam_project.models.ChatRoom;
import de.lorenz.da_exam_project.utils.AndroidUtil;
import de.lorenz.da_exam_project.utils.ChatRoomUtil;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class ChatGroupTitleCreateListener implements DialogInterface.OnClickListener {

    Context context;
    List<String> selectedUsers;
    EditText editMessageInput;

    public ChatGroupTitleCreateListener(Context context, List<String> selectedUsers, EditText editMessageInput) {
        this.context = context;
        this.selectedUsers = selectedUsers;
        this.editMessageInput = editMessageInput;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String title = editMessageInput.getText().toString();

        // create a new chat with the selected users
        this.createGroupChatRoom(title).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                AndroidUtil.showToast(context, "Failed to create chat");
                return;
            }

            ChatRoom chatRoom = task.getResult();

            // open the created chat
            ChatRoomUtil.openChatRoom(this.context, chatRoom);
        });
    }


    /**
     * Checks whether the chat room exists and creates it if not.
     */
    private Task<ChatRoom> createGroupChatRoom(String groupTitle) {
        String randomChatRoomId = FirebaseUtil.getRandomChatRoomId();

        // add own user id to selected users
        selectedUsers.add(FirebaseUtil.getCurrentUserId());

        ChatRoom chatRoomGroup = new ChatRoom(randomChatRoomId, selectedUsers, 0, "", "", "", groupTitle);

        final TaskCompletionSource<ChatRoom> taskCompletionSource = new TaskCompletionSource<>();

        FirebaseUtil.getChatRoomReference(randomChatRoomId).set(chatRoomGroup).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Task was successful, complete the task with the created ChatRoom
                taskCompletionSource.setResult(chatRoomGroup);
            } else {
                // Task failed, complete the task with the exception
                taskCompletionSource.setException(task.getException());
            }
        });

        return taskCompletionSource.getTask();
    }
}
