package com.example.myapplication5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.OnBackPressedDispatcherOwner;

import com.example.myapplication5.adapter.UserAdapter;
import com.example.myapplication5.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;




public class SearchUserActivity extends AppCompatActivity {
    EditText searchInput;
    ImageButton closeButton;
    RecyclerView recyclerView;
    UserAdapter adapter;
//    List<HelperClass> usersList;
//    FirebaseAuth firebaseAuth;

    //group
    Button createGroupButton;
//    private boolean isGroupCreationMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("entered search");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.seach_username_input);
        closeButton = findViewById(R.id.close_button);
        createGroupButton=findViewById(R.id.new_group_button); //new group
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchInput.requestFocus();

        //button actions
        closeButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();

        });
        //create group button click - send to create group activity
        createGroupButton.setOnClickListener(view -> {
//                isGroupCreationMode = true;
                startActivity(new Intent(SearchUserActivity.this,CreateGroupActivity.class));
        });

        System.out.println("above arraylist line");

        List<HelperClass> userList = new ArrayList<>();
        adapter = new UserAdapter(userList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        System.out.println("adapter line2");


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");

        System.out.println(usersCollection + "usersCollection");

        usersCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.out.println("NULLLLL");
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





    }

}
