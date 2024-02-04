package com.example.whatsdown;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.whatsdown.model.ChatMessageModel;
import com.example.whatsdown.model.ChatroomModel;
import com.example.whatsdown.utils.FirebaseUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MessageEditBottomSheet extends BottomSheetDialogFragment {

    static final String ARG_MSG = "arg_message";
    private static final String ARG_ROOM = "arg_room";

    ChatMessageModel selectedMessage;
    ChatroomModel chatroom;
    TextView editBtn, deleteBtn;
    LinearLayout mainOptionsLayout;

    View view;


    public MessageEditBottomSheet() {
        // Required empty public constructor
    }
    public static MessageEditBottomSheet newInstance(ChatMessageModel selectedMessage, ChatroomModel chatroom) {
        MessageEditBottomSheet fragment = new MessageEditBottomSheet();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MSG, selectedMessage);
        args.putParcelable(ARG_ROOM, chatroom);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedMessage = getArguments().getParcelable(ARG_MSG);
            chatroom = getArguments().getParcelable(ARG_ROOM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message_edit_bottom_sheet, container, false);

        deleteBtn = view.findViewById(R.id.deleteMessage);
        deleteBtn.setOnClickListener(v -> onDeleteClicked());
        editBtn = view.findViewById(R.id.editMessage);
        editBtn.setOnClickListener(v -> onEditClicked());

        return view;
    }

    private void updateChatRoomLastMessage(ChatroomModel chatroom) {

        chatroom.getLastMessage().setDeleted(true);

        FirebaseUtil.getChatroomReference(chatroom.getChatroomId()).set(chatroom);
    }

    private void onDeleteClicked() {
        if (selectedMessage != null && chatroom != null) {
            boolean isLastMessage = chatroom.getLastMessage().getId().equals( selectedMessage.getId());

            //delete the selected message
            view.setVisibility(View.GONE);
            DeleteMessageDialog deleteMessageDialog = new DeleteMessageDialog(selectedMessage, chatroom);
            deleteMessageDialog.show(getChildFragmentManager(), "DeleteMessageDialog");
        }
    }

    private void onEditClicked() {
        if (selectedMessage != null && chatroom != null) {
            //edit the selected message
            view.setVisibility(View.GONE);
            EditMessageDialog  editMessageDialog  = new EditMessageDialog(selectedMessage, chatroom);
            editMessageDialog.show(getChildFragmentManager(), "EditMessageDialog");
        }
    }
}