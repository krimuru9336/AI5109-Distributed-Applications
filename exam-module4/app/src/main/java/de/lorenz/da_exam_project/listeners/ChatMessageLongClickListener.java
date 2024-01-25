package de.lorenz.da_exam_project.listeners;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.lorenz.da_exam_project.ChatRoomActivity;
import de.lorenz.da_exam_project.models.ChatMessage;

public class ChatMessageLongClickListener implements View.OnLongClickListener {

    private final Context context;
    private final TextView chatMessage;
    private final ChatMessage model;

    public ChatMessageLongClickListener(Context context, TextView chatMessage, ChatMessage model) {
        this.context = context;
        this.chatMessage = chatMessage;
        this.model = model;
    }

    @Override
    public boolean onLongClick(View v) {
        if (context instanceof AppCompatActivity && context instanceof ChatRoomActivity) {

            // set the long clicked chat message in the activity
            ((ChatRoomActivity) context).setLongClickedChatMessage(this.chatMessage);
            ((ChatRoomActivity) context).setLongClickedChatMessageModel(this.model);
            System.out.println("long clicked chat message: " + chatMessage.getText());

            AppCompatActivity activity = (AppCompatActivity) context;
            activity.openContextMenu(chatMessage);
            return true;
        }
        return false;
    }
}
