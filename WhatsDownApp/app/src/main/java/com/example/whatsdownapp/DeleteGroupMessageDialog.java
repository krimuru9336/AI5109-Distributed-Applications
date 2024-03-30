package com.example.whatsdownapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.whatsdownapp.model.ChatGroupModel;
import com.example.whatsdownapp.model.ChatMessageModel;
import com.example.whatsdownapp.utils.FirebaseUtil;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DeleteGroupMessageDialog extends DialogFragment {


    private ChatMessageModel selectedMessage;
    private ChatGroupModel selectedMessageGroup;

    private TextView cancelButton;

    private  Button deleteButton;

    public DeleteGroupMessageDialog(ChatMessageModel selectedMessage, ChatGroupModel chatGroup) {
        this.selectedMessage = selectedMessage;
        this.selectedMessageGroup= chatGroup;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete_message, container, false);

        cancelButton = view.findViewById(R.id.cancel_delete_btn);
        deleteButton = view.findViewById(R.id.delete_btn);

        deleteButton.setOnClickListener(v -> {
            boolean isLastMessage = selectedMessageGroup.getLastMsg().getId().equals( selectedMessage.getId());

            // Implement the logic to delete the selected message
            FirebaseUtil.getGroupChatMessageReference(selectedMessageGroup.getGroupId())
                    .whereEqualTo("id", selectedMessage.getId())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().update("deleted", true)
                                    .addOnSuccessListener(aVoid -> {
                                        if(isLastMessage){
                                            updateGroupChatRoomLastMessage(selectedMessageGroup);
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

    private void updateGroupChatRoomLastMessage(ChatGroupModel chatGroup) {
        chatGroup.getLastMsg().setDeleted(true);
        FirebaseUtil.getGroupChatsReference(chatGroup.getGroupId()).set(chatGroup);
    }

}