package de.lorenz.da_exam_project.listeners.user_list;

import android.widget.CompoundButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import de.lorenz.da_exam_project.Constants;
import de.lorenz.da_exam_project.adapters.UserListRecyclerAdapter;

public class CheckboxChangeListener implements CompoundButton.OnCheckedChangeListener {

    UserListRecyclerAdapter adapter;
    List<String> selectedUsers;
    FloatingActionButton addGroupButton;

    public CheckboxChangeListener(UserListRecyclerAdapter adapter, List<String> selectedUsers, FloatingActionButton addGroupButton) {
        this.adapter = adapter;
        this.selectedUsers = selectedUsers;
        this.addGroupButton = addGroupButton;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String checkedUserId = (String) buttonView.getTag();

        // add or remove user from selected users
        if (isChecked) {
            selectedUsers.add(checkedUserId);
        } else {
            selectedUsers.remove(checkedUserId);
        }

        // enable/disable button
        this.addGroupButton.setEnabled(selectedUsers.size() >= (Constants.MIN_GROUP_SIZE - 1));
    }
}
