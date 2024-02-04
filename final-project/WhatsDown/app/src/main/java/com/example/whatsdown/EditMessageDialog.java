package com.example.whatsdown;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.whatsdown.model.ChatMessageModel;
import com.example.whatsdown.model.ChatroomModel;
import com.example.whatsdown.utils.AndroidUtil;
import com.example.whatsdown.utils.FirebaseUtil;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class EditMessageDialog extends DialogFragment {

    private EditText editMessageInput;
    private TextView cancelButton;
    private Button updateButton;
    private String originalMessage;
    private ChatMessageModel selectedMessage;
    private ChatroomModel selectedChatroom;


    public EditMessageDialog(ChatMessageModel selectedMessage, ChatroomModel chatroom) {
        this.selectedMessage = selectedMessage;
        this.selectedChatroom = chatroom;
        this.originalMessage = selectedMessage.getMessage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_message_dialog, container, false);

        editMessageInput = view.findViewById(R.id.edit_message_input);
        updateButton = view.findViewById(R.id.update_btn);
        cancelButton = view.findViewById(R.id.cancel_update_btn);

        editMessageInput.setText(originalMessage);

        updateButton.setOnClickListener(v -> {
            String editedMessage = editMessageInput.getText().toString().trim();

            if (!editedMessage.isEmpty()) {
                updateMessageInDatabase(editedMessage, selectedChatroom, selectedMessage);
                dismiss();
            } else {
                AndroidUtil.showToast(requireContext(),"Message cannot be empty");
            }
        });

        cancelButton.setOnClickListener(v -> {
            dismiss();
        });

        return view;
    }

    private void updateMessageInDatabase(String editedMessage, ChatroomModel chatroom, ChatMessageModel message) {
        boolean isLastMessage = chatroom.getLastMessage().getId().equals(message.getId());

        FirebaseUtil.getChatroomMessageReference(chatroom.getChatroomId())
                .whereEqualTo("id", message.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().update("message", editedMessage)
                                .addOnSuccessListener(aVoid -> {
                                    if (isLastMessage) {
                                        updateChatroomLastMessage(chatroom, editedMessage);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Edit", "Error updating message", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Edit", "Error querying for message", e);
                });
    }

    private void updateChatroomLastMessage(ChatroomModel chatroom, String newMessage) {
        chatroom.getLastMessage().setMessage(newMessage);
        FirebaseUtil.getChatroomReference(chatroom.getChatroomId()).set(chatroom);
    }
}