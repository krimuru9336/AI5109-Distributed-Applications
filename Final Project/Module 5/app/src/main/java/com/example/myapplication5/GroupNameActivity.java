package com.example.myapplication5;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GroupNameActivity extends AppCompatActivity {
    EditText groupNameInput;
    TextView createButton, previousButton;
    TextView membercount;

            

    public void onPreviousTextViewClick(View view) {
        getOnBackPressedDispatcher().onBackPressed();
//        startActivity(new Intent(GroupNameActivity.this,CreateGroupActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("in group name activity");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_group);

        System.out.println("in group name activity");

        previousButton = findViewById(R.id.previous_textview);

        createButton = findViewById(R.id.create_textview);
        createButton.setEnabled(false);

        groupNameInput = findViewById(R.id.group_name_input);

        membercount = findViewById(R.id.selected_members_count);

        System.out.println("above json stuff");

        // Retrieve the passed map
//        String selectedUsersJson = getIntent().getStringExtra("selectedUsers");
//        Gson gson = new Gson();
//        Type type = new TypeToken<Map<String, String>>(){}.getType();
//        Map<String, String> selectedMembersMap = gson.fromJson(selectedUsersJson, type);
//
//        System.out.println("in selectedMembersMap" + selectedMembersMap);
//
//// If you need to display the count of selected members
//        membercount.setText("Members Selected: " + selectedMembersMap.size());

//
//        // Retrieve the passed list -> original
//        String selectedUsersJson = getIntent().getStringExtra("selectedUsers");
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<String>>(){}.getType();
//        List<String> selectedMembersIDs = gson.fromJson(selectedUsersJson, type);
//
//        System.out.println("in selectedMembersIDs" + selectedMembersIDs);
//
//        membercount.setText("Members Selected: " + selectedMembersIDs.size());


//        createButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onCreateTextViewClick(v, selectedMembersIDs);
//            }
//        });

        // Retrieve the passed map
        String selectedUsersJson = getIntent().getStringExtra("selectedUsers");
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> selectedMembersMap = gson.fromJson(selectedUsersJson, type);

        System.out.println("in selectedMembersMap" + selectedMembersMap);

        membercount.setText("Members Selected: " + selectedMembersMap.size());
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateTextViewClick(v, selectedMembersMap);
            }
        });



        groupNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable the "Create" button if the group name is not empty
                createButton.setEnabled(s.length() > 3);  //atleast 3 characters are entered
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });




        

    }



    public void onCreateTextViewClick(View view, Map<String, String> selectedMembersMap) {
        // Retrieve the group name from the EditText
        String groupName = groupNameInput.getText().toString().trim();

        if (!groupName.isEmpty()) {
            // Create a reference to a document with an auto-generated ID
            DocumentReference newGroupRef = FirebaseFirestore.getInstance().collection("groupChats").document();

            // Prepare the data to be stored, including the document ID as the group ID
            Map<String, Object> data = new HashMap<>();
            data.put("groupName", groupName);
            data.put("id", newGroupRef.getId()); // Store the document ID as the group ID

            // Convert the selectedMembersMap into a list of maps for Firestore
            List<Map<String, String>> membersList = new ArrayList<>();
            for (Map.Entry<String, String> entry : selectedMembersMap.entrySet()) {
                Map<String, String> memberMap = new HashMap<>();
                memberMap.put("id", entry.getKey());
                memberMap.put("name", entry.getValue());
                membersList.add(memberMap);
            }
            data.put("members", membersList);

            data.put("isGroupChat", true);

            // Extract the selected member IDs and add them to the data
            List<String> memberIDs = new ArrayList<>(selectedMembersMap.keySet());
            data.put("memberIDs", memberIDs); // Add memberIDs to the data being stored

//            data.put()

            // Set the data at the document reference
            newGroupRef.set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(GroupNameActivity.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(GroupNameActivity.this, MainActivity.class);
                            intent.putExtra("isGroupChat", true);

                            // Extract the selected member IDs and pass them as an extra
                            List<String> memberIDs = new ArrayList<>(selectedMembersMap.keySet());
//                            intent.putStringArrayListExtra("memberIDs", new ArrayList<>(memberIDs));

                            startActivity(intent);
                            // finish the current activity to prevent the user from going back to it
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the error
                        }
                    });
        } else {
            // Show an error message if the group name is empty
            groupNameInput.setError("Group name cannot be empty");
            Toast.makeText(GroupNameActivity.this, "Group name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }



//
//    public void onCreateTextViewClick(View view, Map<String, String> selectedMembersMap) {
//        // Retrieve the group name from the EditText
//        String groupName = groupNameInput.getText().toString().trim();
//
//        if (!groupName.isEmpty()) {
//            // Create a reference to a document with an auto-generated ID
//            DocumentReference newGroupRef = FirebaseFirestore.getInstance().collection("groupChats").document();
//
//            // Prepare the data to be stored, including the document ID as the group ID
//            Map<String, Object> data = new HashMap<>();
//            data.put("groupName", groupName);
//            data.put("id", newGroupRef.getId()); // Store the document ID as the group ID
//
//            // Convert the selectedMembersMap into a list of maps for Firestore
//            List<Map<String, String>> membersList = new ArrayList<>();
//            for (Map.Entry<String, String> entry : selectedMembersMap.entrySet()) {
//                Map<String, String> memberMap = new HashMap<>();
//                memberMap.put("id", entry.getKey());
//                memberMap.put("name", entry.getValue());
//                membersList.add(memberMap);
//            }
//            data.put("members", membersList);
//
//            data.put("isGroupChat", true);
//
//            // Set the data at the document reference
//            newGroupRef.set(data)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(GroupNameActivity.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(GroupNameActivity.this, MainActivity.class);
//                            intent.putExtra("isGroupChat", true);
//                            startActivity(intent);
//                            // finish the current activity to prevent the user from going back to it
//                            finish();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Handle the error
//                        }
//                    });
//        } else {
//            // Show an error message if the group name is empty
//            groupNameInput.setError("Group name cannot be empty");
//            Toast.makeText(GroupNameActivity.this, "Group name cannot be empty", Toast.LENGTH_SHORT).show();
//        }
//    }


//    public void onCreateTextViewClick(View view, List<String> selectedMembersIDs) {
//        // Retrieve the group name from the EditText
//        String groupName = groupNameInput.getText().toString().trim();
//
//        if (!groupName.isEmpty()) {
//            // Create a reference to a document with an auto-generated ID
//            DocumentReference newGroupRef = FirebaseFirestore.getInstance().collection("groupChats").document();
//
//            // Prepare the data to be stored, including the document ID as the group ID
//            Map<String, Object> data = new HashMap<>();
//            data.put("groupName", groupName);
//            data.put("id", newGroupRef.getId()); // Store the document ID as the group ID
//            data.put("members", selectedMembersIDs);
//            data.put("isGroupChat", true);
//
//            // Set the data at the document reference
//            newGroupRef.set(data)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(GroupNameActivity.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(GroupNameActivity.this, MainActivity.class);
////                            intent.putExtra("id", newGroupRef.getId()); // Pass the document ID to the next activity
//                            intent.putExtra("isGroupChat", true);
//                            startActivity(intent);
//                            // finish the current activity to prevent the user from going back to it
//                            finish();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Handle the error
//                        }
//                    });
//        } else {
//            // Show an error message if the group name is empty
//            groupNameInput.setError("Group name cannot be empty");
//        }
//    }
//


//    public void onCreateTextViewClick(View view, List<String> selectedMembersIDs) {
//        // Retrieve the group name from the EditText
//        String groupName = groupNameInput.getText().toString().trim();
//
//
//        if (!groupName.isEmpty()) {
//            //store the group in db
//            String uniqueId = UUID.randomUUID().toString();
//            GroupChatroomModel groupChat = new GroupChatroomModel(uniqueId, groupName, selectedMembersIDs);
//
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            db.collection("groupChats").add(groupChat)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//
//                            Toast.makeText(GroupNameActivity.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(GroupNameActivity.this, ChatActivity.class);
//                            intent.putExtra("id", documentReference.getId());
//                            intent.putExtra("isGroupChat", true);
//                            startActivity(intent);
//                            // finish the current activity to prevent the user from going back to it
//                            finish();
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    });
//
//
//
//
//        } else {
//            // Show an error message if the group name is empty
//            groupNameInput.setError("Group name cannot be empty");
//        }
//    }


}
