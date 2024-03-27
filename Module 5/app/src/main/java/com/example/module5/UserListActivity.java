package com.example.module5;

import static com.example.module5.RegisterActivity.MY_PREFS_NAME;
import static com.example.module5.RegisterActivity.pwd_field;
import static com.example.module5.RegisterActivity.user_field;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.module5.adapter.UserAdapter;
import com.example.module5.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListActivity extends AppCompatActivity implements UserAdapter.StartChat{

    private RecyclerView messagesRecyclerView;
    private UserAdapter userAdapter;
    List<User> users = new ArrayList<>();

    // Initialize Firebase components
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsersRef;

    private TextView name;

    String userId;

    private static final int REQUEST_READ_STORAGE_PERMISSION = 1;

    ImageView add_btn;

    List<String> groupUsers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        name = findViewById(R.id.name);
        add_btn = findViewById(R.id.add_btn);

        // Initialize Firebase Auth and Database instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mDatabase.getReference("users");

        // Check if the permission is not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat
                .checkSelfPermission(UserListActivity.this
                        ,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserListActivity.this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },REQUEST_READ_STORAGE_PERMISSION);
        }

        // Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Assuming you want to retrieve details for the currently logged in user
            userId = currentUser.getUid();

            // Retrieve user details from Firebase Realtime Database
            mUsersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get user details
                    String userName = dataSnapshot.child("userName").getValue(String.class);
                    // Do something with the user details
                    Log.d("MainActivity", "User name: " + userName);

                    name.setText(userName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                    Log.e("MainActivity", "Error fetching user details", databaseError.toException());
                }
            });
        }



        fetchUsers();

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroupDilog();
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your code

            } else {
                // Permission denied, show a message or handle it accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                // You might want to disable functionality that requires this permission
            }
        }
    }


    private void fetchUsers() {
        users.clear();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getUserType() != null && user.getUserType().equals("group")){
                        if (user.getMembers().containsKey(userId)){
                            users.add(user);
                        }
                    }
                    else {
                        if (!user.getUserId().equals(userId)) {
                            users.add(user);
                        }
                    }


                }
                // Update your adapter with this list
                userAdapter = new UserAdapter(users,UserListActivity.this,0);
                messagesRecyclerView.setLayoutManager(new LinearLayoutManager(UserListActivity.this));
                messagesRecyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    public void logOut(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(UserListActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Are you sure want to LogOut "+"?"+"\n\nPress ok to be continue.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        mAuth.signOut();
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString(user_field,null);
                        editor.putString(pwd_field,null );
                        editor.apply();

                        Intent intent = new Intent(UserListActivity.this,RegisterActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();


                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void startChat(String user2Id, String group) {
        if (group != null){
            String roomID = createChatRoom(null,null, user2Id);
            startActivity(new Intent(UserListActivity.this, MainActivity.class).putExtra("roomId", roomID));
        }
        else {
            String roomID = createChatRoom(userId, user2Id,null);
            startActivity(new Intent(UserListActivity.this, MainActivity.class).putExtra("roomId", roomID));

        }
    }



    // Method to create a chat room
    private String createChatRoom(String userId1, String userId2,String group) {
        if (group == null) {
            // Sort user IDs alphabetically to ensure consistency
            String[] sortedIds = {userId1, userId2};
            Arrays.sort(sortedIds);
            String chatRoomId = sortedIds[0] + "_" + sortedIds[1];
            DatabaseReference chatRoomRef = mDatabase.getReference("chats").child(chatRoomId);
            chatRoomRef.setValue(true); // Create an entry for the chat room
            return chatRoomId;
        }
        else {
            DatabaseReference chatRoomRef = mDatabase.getReference("chats").child(group);
            chatRoomRef.setValue(true); // Create an entry for the chat room
            return group;
        }


    }

    Dialog groupDialog;
    private void createGroupDilog() {

        groupUsers.clear();

        groupDialog = new Dialog(UserListActivity.this);
        groupDialog.setContentView(R.layout.dialog_create_group);
        groupDialog.setCancelable(true);
        groupDialog.setCanceledOnTouchOutside(true);
        groupDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        groupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView back_btn = groupDialog.findViewById(R.id.back_btn);
        EditText group_name = groupDialog.findViewById(R.id.group_name);
        Button create_group = groupDialog.findViewById(R.id.create_group);
        RecyclerView groupRecyclerView = groupDialog.findViewById(R.id.groupRecyclerView);

        List<User> userList = new ArrayList<>();
        userList.clear();
        for (User u:users){
            if (!(u.getUserType() != null && u.getUserType().equals("group"))){
                userList.add(u);
            }
        }

        UserAdapter userAdapter2 = new UserAdapter(userList,UserListActivity.this,1);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(UserListActivity.this));
        groupRecyclerView.setAdapter(userAdapter2);




        create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupStr = group_name.getText().toString().trim();

                if (groupStr.isEmpty()){
                    group_name.setError("Enter Name");
                    group_name.requestFocus();
                    return;
                }
                if (!(groupUsers.size() >0)){
                    Toast.makeText(UserListActivity.this, "Select Member ! ", Toast.LENGTH_SHORT).show();
                    return;
                }
                groupDialog.dismiss();
                submiteGroup(groupStr);
            }
        });



        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupDialog.dismiss();
            }
        });


        groupDialog.show();

    }

    private void submiteGroup(String groupStr) {

        // Now store the user name and initialize groups
        DatabaseReference dtRef = FirebaseDatabase.getInstance().getReference("users");
        String groupId = dtRef.push().getKey();
        if (groupId != null) {
            DatabaseReference userRef = dtRef.child(groupId);
            Map<String, Object> userData = new HashMap<>();
            userData.put("userName", groupStr);
            userData.put("userId", groupId);
            userData.put("userType", "group");
            Map<String, Boolean> members = new HashMap<>();
            groupUsers.add(userId);
            for (String selectedUserId : groupUsers) {
                members.put(selectedUserId, true); // Add selected users as members
            }
            userData.put("members", members);
            userRef.setValue(userData);
            fetchUsers();

        }



    }

    @Override
    public void selectUser(String userID) {
        if (groupUsers.contains(userID)){
            groupUsers.remove(userID);
        }
        else {
            groupUsers.add(userID);
        }
    }


}