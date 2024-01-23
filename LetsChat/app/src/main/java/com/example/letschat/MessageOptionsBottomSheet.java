package com.example.letschat;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        View view = inflater.inflate(R.layout.bottom_sheet_message_options, container, false);

        deleteBtn = view.findViewById(R.id.deleteMessage);
        deleteBtn.setOnClickListener(v -> onDeleteClicked());

        return view;
    }

    private void onDeleteClicked() {
        if (selectedMessage != null && chatRoom != null) {
            Log.d("Click", selectedMessage.getMessage());
            boolean isLastMessage = chatRoom.getLastMsg().getId().equals( selectedMessage.getId());

            // Implement the logic to delete the selected message
            FirebaseUtil.getChatMessageReference(chatRoom.getChatRoomId())
                    .whereEqualTo("id", selectedMessage.getId())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().update("deleted", true)
                                    .addOnSuccessListener(aVoid -> {
                                        if(isLastMessage){
                                           updateChatRoomLastMessage(chatRoom);
                                        }
                                        Log.d("Delete", "Message deleted successfully");
                                        dismiss(); // Close the bottom sheet after performing the action
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Delete", "Error deleting message", e);
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Delete", "Error querying for message", e);
                    });
        }
    }

    private void updateChatRoomLastMessage(ChatRoom chatRoom) {

        chatRoom.getLastMsg().setDeleted(true);

        FirebaseUtil.getChatRoomReference(chatRoom.getChatRoomId()).set(chatRoom);
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
