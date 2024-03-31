package com.example.chatapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapter.SelectUsersAdapter;
import com.example.chatapp.model.ChatroomModel;
import com.example.chatapp.model.UserModel;
import com.example.chatapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectUsersActivity extends AppCompatActivity {

    private EditText searchUsersInput;
    private ImageButton searchUsersBtn, clearSearchBtn;
    private RecyclerView selectUsersRecyclerView;
    private Button doneButton;
    private ImageButton backBtn;
    private List<UserModel> userList;
    private SelectUsersAdapter adapter;
    private EditText groupNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_users);
        groupNameInput = findViewById(R.id.group_name_input);
        searchUsersInput = findViewById(R.id.search_users_input);
        searchUsersBtn = findViewById(R.id.search_users_btn);
        clearSearchBtn = findViewById(R.id.clear_search_btn);
        selectUsersRecyclerView = findViewById(R.id.select_users_recycler_view);
        doneButton = findViewById(R.id.done_button);
        backBtn = findViewById(R.id.back_btn);

        userList = new ArrayList<>(); // Initialize empty list

        adapter = new SelectUsersAdapter(userList);
        selectUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectUsersRecyclerView.setAdapter(adapter);

        getUsersFromFirestore();

        searchUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement search functionality here
                String searchText = searchUsersInput.getText().toString().trim();
                // Filter userList based on searchText
                // Update adapter with filtered list
                // adapter.filter(searchText);
            }
        });
        backBtn.setOnClickListener(v -> {
            try {
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        clearSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchUsersInput.setText("");
                // Reset adapter with the original userList
                // adapter.reset();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = groupNameInput.getText().toString();
                if (groupName.isEmpty()) {
                    Toast.makeText(SelectUsersActivity.this, "Please enter a group name", Toast.LENGTH_SHORT).show();
                } else {
                    // Get the ID of the first selected user
                    String selectedUserId = adapter.getSelectedUsers().get(0).getUserId();

                    // Generate a new chatroom ID
                    String newChatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), selectedUserId);

                    // Create a new chatroom model
                    ChatroomModel newChatroom = new ChatroomModel(
                            newChatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), selectedUserId),
                            Timestamp.now(),
                            "", // You might leave these fields empty initially
                            "",
                            ""
                    );

                    // Save the new chatroom to Firestore
                    FirebaseUtil.getChatroomReference(newChatroomId)
                            .set(newChatroom)
                            .addOnSuccessListener(aVoid -> {
                                // Chatroom created successfully, open ChatActivity
                                Intent intent = new Intent(SelectUsersActivity.this, ChatActivity.class);
                                intent.putExtra("chatroom_id", newChatroomId);
                                intent.putExtra("group_name", groupName);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                // Error handling if chatroom creation fails
                                Toast.makeText(SelectUsersActivity.this, "Failed to create chatroom", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });

    }
    // Method to create a new chatroom in Firestore
    private void createNewChatroom(String chatroomId, String groupName, String recipientUserId) {
        // Get a reference to the chatrooms collection
        CollectionReference chatroomsRef = FirebaseFirestore.getInstance().collection("chatrooms");

        // Create a new ChatroomModel object
        ChatroomModel chatroom = new ChatroomModel(chatroomId, Arrays.asList(FirebaseUtil.currentUserId(), recipientUserId), Timestamp.now(), FirebaseUtil.currentUserId(), null, null);

        // Add the chatroom document to Firestore
        chatroomsRef.document(chatroomId).set(chatroom)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Chatroom created successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to create chatroom", e);
                    }
                });
    }


    // Method to fetch users from Firestore
    private void getUsersFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseUtil.currentUserId(); // Replace this with your method to get the current user's ID

        db.collection("users")
                .whereNotEqualTo("userId", currentUserId) // Exclude the current user's document
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                UserModel user = document.toObject(UserModel.class);
                                userList.add(user);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(SelectUsersActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
