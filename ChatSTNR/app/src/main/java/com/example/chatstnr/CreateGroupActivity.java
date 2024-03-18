package com.example.chatstnr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.chatstnr.adapter.AddMemberGroupAdapter;
import com.example.chatstnr.adapter.UserlistAdapter;
import com.example.chatstnr.models.GroupModel;
import com.example.chatstnr.models.UserModel;
import com.example.chatstnr.utils.AndroidUtil;
import com.example.chatstnr.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CreateGroupActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;
    AddMemberGroupAdapter adapter;
    UserlistAdapter userlistAdapter;
    Button createGroup;
    EditText groupName;


    UserModel current;
    List<UserModel> selectedUsers = new ArrayList<>();
    List<UserModel> searchUsers = new ArrayList<>();

    public void addSelectedUser(UserModel user) {
        selectedUsers.add(user);
        setupSelectedUsersRecyclerView();
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        searchInput = findViewById(R.id.seach_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);
        createGroup = findViewById(R.id.create_group_button);
        groupName = findViewById(R.id.group_name_input);

        searchInput.requestFocus();

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
                    current = task.getResult().toObject(UserModel.class);
                });


        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        searchButton.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString();
            if(searchTerm.isEmpty() || searchTerm.length()<3){
                searchInput.setError("Invalid Username");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });

        createGroup.setOnClickListener(v -> {
            createNewGroup();
        });

    }

    private void createNewGroup() {
        if(groupName.getText().length() < 3){
            groupName.setError("Group name must be at least 3 characters");
            return;
        }

        Set<UserModel> uniqueUsers = new LinkedHashSet<>(selectedUsers);

        selectedUsers.clear();
        selectedUsers.addAll(uniqueUsers);

        // Check if the current user is not already in the selected users list, and add them if necessary
        boolean currentUserIncluded = false;
        for (UserModel user : selectedUsers) {
            if (Objects.equals(user.getUserId(), FirebaseUtil.currentUserid())) {
                currentUserIncluded = true;
                break;
            }
        }
        if (!currentUserIncluded) {
            // Add the current user to the selected users list
            selectedUsers.add(current);
        }

        if(selectedUsers.size() < 3){
            searchInput.setError("Group should have at least 3 users");
            return;
        }

        GroupModel newGroup = new GroupModel(
                Timestamp.now(), // Assuming you want to timestamp the group creation
                null, // Group ID will be generated automatically by Firestore
                groupName.getText().toString(), // Group name from the input field
                selectedUsers, // List of selected users for the group,
                null,
                null,
                null
        );

        CollectionReference groupsCollection = FirebaseUtil.getAllGroupDetails();

        groupsCollection.add(newGroup)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        String groupId = documentReference.getId();
                        newGroup.setGroupId(groupId);

                        documentReference.set(newGroup)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                        Intent intent = new Intent(getApplicationContext(), GroupChatActivity.class);
                                        AndroidUtil.passGroupModelAsIntent(intent,newGroup);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getApplicationContext().startActivity(intent);

                                        AndroidUtil.showToast(getApplicationContext(), "Group created successfully");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        AndroidUtil.showToast(getApplicationContext(), "Failed to update group with ID: " + groupId);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AndroidUtil.showToast(getApplicationContext(), "Failed to create group: " + e.getMessage());
                    }
                });

    }

    void setupSearchRecyclerView(String searchTerm){
        recyclerView.setVisibility(View.VISIBLE);
        searchInput.setText("");

        Query query = FirebaseUtil.allUserCollectionReference();

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        adapter = new AddMemberGroupAdapter(options, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        searchUsers.clear();

        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<UserModel> searchResults = task.getResult().toObjects(UserModel.class);

                for (UserModel user : searchResults) {
                    if (!selectedUsers.contains(user)) {
                        searchUsers.add(user);
                    }
                }

                AndroidUtil.showToast(getApplicationContext(), "Success " + task.getResult().size());


            } else {
                AndroidUtil.showToast(getApplicationContext(), "Failure");
            }
        });
    }

    void setupSelectedUsersRecyclerView() {
        RecyclerView usersRecyclerView = findViewById(R.id.users_recycler_view);
        userlistAdapter = new UserlistAdapter(selectedUsers, this);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(userlistAdapter);
    }

}
