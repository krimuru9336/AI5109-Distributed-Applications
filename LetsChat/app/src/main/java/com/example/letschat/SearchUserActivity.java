package com.example.letschat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.letschat.adapter.SearchUserRecyclerAdapter;
import com.example.letschat.model.User;
import com.example.letschat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchBtn;
    ImageButton backBtn;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter searchUserRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.search_user_txt);
        searchBtn = findViewById(R.id.search_user_btn);
        backBtn = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchBtn.requestFocus();

        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        searchBtn.setOnClickListener(v->{
            String searchTerm = searchInput.getText().toString();
            if(searchTerm.isEmpty() || searchTerm.length() < 2){
                searchInput.setError("Invalid Username");
                return;
            }
            setUpSearchRecycleView(searchTerm);
        });

    }

    void setUpSearchRecycleView(String searchTerm){

        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchTerm)
                .whereLessThanOrEqualTo("username", searchTerm+'\uf8ff');

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class).build();

        searchUserRecyclerAdapter = new SearchUserRecyclerAdapter(options,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchUserRecyclerAdapter);
        searchUserRecyclerAdapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(searchUserRecyclerAdapter !=null){
            searchUserRecyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(searchUserRecyclerAdapter !=null){
            searchUserRecyclerAdapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(searchUserRecyclerAdapter!=null){
            searchUserRecyclerAdapter.startListening();
        }
    }
}