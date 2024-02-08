package com.example.whatsdown;

import static com.example.whatsdown.Constants.BASE_URL;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.whatsdown.model.CreateGroupChat;
import com.example.whatsdown.model.GroupChat;
import com.example.whatsdown.model.User;
import com.example.whatsdown.requests.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatsActivity extends AppCompatActivity {
    /*
     * Jonas Wagner - 1315578
     */
    private LinearLayout userListContainer;
    private List<User> userList;
    private ApiService apiService;
    private final List<Integer> selectedUserIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        userListContainer = findViewById(R.id.userListContainer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        User selectedUser = (User) getIntent().getSerializableExtra("selectedUser");
        assert selectedUser != null;
        getUsersFromApi(selectedUser);
        getGroupsFromApi(selectedUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new_group_chat) {
            showCreateGroupDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showCreateGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_group, null);
        builder.setView(dialogView);

        EditText editTextGroupName = dialogView.findViewById(R.id.editTextGroupName);
        Button buttonCreateGroup = dialogView.findViewById(R.id.buttonCreateGroup);
        userListContainer = dialogView.findViewById(R.id.userListContainer);

        User currentUser = (User) getIntent().getSerializableExtra("selectedUser");

        List<User> users = userList;
        for (User user : users) {
            assert currentUser != null;
            if (user.getUserId() != currentUser.getUserId()) {
                addUserToDialog(user);
            }
        }

        AlertDialog dialog = builder.create();
        dialog.show();

        buttonCreateGroup.setOnClickListener(v -> {
            String groupName = editTextGroupName.getText().toString().trim();

            if (groupName.isEmpty()) {
                editTextGroupName.setError("Group name is required");
                editTextGroupName.requestFocus();
                return;
            }

            // Get the list of selected user IDs
            List<Integer> selectedUserIds = getSelectedUserIds();

            if (selectedUserIds.isEmpty()) {
                Toast.makeText(ChatsActivity.this, "Please select at least one user", Toast.LENGTH_SHORT).show();
                return;
            }

            // Make the API call to create a group chat
            CreateGroupChat createGroupChat = new CreateGroupChat(groupName, selectedUserIds);
            System.out.println(createGroupChat.getName() + " " + createGroupChat.getMemberIds());
            Call<GroupChat> call = apiService.createGroupChat(createGroupChat);
            call.enqueue(new Callback<GroupChat>() {
                @Override
                public void onResponse(Call<GroupChat> call, Response<GroupChat> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ChatsActivity.this, "Group created successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatsActivity.this, "Failed to create group chat", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<GroupChat> call, Throwable t) {
                    Toast.makeText(ChatsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        });
    }

    private void addUserToDialog(User user) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 8, 0, 8);
        textView.setLayoutParams(layoutParams);
        textView.setText(user.getName());
        textView.setTextSize(18);
        textView.setPadding(16, 16, 16, 16);
        textView.setBackgroundResource(R.drawable.user_item_background);

        textView.setOnClickListener(v -> {
            toggleUserSelection(textView, user.getUserId());
        });

        userListContainer.addView(textView);
    }

    private void addUserItem(final User user, final User selectedUser) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 8, 0, 8);
        textView.setLayoutParams(layoutParams);
        textView.setText(user.getName());
        textView.setTextSize(18);
        textView.setPadding(16, 16, 16, 16);
        textView.setBackgroundResource(R.drawable.user_item_background);

        textView.setOnClickListener(v -> onItemClick(user, selectedUser));

        userListContainer.addView(textView);
    }

    private void toggleUserSelection(TextView textView, int userId) {
        if (textView.getTag() == null || !(boolean) textView.getTag()) {
            textView.setBackgroundColor(Color.LTGRAY);
            textView.setTag(true);
            selectedUserIds.add(userId);
        } else {
            textView.setBackgroundResource(R.drawable.user_item_background);
            textView.setTag(false);
            selectedUserIds.remove((Integer) userId);
        }
    }

    private List<Integer> getSelectedUserIds() {
        return selectedUserIds;
    }

    private void getUsersFromApi(User loggedInUser) {
        Call<List<User>> call = apiService.getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    userList = response.body();
                    for (User user : userList) {
                        if (user.getUserId() != loggedInUser.getUserId()) {
                            addUserItem(user, loggedInUser);
                        }
                    }
                } else {
                    System.out.println("Error: " + response.message());
                    Toast.makeText(ChatsActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ChatsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getGroupsFromApi(User loggedInUser) {
        System.out.println("Fetching groups for user: " + loggedInUser.getUserId());
        Call<List<GroupChat>> groupsCall = apiService.getGroupChats(loggedInUser.getUserId());
        groupsCall.enqueue(new Callback<List<GroupChat>>() {
            @Override
            public void onResponse(Call<List<GroupChat>> call, Response<List<GroupChat>> response) {
                if (response.isSuccessful()) {
                    List<GroupChat> groupList = response.body();
                    for (GroupChat group : groupList) {
                        addGroupItem(group);
                    }
                } else {
                    System.out.println("Error fetching groups: " + response.message());
                    Toast.makeText(ChatsActivity.this, "Error fetching groups: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GroupChat>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ChatsActivity.this, "Error fetching groups: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addGroupItem(GroupChat group) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 8, 0, 8);
        textView.setLayoutParams(layoutParams);
        textView.setText(group.getName());
        textView.setTextSize(18);
        textView.setPadding(16, 16, 16, 16);
        textView.setBackgroundResource(R.drawable.user_item_background);

        // You can add click listeners or other interactions for group items here
        textView.setOnClickListener(v -> onItemClickGroup(group, (User) getIntent().getSerializableExtra("selectedUser")));

        userListContainer.addView(textView);
    }

    private void onItemClick(User user, User loggedInUser) {
        Intent intent = new Intent(ChatsActivity.this, MainActivity.class);
        intent.putExtra("selectedUser", user);
        intent.putExtra("loggedInUser", loggedInUser);
        startActivity(intent);
    }

    private void onItemClickGroup(GroupChat group, User loggedInUser) {
        Intent intent = new Intent(ChatsActivity.this, GroupChatsActivity.class);
        intent.putExtra("selectedGroupChat", group);
        intent.putExtra("loggedInUser", loggedInUser);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.onBackPressed();
        return true;
    }

}
