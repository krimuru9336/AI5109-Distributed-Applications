package com.da.chitchat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class DialogHelper {
    public interface InputDialogListener {
        void onInputProvided(String userInput);
    }

    public static void showConfirmationDialog(Context context, String title, String message, DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, positiveListener)
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public static void showInputDialog(Context context, Message origMessage, String title,
                                       String message, final InputDialogListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.input_dialog, null);

        final EditText input = view.findViewById(R.id.editTextInput);
        ImageView imgDeleteAll = view.findViewById(R.id.imgDeleteAll);

        input.setText(origMessage.getText());

        imgDeleteAll.setOnClickListener(v -> input.setText(""));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setView(view)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    String userInput = input.getText().toString().trim();

                    if (listener != null) {
                        listener.onInputProvided(userInput);
                    }
                })
                .setNegativeButton(android.R.string.no, null);

        final AlertDialog dialog = builder.create();
        dialog.show();

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean isInputEmpty = charSequence.toString().trim().isEmpty();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!isInputEmpty);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing
            }
        });

        input.requestFocus();
    }
}
