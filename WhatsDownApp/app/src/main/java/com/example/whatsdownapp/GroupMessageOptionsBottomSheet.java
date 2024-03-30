package com.example.whatsdownapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.whatsdownapp.model.ChatGroupModel;
import com.example.whatsdownapp.model.ChatMessageModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class GroupMessageOptionsBottomSheet extends BottomSheetDialogFragment {
    static final String ARG_MESSAGE = "arg_message";
    static final String ARG_ROOM = "arg_room";
    ChatMessageModel selectedMessage;
    ChatGroupModel groupChat;
    TextView editBtn, deleteBtn;
    LinearLayout mainOptionsLayout;

    View view;

    public GroupMessageOptionsBottomSheet() {
        // Required empty public constructor
    }

    public static GroupMessageOptionsBottomSheet newInstance(ChatMessageModel selectedMessage, ChatGroupModel chatGroup) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_MESSAGE,  selectedMessage);
        args.putParcelable(ARG_ROOM, chatGroup);
        GroupMessageOptionsBottomSheet fragment = new GroupMessageOptionsBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chat_bottom_menu_option, container, false);

        deleteBtn = view.findViewById(R.id.deleteMessage);
        deleteBtn.setOnClickListener(v -> onDeleteClicked());
        editBtn = view.findViewById(R.id.editMessage);
        editBtn.setOnClickListener(v -> onEditClicked());

        return view;
    }

    private void onEditClicked() {
        if (selectedMessage != null && groupChat != null) {
            Log.d("Click Edit", selectedMessage.getMessage());
            view.setVisibility(View.GONE);
            // Show the edit dialog, passing the original message
            EditGroupMessageDialog editMessageDialog = new EditGroupMessageDialog(selectedMessage, groupChat);
            editMessageDialog.show(getChildFragmentManager(), "EditMessageDialog");
        }

    }

    private void onDeleteClicked() {
        if (selectedMessage != null && groupChat != null) {
            Log.d("Click Deletion", selectedMessage.getMessage());
            view.setVisibility(View.GONE);
            DeleteGroupMessageDialog deleteMessageDialog = new DeleteGroupMessageDialog(selectedMessage, groupChat);
            deleteMessageDialog.show(getChildFragmentManager(), "DeleteMessageDialog");

        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedMessage = getArguments().getParcelable(ARG_MESSAGE);
            groupChat = getArguments().getParcelable(ARG_ROOM);
        }
    }

}