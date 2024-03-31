package com.example.myapplication5;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication5.adapter.GroupMemberAdapter;
//import com.example.myapplication5.adapter.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CreateGroupActivity extends AppCompatActivity {

    EditText searchInput;
    TextView cancel, next;
    RecyclerView recyclerView;

    GroupMemberAdapter adapter;
    List<HelperClass> usersList;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("entered Create group activity");
        super.onCreate(savedInstanceState);
        System.out.println("entered Create group activity 2");
        setContentView(R.layout.activity_create_group);

        System.out.println("entered Create group activity 2");
        searchInput = findViewById(R.id.seach_username_input);
        cancel=findViewById(R.id.cancel_textview);
        next= findViewById(R.id.next_textview);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchInput.requestFocus();

        List<HelperClass> userList = new ArrayList<>();
        adapter = new GroupMemberAdapter(userList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        System.out.println("adapter line2");


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        usersCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {

                    return;
                }

                userList.clear();

                for (QueryDocumentSnapshot document : querySnapshot) {
                    // Convert the document to your HelperClass
                    HelperClass user = document.toObject(HelperClass.class);
                    System.out.println(user+"user");
                    userList.add(user);
                }

                adapter.notifyDataSetChanged();
            }
        });



    }

    public void onNextTextViewClick (View view){


        Map<String, String> selectedUsersMap = adapter.getSelectedUsers();

        System.out.println("Next is clicked selectedUsers->\t" + selectedUsersMap);

        // Serialize the selected users map to a JSON string
        Gson gson = new Gson();
        String selectedUsersJson = gson.toJson(selectedUsersMap);

        System.out.println("selectedusersjson:" + selectedUsersJson);

        // Extract the selected user IDs from the map
        List<String> selectedUserIDs = new ArrayList<>(selectedUsersMap.keySet());



        // Create an intent to start GroupNameActivity
        Intent intent = new Intent(CreateGroupActivity.this, GroupNameActivity.class);
        // Pass the selected users map as an extra
        intent.putExtra("selectedUsers", selectedUsersJson);


        // Add the selectedUserIDs to the intent
        intent.putStringArrayListExtra("selectedUserIDs", new ArrayList<>(selectedUserIDs));


        startActivity(intent);








        // Get the selected users list from the adapter
//
//
//
//        System.out.println("Next is clicked selectedUsers->\t");
//
//        List<String> selectedUsers = adapter.getSelectedUsers();
//
//        // Serialize the selected users list to a JSON string
//        Gson gson = new Gson();
//        String selectedUsersJson = gson.toJson(selectedUsers);
//
//        System.out.println("selectedusersjson"  +  selectedUsersJson);
//
//        // Create an intent to start GroupNameActivity
//        Intent intent = new Intent(CreateGroupActivity.this, GroupNameActivity.class);
//        // Pass the selected users list as an extra
//        intent.putExtra("selectedUsers", selectedUsersJson);
//        startActivity(intent);

    }

    public void onCancelTextViewClick (View view){
        getOnBackPressedDispatcher().onBackPressed();
//        startActivity(new Intent(CreateGroupActivity.this,SearchUserActivity.class));
    }


}
