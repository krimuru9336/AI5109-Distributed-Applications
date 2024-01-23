package com.example.letschat;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.letschat.model.ChatMessage;
import com.example.letschat.model.ChatRoom;
import com.example.letschat.util.FirebaseUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MessageOptionsBottomSheet extends BottomSheetDialogFragment {
    static final String ARG_MESSAGE = "arg_message";
    static final String ARG_ROOM = "arg_room";
    ChatMessage selectedMessage;
    ChatRoom chatRoom;
    TextView editBtn, deleteBtn;
    LinearLayout mainOptionsLayout;

    View view;

    public MessageOptionsBottomSheet() {
        // Required empty public constructor
    }

    public static MessageOptionsBottomSheet newInstance(ChatMessage selectedMessage, ChatRoom room) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_MESSAGE,  selectedMessage);
        args.putParcelable(ARG_ROOM, room);
        MessageOptionsBottomSheet fragment = new MessageOptionsBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_message_options, container, false);

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
