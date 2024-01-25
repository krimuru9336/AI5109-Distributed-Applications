package de.lorenz.da_exam_project.listeners;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import de.lorenz.da_exam_project.ChatListActivity;
import de.lorenz.da_exam_project.models.User;
import de.lorenz.da_exam_project.utils.AndroidUtil;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class LoginButtonClickListener implements View.OnClickListener {

    private EditText username;
    private Button loginButton;
    private ProgressBar progressBar;
    private User user;

    public LoginButtonClickListener(EditText username, Button loginButton, ProgressBar progressBar) {
        this.username = username;
        this.loginButton = loginButton;
        this.progressBar = progressBar;
    }

    @Override
    public void onClick(View v) {
        setInProgress(true);

        // if username is empty, show notification and return
        if (username.getText().toString().isEmpty()) {
            AndroidUtil.showToast(v.getContext(), "Please enter a username");
            return;
        }

        // register firebase user
        FirebaseUtil.registerUserAnonymously().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                AndroidUtil.showToast(v.getContext(), "Login failed");
                return;
            }

            String firebaseId = FirebaseUtil.getCurrentUserId();

            // create user object
            user = new User(firebaseId, username.getText().toString());
            user.setRegistrationTimestamp(System.currentTimeMillis());

            addUserToDatabase(v, user);
        });
    }

    /**
     * Adds a new user to the database.
     */
    private void addUserToDatabase(View v, User newUser) {

        FirebaseUtil.getCurrentUserDetails().set(newUser).addOnCompleteListener(task -> {

            // if adding user to database failed, show notification and return
            if (!task.isSuccessful()) {
                AndroidUtil.showToast(v.getContext(), "Login failed");
                return;
            }

            // Show notification that login/registration was successful
            AndroidUtil.showToast(v.getContext(), "Login successful");

            // redirect to chat list
            Intent intent = new Intent(v.getContext(), ChatListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            v.getContext().startActivity(intent);
        });
    }

    /**
     * Set the whole view to be in progress or not.
     */
    private void setInProgress(boolean inProgress) {
        loginButton.setEnabled(!inProgress);
        progressBar.setVisibility(inProgress ? ProgressBar.VISIBLE : ProgressBar.INVISIBLE);
    }
}
