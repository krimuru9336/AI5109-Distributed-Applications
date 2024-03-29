package com.example.whatsdownapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.whatsdownapp.model.ChatMessageModel;
import com.example.whatsdownapp.model.ChatroomModel;
import com.example.whatsdownapp.utils.AndroidUtil;
import com.example.whatsdownapp.utils.FirebaseUtil;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class EditMessageDialog extends DialogFragment {

    private EditText editMessageInput;
    private Button updateButton;
    private String originalMessage;

    private ChatMessageModel selectedMessage;
    private ChatroomModel selectedMessageRoom;

    private TextView cancelButton;

    public EditMessageDialog(ChatMessageModel selectedMessage, ChatroomModel chatRoom) {
        this.selectedMessage = selectedMessage;
        this.selectedMessageRoom = chatRoom;
        this.originalMessage = selectedMessage.getMessage();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_message, container, false);

        editMessageInput = view.findViewById(R.id.edit_message_input);
        updateButton = view.findViewById(R.id.update_btn);
        cancelButton = view.findViewById(R.id.cancel_update_btn);

        // Pre-fill the EditText with the original message
        editMessageInput.setText(originalMessage);

        updateButton.setOnClickListener(v -> {
            // Get the edited message from the EditText
            String editedMessage = editMessageInput.getText().toString().trim();

            // Perform any necessary validations on the edited message
            if (!editedMessage.isEmpty()) {
                updateMessageInDatabase(editedMessage, selectedMessageRoom, selectedMessage);
                dismiss(); // Close the dialog
            } else {
                AndroidUtil.showSnackBar(v, "Message cannot be empty", getResources());
            }
        });

        cancelButton.setOnClickListener(v -> {
            dismiss();
        });

        return view;
    }

    private void updateMessageInDatabase(String editedMessage, ChatroomModel chatRoom, ChatMessageModel message) {
        // Update the selected message with the edited content in the database
        boolean isLastMessage = chatRoom.getlastMessage().getId().equals(message.getId());

        FirebaseUtil.getChatroomMessageReference(chatRoom.getChatroomId())
                .whereEqualTo("id", message.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().update("message", editedMessage)
                                .addOnSuccessListener(aVoid -> {
                                    if (isLastMessage) {
                                        updateChatRoomLastMessage(chatRoom, editedMessage);
                                    }
                                    Log.d("Edit", "Message updated successfully");
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

    private void updateChatRoomLastMessage(ChatroomModel chatRoom, String newMessage) {
        chatRoom.getlastMessage().setMessage(newMessage);
        FirebaseUtil.getChatroomReference(chatRoom.getChatroomId()).set(chatRoom);
    }
}