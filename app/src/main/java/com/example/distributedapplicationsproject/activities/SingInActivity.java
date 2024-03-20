package com.example.distributedapplicationsproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.distributedapplicationsproject.R;
import com.example.distributedapplicationsproject.android.UserArrayAdapter;
import com.example.distributedapplicationsproject.firebase.DatabaseService;
import com.example.distributedapplicationsproject.models.User;
import com.example.distributedapplicationsproject.utils.DataShare;

import java.util.List;
import java.util.stream.Collectors;

public class SingInActivity extends AppCompatActivity {

    Spinner userSpinner;

    ProgressBar userLoadedProgress;

    Button loginButton;

    List<User> userList;

    DatabaseService databaseService = DatabaseService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        userSpinner = findViewById(R.id.spinner_users);
        userLoadedProgress = findViewById(R.id.progress_user_loaded);
        loginButton = findViewById(R.id.buttonLogin);

        databaseService.getUsers(new DatabaseService.OnUsersDataListener() {
            @Override
            public void onUsersDataLoaded(List<User> userList) {
                // Handle retrieved user list
                initSpinner(userList);
            }

            @Override
            public void onUsersDataError(Exception e) {
                Log.d("firebase", e.toString());
            }
        });
    }

    public void initSpinner(List<User> users) {
        this.userList = users;

        UserArrayAdapter userAdapter = new UserArrayAdapter(this, android.R.layout.simple_spinner_item, userList);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        userSpinner.setAdapter(userAdapter);

        userSpinner.setVisibility(View.VISIBLE);
        userLoadedProgress.setVisibility(View.INVISIBLE);
        loginButton.setEnabled(true);
    }

    public void onClickLogin(View view) {
        User selectedUser = (User) this.userSpinner.getSelectedItem();
        DataShare.getInstance().setCurrentUser(selectedUser);

        Toast.makeText(this, selectedUser.getName(), Toast.LENGTH_SHORT).show();

        User user1 = userList.stream().filter(user -> user.getName().equals("Nick")).collect(Collectors.toList()).get(0);
        User user2 = userList.stream().filter(user -> user.getName().equals("Olaf")).collect(Collectors.toList()).get(0);
        User user3 = userList.stream().filter(user -> user.getName().equals("Test")).collect(Collectors.toList()).get(0);
        databaseService.createPrivateChat(user1, user2);

//        databaseService.createGroupChat("Group 1", user1, user2);
//        databaseService.createGroupChat("Group 2", user1, user2);
//        databaseService.createGroupChat("Group Demo", user1, Arrays.asList(user1, user2, user3));

        startActivity(new Intent(this, HomeActivity.class));
    }
}
