package com.example.whatsdownapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.whatsdownapp.model.ChatMessageModel;
import com.example.whatsdownapp.model.ChatroomModel;
import com.example.whatsdownapp.utils.FirebaseUtil;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DeleteMessageDialog extends DialogFragment {


    private ChatMessageModel selectedMessage;
    private ChatroomModel selectedMessageRoom;

    private TextView cancelButton;

    private  Button deleteButton;

    public DeleteMessageDialog(ChatMessageModel selectedMessage, ChatroomModel chatRoom) {
        this.selectedMessage = selectedMessage;
        this.selectedMessageRoom = chatRoom;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete_message, container, false);

        cancelButton = view.findViewById(R.id.cancel_delete_btn);
        deleteButton = view.findViewById(R.id.delete_btn);

        deleteButton.setOnClickListener(v -> {
            Log.i("delete", selectedMessage.getMessage());
            boolean isLastMessage = selectedMessageRoom.getlastMessage().getId().equals( selectedMessage.getId());

            // Implement the logic to delete the selected message
            FirebaseUtil.getChatroomMessageReference(selectedMessageRoom.getChatroomId())
                    .whereEqualTo("id", selectedMessage.getId())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().update("deleted", true)
                                    .addOnSuccessListener(aVoid -> {
                                        if(isLastMessage){
                                            updateChatRoomLastMessage(selectedMessageRoom);
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
        });

        cancelButton.setOnClickListener(v->{
            dismiss();
        });


        return view;
    }

    private void updateChatRoomLastMessage(ChatroomModel chatroomModel) {
        chatroomModel.getlastMessage().setDeleted(true);
        FirebaseUtil.getChatroomReference(chatroomModel.getChatroomId()).set(chatroomModel);
    }

}