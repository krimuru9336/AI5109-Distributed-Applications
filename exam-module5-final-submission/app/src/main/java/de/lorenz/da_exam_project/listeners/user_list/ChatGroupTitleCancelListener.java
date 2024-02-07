package de.lorenz.da_exam_project.listeners.user_list;

import android.content.DialogInterface;

public class ChatGroupTitleCancelListener implements DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}
