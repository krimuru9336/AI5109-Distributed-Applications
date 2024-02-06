package com.example.chitchatapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

        input.setText(origMessage.getMessage());

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
                .setNegativeButton(android.R.string.no, null) // null listener for dismissing the dialog without any action
                .show();
    }

    public void onDeleteAllClick(View view) {
        EditText input = view.getRootView().findViewById(R.id.editTextInput);
        if (input != null) {
            input.setText("");
        }
    }
}
