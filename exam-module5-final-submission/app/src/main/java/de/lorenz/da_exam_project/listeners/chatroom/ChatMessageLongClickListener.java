package de.lorenz.da_exam_project.listeners.chatroom;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import de.lorenz.da_exam_project.ChatRoomActivity;
import de.lorenz.da_exam_project.models.ChatMessage;

public class ChatMessageLongClickListener implements View.OnLongClickListener {

    private final Context context;
    private final LinearLayout chatLayout;
    private final ChatMessage model;

    public ChatMessageLongClickListener(Context context, LinearLayout chatLayout, ChatMessage model) {
        this.context = context;
        this.chatLayout = chatLayout;
        this.model = model;
    }

    @Override
    public boolean onLongClick(View v) {
        if (context instanceof AppCompatActivity && context instanceof ChatRoomActivity) {

            // set the long clicked chat message in the activity
            ((ChatRoomActivity) context).setLongClickedChatLayout(this.chatLayout);
            ((ChatRoomActivity) context).setLongClickedChatMessageModel(this.model);

            AppCompatActivity activity = (AppCompatActivity) context;
            activity.openContextMenu(this.chatLayout);
            return true;
        }
        return false;
    }
}
