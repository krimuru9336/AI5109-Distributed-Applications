package de.lorenz.da_exam_project.listeners.user_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

import de.lorenz.da_exam_project.Constants;
import de.lorenz.da_exam_project.R;
import de.lorenz.da_exam_project.utils.AndroidUtil;

public class AddGroupButtonClickListener implements View.OnClickListener {

    Context context;
    List<String> selectedUsers;

    public AddGroupButtonClickListener(Context context, List<String> selectedUsers) {
        this.context = context;
        this.selectedUsers = selectedUsers;
    }

    @Override
    public void onClick(View v) {
        int usersCount = selectedUsers.size();

        if (usersCount < (Constants.MIN_GROUP_SIZE - 1)) {
            AndroidUtil.showToast(context, "You need to select at least " + (Constants.MIN_GROUP_SIZE - 1) + " user");
            return;
        }

        openChatTitleDialog();
    }

    public void openChatTitleDialog() {

        View dialogView = LayoutInflater.from(this.context).inflate(R.layout.set_chat_group_title_layout, null);

        EditText editMessageInput = dialogView.findViewById(R.id.chat_group_title);

        // create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Set group title");
        builder.setView(dialogView);

        ChatGroupTitleCreateListener chatGroupTitleCreateListener = new ChatGroupTitleCreateListener(this.context, this.selectedUsers, editMessageInput);
        builder.setPositiveButton(R.string.create, chatGroupTitleCreateListener);

        ChatGroupTitleCancelListener chatGroupTitleCancelListener = new ChatGroupTitleCancelListener();
        builder.setNegativeButton(R.string.cancel, chatGroupTitleCancelListener);

        builder.create().show();
    }
}
