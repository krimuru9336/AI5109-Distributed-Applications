package com.example.chitchat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class MessageContext {
    public interface EditInputListener {
        void onInputProvided(String userInput);
    }
    public static void showContextMenu(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, onClickListener)
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public static void showInputField(Context context, Message origMessage, String title,
                                       String message, final EditInputListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.context_input, null);

        final EditText input = view.findViewById(R.id.editTextInput);
        ImageView imgDeleteAll = view.findViewById(R.id.deleteInput);

        input.setText(origMessage.getContent());

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
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void onDeleteInput(View view) {
        EditText input = view.getRootView().findViewById(R.id.editTextInput);
        if (input != null) {
            input.setText("");
        }
    }
}
