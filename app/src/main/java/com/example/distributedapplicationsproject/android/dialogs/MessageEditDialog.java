package com.example.distributedapplicationsproject.android.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.distributedapplicationsproject.R;
import com.example.distributedapplicationsproject.models.Message;
import com.google.android.material.textfield.TextInputEditText;
import org.jetbrains.annotations.NotNull;

public class MessageEditDialog extends DialogFragment {

    Message message;
    OnMessageEditDialogListener listener;

    TextInputEditText textEdit;

    public MessageEditDialog(Message message, OnMessageEditDialogListener listener) {
        super();
        this.listener = listener;
        this.message = message;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = initDialogView();
        builder.setView(view)
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        message.setMessage(textEdit.getText().toString());
                        listener.onMessageEditDialogEdit(message);
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onMessageEditDialogDelete();
                    }
                }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onMessageEditDialogCancel();
                    }
                });
        // Create the AlertDialog object and return it.
        return builder.create();
    }

    private View initDialogView() {
        // Get the layout inflater.
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_message_edit, null);

        textEdit = view.findViewById(R.id.text_input_edit_text);
        textEdit.setText(message.getMessage());
        return view;
    }

    public interface OnMessageEditDialogListener {
        void onMessageEditDialogCancel();

        void onMessageEditDialogDelete();

        void onMessageEditDialogEdit(Message editedMessage);
    }

}
