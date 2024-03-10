package com.example.whatsdown;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class FindOthersActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView findOthersRecyclerList;
    private DatabaseReference usersReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_others);

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        findOthersRecyclerList = (RecyclerView) findViewById(R.id.find_others_recycler_list);
        findOthersRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar) findViewById(R.id.find_others_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find others");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(usersReference, Contacts.class)
                        .build();
        FirebaseRecyclerAdapter<Contacts, FindOthersViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, FindOthersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindOthersViewHolder holder, int position, @NonNull Contacts model) {
                holder.userName.setText(model.getName());
                holder.userStatus.setText(model.getStatus());

            }

            @NonNull
            @Override
            public FindOthersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                FindOthersViewHolder viewHolder = new FindOthersViewHolder(view);
                return viewHolder;
            }
        };

        findOthersRecyclerList.setAdapter(adapter);

        adapter.startListening();
    }

    public static class FindOthersViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userStatus;

        public FindOthersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
        }
    }
}