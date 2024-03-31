package com.example.mychatapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mychatapplication.adapters.UsersAdapter;
import com.example.mychatapplication.databinding.ActivityUsersBinding;
import com.example.mychatapplication.listeners.UserListener;
import com.example.mychatapplication.models.Message;
import com.example.mychatapplication.models.User;
import com.example.mychatapplication.utilities.Constants;
import com.example.mychatapplication.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;
    private String createType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            createType = intent.getStringExtra("createType");
            if(createType.equals("group")){
                binding.done.setVisibility(View.VISIBLE);
            }
        }

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentuserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null){
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                            if (currentuserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            User user = queryDocumentSnapshot.toObject(User.class);
                            user.setId(queryDocumentSnapshot.getId());
                            users.add(user);
                        }
                        if (users.size() > 0){
                            UsersAdapter usersAdapter = new UsersAdapter(users, this,createType);
                            binding.userRecyclerView.setAdapter(usersAdapter);
                            binding.userRecyclerView.setVisibility(View.VISIBLE);
                            binding.done.setOnClickListener(v -> {
                                List<String> selectedUsers = usersAdapter.getSelectedUsers();
                                if (selectedUsers.isEmpty()) {
                                    Toast.makeText(this, "Please select at least one user", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(UsersActivity.this,CreateGroupActivity.class);
                                    intent.putStringArrayListExtra(Constants.KEY_SELECTED_USERS, new ArrayList<>(selectedUsers));
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        intent.putExtra("chatType","user");
        startActivity(intent);
        finish();
    }
}