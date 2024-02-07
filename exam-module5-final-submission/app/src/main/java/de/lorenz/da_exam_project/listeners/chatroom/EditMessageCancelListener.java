package de.lorenz.da_exam_project.listeners.chatroom;

import android.content.DialogInterface;

public class EditMessageCancelListener implements DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}
