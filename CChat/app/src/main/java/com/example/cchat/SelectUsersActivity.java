package com.example.cchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.cchat.adapter.AddUsersRecyclerAdapter;
import com.example.cchat.adapter.ChatRecyclerAdapter;
import com.example.cchat.adapter.RecentChatRecyclerAdapter;
import com.example.cchat.adapter.SearchUserRecyclerAdapter;
import com.example.cchat.model.AddUsersModel;
import com.example.cchat.model.ChatRoomModel;
import com.example.cchat.model.UserModel;
import com.example.cchat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class SelectUsersActivity extends AppCompatActivity implements AddUsersRecyclerAdapter.OnClickListener {

    ImageButton backBtn;
    RecyclerView recyclerView;
    Button createGrpBtn;
    AddUsersRecyclerAdapter adapter;

    ArrayList<String> selectedUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_users);

        backBtn = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.add_users_recycler_row);
        createGrpBtn = findViewById(R.id.select_users_btn);
        createGrpBtn.setVisibility(View.GONE);

        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });

        createGrpBtn.setOnClickListener((v) -> {
            Intent intent = new Intent(SelectUsersActivity.this, CreateGroupActivity.class);
            selectedUsers.add(FirebaseUtil.currentUserId());
            intent.putExtra("selectedUsers", selectedUsers);
            startActivity(intent);
        });

        setupRecyclerView();
    }

    void setupRecyclerView() {
        try {
            Log.i("activity", "here");
            Query query = FirebaseUtil.allChatRoomCollectionReference()
                    .whereArrayContains("userIds", FirebaseUtil.currentUserId())
                    .whereEqualTo("chatroomType", "p2p")
                    .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

            FirestoreRecyclerOptions<ChatRoomModel> options = new FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                    .setQuery(query, ChatRoomModel.class).build();

            adapter = new AddUsersRecyclerAdapter(options, getApplicationContext(), this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        } catch (Error err) {
            Log.e("error", String.valueOf(err));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onUserSelected(ArrayList<String> users) {
        selectedUsers = users;
        if(users.size() > 0) {
            createGrpBtn.setVisibility(View.VISIBLE);
        } else {
            createGrpBtn.setVisibility(View.GONE);
        }
    }
}