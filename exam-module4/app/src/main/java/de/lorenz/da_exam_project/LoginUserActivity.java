package de.lorenz.da_exam_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import de.lorenz.da_exam_project.listeners.LoginButtonClickListener;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class LoginUserActivity extends AppCompatActivity {

    EditText usernameInput;
    Button loginButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        usernameInput = findViewById(R.id.usernameInput);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        // register login button click
        LoginButtonClickListener loginButtonClickListener = new LoginButtonClickListener(usernameInput, loginButton, progressBar);
        loginButton.setOnClickListener(loginButtonClickListener);

        loginUser();
    }

    /**
     * Login the user anonymously if not already logged in.
     */
    void loginUser() {
        this.setInProgress(true);

        // if user id is empty
        String currentUserId = FirebaseUtil.getCurrentUserId();
        if (currentUserId == null) {
            // now wait for login button click to register user...
            setInProgress(false);
            return;
        }

        // check if user is in database
        Objects.requireNonNull(FirebaseUtil.getCurrentUserDetails()).get().addOnCompleteListener(task -> {

            if (task.getResult().getData() != null && FirebaseUtil.isLoggedIn()) {
                // Result: user is in database

                // set username in input (just for nicer ui)
                String username = task.getResult().getString("username");
                usernameInput.setText(username);
                usernameInput.setEnabled(false);

                // redirect to chat list
                Intent intent = new Intent(LoginUserActivity.this, ChatListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return;
            }

            // now wait for login button click to register user...
            setInProgress(false);
            return;
        });
    }

    /**
     * Sets whether the login process is in progress.
     */
    private void setInProgress(boolean inProgress) {
        loginButton.setEnabled(!inProgress);
        progressBar.setVisibility(inProgress ? ProgressBar.VISIBLE : ProgressBar.INVISIBLE);
    }
}