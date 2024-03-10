package com.example.whatsdown;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton sendMessageButton;
    private EditText userMessage;
    private ScrollView mScrollView;
    private TextView displayTextMessages;
    private FirebaseAuth mAuth;
    private DatabaseReference usersReference, groupNameReference, groupMessageKeyReference;
    private String groupName, userId, name, date, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        InitializeFields();
        GetUserInfo();

        sendMessageButton.setOnClickListener(sendMessageButtonOnClick);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        groupNameReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String s)
            {
                if (snapshot.exists())
                {
                    DisplayMessages(snapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String s)
            {
                if (snapshot.exists())
                {
                    DisplayMessages(snapshot);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void InitializeFields() {
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        groupName = getIntent().getExtras().get("groupName").toString();

        usersReference = FirebaseDatabase
                .getInstance("https://whatsdown-7baba-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference()
                .child("Users");

        groupNameReference = FirebaseDatabase
                .getInstance("https://whatsdown-7baba-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference()
                .child("Groups")
                .child(groupName);

        mToolbar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(groupName);

        sendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        userMessage = (EditText) findViewById(R.id.input_group_message);
        displayTextMessages = (TextView) findViewById(R.id.group_chat_text_display);
        mScrollView = (ScrollView) findViewById(R.id.my_scroll_view);
    }

    private void GetUserInfo() {
        usersReference.child(userId).addValueEventListener(GetUserInfoListener);
    }

    private final ValueEventListener GetUserInfoListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot)
        {
            if (snapshot.exists())
            {
                name = snapshot.child("name").getValue().toString();
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private final View.OnClickListener sendMessageButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            SaveMessage();
            userMessage.setText("");
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };

    private void SaveMessage()
    {
        String message = userMessage.getText().toString();
        String messageKey = groupNameReference.push().getKey();

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "You haven't written anything", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            date = dateFormat.format(calendar.getTime());

            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            time = timeFormat.format(calendar.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupNameReference.updateChildren(groupMessageKey);

            groupMessageKeyReference = groupNameReference.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", name);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", date);
            messageInfoMap.put("time", time);
            groupMessageKeyReference.updateChildren(messageInfoMap);
        }
    }

    private void DisplayMessages(DataSnapshot dataSnapshot)
    {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while(iterator.hasNext())
        {
            String date = (String) ((DataSnapshot)iterator.next()).getValue();
            String message = (String) ((DataSnapshot)iterator.next()).getValue();
            String name = (String) ((DataSnapshot)iterator.next()).getValue();
            String time = (String) ((DataSnapshot)iterator.next()).getValue();

            displayTextMessages.append(name + ":\n" + message + "\n" + time + " " + date + "\n\n\n");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}