package com.example.cchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.cchat.model.ChatRoomModel;
import com.example.cchat.utils.FirebaseUtil;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class CreateGroupActivity extends AppCompatActivity {

    EditText groupNameInput;
    Button createGroupBtn;
    String groupName;

    String chatroomId;

    ArrayList<String> selectedUsers;
    ChatRoomModel chatRoomModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        groupNameInput = findViewById(R.id.groupname_ip);
        createGroupBtn = findViewById(R.id.create_group_btn);
        selectedUsers = getIntent().getExtras().getStringArrayList("selectedUsers");

        createGroupBtn.setOnClickListener(v -> {
            createGroup();
        });
    }

    void createGroup() {
         groupName = groupNameInput.getText().toString();
        if(groupName.isEmpty() || groupName.length() < 3) {
            groupNameInput.setError("Group name should be at least 3 characters long");
            return;
        }

        if(chatRoomModel != null) {
            chatRoomModel.setGroupName(groupName);
        } else {
            chatroomId = UUID.randomUUID().toString();
            chatRoomModel = new ChatRoomModel(chatroomId, selectedUsers, Timestamp.now(), "", "group");
            chatRoomModel.setGroupName(groupName);
        }

        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                FirebaseUtil.getChatroomReference(chatroomId).set(chatRoomModel);
            }
        });
    }
}