package com.example.whatsdownapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.whatsdownapp.model.ChatMessageModel;
import com.example.whatsdownapp.model.ChatroomModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ChatMenuOptionsBottomSheet extends BottomSheetDialogFragment {
    static final String ARG_MESSAGE = "arg_message";
    static final String ARG_ROOM = "arg_room";
    ChatMessageModel selectedMessage;
    ChatroomModel chatRoom;
    TextView editBtn, deleteBtn;
    LinearLayout mainOptionsLayout;
    View view;

    public ChatMenuOptionsBottomSheet() {

    }

    public static ChatMenuOptionsBottomSheet newInstance(ChatMessageModel selectedMessage, ChatroomModel room) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_MESSAGE,  selectedMessage);
        args.putParcelable(ARG_ROOM, room);
        ChatMenuOptionsBottomSheet fragment = new ChatMenuOptionsBottomSheet();
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
        if (selectedMessage != null && chatRoom != null) {
            Log.d("Click Edit", selectedMessage.getMessage());
            view.setVisibility(View.GONE);
            // Show the edit dialog, passing the original message
            EditMessageDialog editMessageDialog = new EditMessageDialog(selectedMessage, chatRoom);
            editMessageDialog.show(getChildFragmentManager(), "EditMessageDialog");
        }

    }

    private void onDeleteClicked() {
        if (selectedMessage != null && chatRoom != null) {
            Log.d("Click Deletion", selectedMessage.getMessage());
            view.setVisibility(View.GONE);
            DeleteMessageDialog deleteMessageDialog = new DeleteMessageDialog(selectedMessage, chatRoom);
            deleteMessageDialog.show(getChildFragmentManager(), "DeleteMessageDialog");

        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedMessage = getArguments().getParcelable(ARG_MESSAGE);
            chatRoom= getArguments().getParcelable(ARG_ROOM);
        }
    }

}
