package com.example.chitchat;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Adapters.MessageAdapter;
import Models.AllMethods;
import Models.ChatRoom;
import Models.Message;
import Models.Message.MessageType;
import Models.MessageRes;
import Models.User;

public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener {


    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    MessageAdapter adapter;
    User u;
    ArrayList<Message> messages;

    RecyclerView rvMessage;
    ObjectMapper objectMapper = new ObjectMapper();
    EditText uInput;
    ImageButton sendBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        init();

    }

    private void  init( ){
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        u = new User();

        rvMessage = findViewById(R.id.messages);
        uInput = findViewById(R.id.userMessage);
        sendBtn = findViewById(R.id.SendButton);
        sendBtn.setOnClickListener(this);
        messages = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        if(!TextUtils.isEmpty(uInput.getText())){
            Date currentDate = new Date();

            // Format the date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDateTime = dateFormat.format(currentDate);
            Message message = new Message(uInput.getText().toString(), formattedDateTime,u.getName(),AllMethods.uId, Message.MessageType.Text);
            uInput.setText("");

            reference.push().setValue(message);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

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


        reference = database.getReference("chat_rooms").child(AllMethods.CurrentSelectedRoom.getKey()).child("messages");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                ArrayList<HashMap<String,Object>> m = (ArrayList<HashMap<String, Object>>) snapshot.getValue();
//                messages = new ArrayList<>();
//                for (HashMap<String,Object> map:m
//                     ) {
//                    messages.add(objectMapper.convertValue(map,Message.class));
//                }
//                displayMessages(messages);
                Message m = snapshot.getValue(Message.class);
                messages.add(m);
                displayMessages(messages);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                ArrayList<HashMap<String,Object>> m = (ArrayList<HashMap<String, Object>>) snapshot.getValue();
//                int i = 0,count = 0;
//                for (Message map:messages
//                ) {
//                    Message newM =objectMapper.convertValue(map,Message.class);
//                    if(map.getKey().equals(newM.getKey())){
//                        count = i;
//                        break;
//                    }
//                    i++;
//                }
//                messages.remove(count);
//                assert m != null;
//                messages.add(count,objectMapper.convertValue(m.get(0),Message.class));
//
//                displayMessages(messages);


//                ArrayList<HashMap<String,Object>> m = (ArrayList<HashMap<String, Object>>) snapshot.getValue();
//                messages = new ArrayList<>();
//                for (HashMap<String,Object> map:m
//                ) {
//                    messages.add(objectMapper.convertValue(map,Message.class));
//                }
//                displayMessages(messages);

                int i = 0,count = 0;
                Message m = snapshot.getValue(Message.class);
                for (Message marr : messages){
                    if(marr.getKey().equals(m.getKey())){
                        count = i;
                    }
                    i++;
                }
                messages.remove(count);
                messages.add(count,m);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

//                ArrayList<HashMap<String,Object>> m = (ArrayList<HashMap<String, Object>>) snapshot.getValue();
//                int i = 0,count = 0;
//                for (Message map:messages
//                ) {
//                    Message newM =objectMapper.convertValue(map,Message.class);
//                    if(map.getKey().equals(newM.getKey())){
//                        count = i;
//                        break;
//                    }
//                    i++;
//                }
//                messages.remove(count);
//                displayMessages(messages);


//                ArrayList<HashMap<String,Object>> m = (ArrayList<HashMap<String, Object>>) snapshot.getValue();
//                messages = new ArrayList<>();
//                for (HashMap<String,Object> map:m
//                ) {
//                    messages.add(objectMapper.convertValue(map,Message.class));
//                }
//                displayMessages(messages);

                int i = 0,count = 0;
                Message m = snapshot.getValue(Message.class);
                for (Message marr : messages){
                    if(marr.getKey().equals(m.getKey())){
                        count = i;
                    }
                    i++;
                }
                messages.remove(count);
//                messages.add(count,m);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        messages = new ArrayList<>();
    }

    private  void  displayMessages(ArrayList<Message> messages){
        rvMessage.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
        adapter = new MessageAdapter(GroupChatActivity.this,messages,reference);
        rvMessage.setAdapter(adapter);

    }
}

