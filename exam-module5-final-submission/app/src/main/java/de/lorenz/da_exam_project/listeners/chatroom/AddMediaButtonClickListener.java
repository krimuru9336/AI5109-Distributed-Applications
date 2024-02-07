package de.lorenz.da_exam_project.listeners.chatroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import de.lorenz.da_exam_project.models.ChatRoom;

public class AddMediaButtonClickListener implements View.OnClickListener {

    private static final int RESULT_OK = 1; // Request code for media picker
    Context context;
    String currentUserId;
    ChatRoom chatRoom;

    public AddMediaButtonClickListener(Context context, String currentUserId, ChatRoom chatRoom) {
        this.context = context;
        this.currentUserId = currentUserId;
        this.chatRoom = chatRoom;
    }

    @Override
    public void onClick(View v) {
        openMediaChooser();
    }

    private void openMediaChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/* video/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        ((Activity) context).startActivityForResult(intent, RESULT_OK);
    }
}
