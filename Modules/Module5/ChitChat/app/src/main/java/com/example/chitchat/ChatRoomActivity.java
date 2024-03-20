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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Adapters.ChatRoomAdapter;
import Models.AllMethods;
import Models.ChatRoom;
import Models.ChatRoomRes;
import Models.Message;
import Models.User;
import Fragments.OverlayMenuFragment;
import Models.UserRes;
import kotlin.reflect.KType;

public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener {


    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    ChatRoomAdapter adapter;
    User u;
    List<ChatRoom> chatrooms;

    RecyclerView rvChatRoom;

    FloatingActionButton addRoomButton;
    ChildEventListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room_activity);

        init();

    }

    private void  init( ){
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        u = new User();

        rvChatRoom = findViewById(R.id.chatroomrecyclerView);

        addRoomButton = findViewById(R.id.fabAddRoom);
        addRoomButton.setOnClickListener(this);
        chatrooms = new ArrayList<>();

    }

    @Override
    public void onClick(View v) {
        OverlayMenuFragment overlayMenuFragment = new OverlayMenuFragment();
        overlayMenuFragment.show(getSupportFragmentManager(), overlayMenuFragment.getTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return  true;
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

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("0","on start");



        final FirebaseUser firebaseUser = auth.getCurrentUser();
        u.setUid(firebaseUser.getUid());
        u.setEmail(firebaseUser.getEmail());

        database.getReference("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                u = snapshot.getValue(User.class);
                u.setUid(firebaseUser.getUid());
                AllMethods.name = u.getName();
                AllMethods.uId = firebaseUser.getUid();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        reference = database.getReference("chat_rooms");

        updatedUserRooms();



//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot sp:snapshot.getChildren()){
//                    ChatRoomRes room = sp.getValue(ChatRoomRes.class);
//
//                    for (ChatRoom.ChatRoomUser user : room.users
//                    ) {
//                        if(user.getUid().equals(firebaseUser.getUid())){
//                            ChatRoom room1 = room.getRoom();
//                            room1.setKey(sp.getKey());
//                            chatrooms.add(room1);
//                            displayMessages(chatrooms);
//                        }
//                    }
//
////                    for (Map.Entry<String,ArrayList<ChatRoom.ChatRoomUser>> u:room.users
////                    ) {
////
////
////
////
////                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        }
//
//        );

        displayMessages(chatrooms);

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
                for (ChatRoom.ChatRoomUser u :c.users
                ) {
                    if(u.getName().equals(AllMethods.name)){
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




    @Override
    protected void onResume() {
        super.onResume();
        Log.i("0","on resume");
        chatrooms = new ArrayList<>();
        updatedUserRooms();
        displayMessages(chatrooms);
    }

    private  void  displayMessages(List<ChatRoom> rooms){
        rvChatRoom.setLayoutManager(new LinearLayoutManager(ChatRoomActivity.this));
        rvChatRoom.setAdapter(null);
        adapter = new ChatRoomAdapter(rooms,ChatRoomActivity.this);
        rvChatRoom.setAdapter(adapter);
    }
}

