// Sven Schickentanz - fdai7287
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

import java.util.ArrayList;
import java.util.List;

/**
 * The DialogHelper class provides utility methods for displaying various types of dialogs.
 */
public class DialogHelper {
    /**
     * The InputDialogListener interface provides a callback method to be invoked when the user
     * provides input in the dialog.
     */
    public interface InputDialogListener {
        void onInputProvided(String userInput);
    }

    /**
     * The MultiChoiceListener interface provides a callback method to be invoked when the user
     * selects multiple choices in the dialog.
     */
    public interface MultiChoiceListener {
        void onMultiChoice(String[] users);
    }

    /**
     * Displays a confirmation dialog with the specified title and message.
     *
     * @param context          The context in which the dialog should be displayed.
     * @param title            The title of the dialog.
     * @param message          The message to be displayed in the dialog.
     * @param positiveListener The listener to be invoked when the user clicks the positive button.
     */
    public static void showConfirmationDialog(Context context, String title, String message,
                                              DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, positiveListener)
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    /**
     * Displays a simple alert dialog with the specified title and message.
     *
     * @param context The context in which the dialog should be displayed.
     * @param title   The title of the dialog.
     * @param message The message to be displayed in the dialog.
     */
    public static void showSimpleAlert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    /**
     * Displays an input dialog with the specified title and message.
     *
     * @param context  The context in which the dialog should be displayed.
     * @param origMessage The original message to be displayed in the input field.
     * @param title    The title of the dialog.
     * @param message  The message to be displayed in the dialog.
     * @param listener The listener to be invoked when the user provides input.
     */
    public static void showInputDialog(Context context, Message origMessage, String title,
                                       String message, final InputDialogListener listener) {
        // Inflate the input dialog layout
        View view = LayoutInflater.from(context).inflate(R.layout.input_dialog, null);

        final EditText input = view.findViewById(R.id.editTextInput);
        // Get the delete all button - which clears the input field
        ImageView imgDeleteAll = view.findViewById(R.id.imgDeleteAll);

        if (origMessage != null)
            input.setText(origMessage.getText());

        // Set the delete all button click listener to clear the input field
        imgDeleteAll.setOnClickListener(v -> input.setText(""));

        // Create the input dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setView(view)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    String userInput = input.getText().toString().trim();

                    // Pass the user input to the listener
                    if (listener != null) {
                        listener.onInputProvided(userInput);
                    }
                })
                .setNegativeButton(android.R.string.no, null);

        final AlertDialog dialog = builder.create();
        dialog.show();

        // Disable the positive button by default
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing
            }

            // Enable the positive button when the user provides input
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

        // Request focus on the input field
        input.requestFocus();
    }

    /**
     * Displays a multi-choice dialog with the specified title and users.
     *
     * @param context     The context in which the dialog should be displayed.
     * @param users       The list of users to be displayed in the dialog.
     * @param usersInGroup The list of users already in the group.
     * @param title       The title of the dialog.
     * @param listener    The listener to be invoked when the user selects multiple choices.
     */
    public static void showUserSelectDialog(Context context, String[] users, String[] usersInGroup,
                                            String title, final MultiChoiceListener listener) {
        final boolean[] checked = new boolean[users.length];

        for (int i = 0; i < users.length; i++) {
            for (String userInGroup : usersInGroup) {
                if (users[i].equals(userInGroup)) {
                    checked[i] = true;
                    break;
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMultiChoiceItems(users, checked, (dialog, which, isChecked) -> checked[which] = isChecked);

        // Set the positive button to pass the selected users to the listener
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            List<String> selectedUsers = new ArrayList<>();
            for (int i = 0; i < users.length; i++) {
                if (checked[i]) {
                    selectedUsers.add(users[i]);
                }
            }
            // Pass the selected users to the listener
            if (listener != null) {
                listener.onMultiChoice(selectedUsers.toArray(new String[0]));
            }
        });

        // Set the negative button to cancel the dialog
        builder.setNegativeButton(android.R.string.cancel, null);

        builder.show();
    }
}
