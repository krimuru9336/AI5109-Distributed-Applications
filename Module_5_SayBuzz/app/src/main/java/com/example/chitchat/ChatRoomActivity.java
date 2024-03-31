package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.ChatRoomAdapter;
import Models.AllMethods;
import Models.ChatRoom;
import Models.ChatRoomRes;
import Models.User;
import Fragments.OverlayMenuFragment;

public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener {


    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    FloatingActionButton floatingButtomAdd;
    List<ChatRoom> chatrooms;
    ChildEventListener listener;
    ChatRoomAdapter adapter;
    User user;
    

    RecyclerView chatRoomRecyclerView;


    private void  init( ){
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = new User();

        chatRoomRecyclerView = findViewById(R.id.chatRoomRV);

        floatingButtomAdd = findViewById(R.id.NewButton);
        floatingButtomAdd.setOnClickListener(this);
        chatrooms = new ArrayList<>();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        init();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return  true;
    }

    @Override
    public void onClick(View v) {
        OverlayMenuFragment overlayMenuFragment = new OverlayMenuFragment();
        overlayMenuFragment.show(getSupportFragmentManager(), overlayMenuFragment.getTag());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("0","on start");



        final FirebaseUser firebaseUser = auth.getCurrentUser();
        user.setUid(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());

        database.getReference("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                user.setUid(firebaseUser.getUid());
                AllMethods.name = user.getName();
                AllMethods.uId = firebaseUser.getUid();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference = database.getReference("chat_rooms");

        updatedUserRooms();
        displayMessages(chatrooms);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout){
            auth.signOut();
            finish();
            UserPreferences preferences = new UserPreferences(this);
            preferences.clearCredentials();
            Intent i = new Intent(ChatRoomActivity.this,MainActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
    void updatedUserRooms(){
        if(listener != null && reference != null){
            reference.removeEventListener(listener);
        }

        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatRoomRes c = snapshot.getValue(ChatRoomRes.class);
                c.setKey(snapshot.getKey());
                for (ChatRoom.ChatRoomUser user :c.users
                ) {
                    if(user.getName().equals(AllMethods.name)){
                        chatrooms.add(c.getRoom());
                        break;
                    }
                }
                displayMessages(chatrooms);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addChildEventListener(listener);
    }


    private  void  displayMessages(List<ChatRoom> rooms){
        chatRoomRecyclerView.setLayoutManager(new LinearLayoutManager(ChatRoomActivity.this));
        chatRoomRecyclerView.setAdapter(null);
        adapter = new ChatRoomAdapter(rooms,ChatRoomActivity.this);
        chatRoomRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("0","on resume");
        chatrooms = new ArrayList<>();
        updatedUserRooms();
        displayMessages(chatrooms);
    }
}

