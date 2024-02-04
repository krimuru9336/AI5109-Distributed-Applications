package com.example.whatsdown;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.whatsdown.model.ChatMessageModel;
import com.example.whatsdown.model.ChatroomModel;
import com.example.whatsdown.utils.FirebaseUtil;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DeleteMessageDialog extends DialogFragment {

    private ChatMessageModel selectedMessage;
    private ChatroomModel selectedChatroom;

    private TextView cancelButton;
    private Button deleteButton;

    public DeleteMessageDialog(ChatMessageModel message, ChatroomModel chatroom) {
        this.selectedMessage = message;
        this.selectedChatroom = chatroom;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_message_dialog, container, false);

        cancelButton = view.findViewById(R.id.cancel_delete_btn);
        deleteButton = view.findViewById(R.id.delete_btn);

        deleteButton.setOnClickListener(v -> {
            boolean isLastMessage = selectedChatroom.getLastMessage().getId().equals( selectedMessage.getId());

            //delete the selected message
            FirebaseUtil.getChatroomMessageReference(selectedChatroom.getChatroomId())
                    .whereEqualTo("id", selectedMessage.getId())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().update("deleted", true)
                                    .addOnSuccessListener(aVoid -> {
                                        if(isLastMessage){
                                            updateChatroomLastMessage(selectedChatroom);
                                        }
                                        dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Delete", "Error deleting message", e);
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Delete", "Error querying for message", e);
                    });
        });

        cancelButton.setOnClickListener(v->{
            dismiss();
        });


        return view;
    }

    private void updateChatroomLastMessage(ChatroomModel chatroom) {
        chatroom.getLastMessage().setDeleted(true);
        FirebaseUtil.getChatroomReference(chatroom.getChatroomId()).set(chatroom);
    }
}