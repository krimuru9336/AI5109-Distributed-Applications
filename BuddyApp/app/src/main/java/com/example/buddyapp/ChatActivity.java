package com.example.buddyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.buddyapp.groupchat.GroupRoom;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference profileRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/");
    RecyclerView recyclerView;
    EditText searchEt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        searchEt = findViewById(R.id.search_chuser);
        recyclerView = findViewById(R.id.rv_chat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        Button groupRoom=findViewById(R.id.grouproom);
        groupRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChatActivity.this, GroupRoom.class);
                startActivity(intent);
            }
        });
        profileRef = database.getReference("All Users");
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String query = searchEt.getText().toString().toUpperCase();
                Query search = profileRef.orderByChild("name").startAt(query).endAt(query+"\uf0ff");

                FirebaseRecyclerOptions<AllUserMember>options =
                        new FirebaseRecyclerOptions.Builder<AllUserMember>()
                                .setQuery(search,AllUserMember.class)
                                .build();
                FirebaseRecyclerAdapter<AllUserMember,ProfileViewHolder>firebaseRecyclerAdapter =
                        new FirebaseRecyclerAdapter<AllUserMember, ProfileViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull AllUserMember model) {
                                final String postkey = getRef(position).getKey();
                                holder.setProfileInchat(getApplication(), model.getName(),model.getUid(),model.getProf(),model.getUrl() );
                                String name = getItem(position).getName();
                                String url = getItem(position).getUrl();
                                String uid = getItem(position).getUid();
                                holder.sendmessagebtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ChatActivity.this,MessageActivity.class);
                                        intent.putExtra("n",name);
                                        intent.putExtra("u",url);
                                        intent.putExtra("uid",uid);
                                        startActivity(intent);
                                    }
                                });
                            }

                            @NonNull
                            @Override
                            public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view  = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.chat_profile_item,parent,false);
                                return new ProfileViewHolder(view);
                            }
                        };
                firebaseRecyclerAdapter.startListening();
                recyclerView.setAdapter(firebaseRecyclerAdapter);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AllUserMember>options =
                new FirebaseRecyclerOptions.Builder<AllUserMember>()
                        .setQuery(profileRef,AllUserMember.class)
                        .build();
        FirebaseRecyclerAdapter<AllUserMember,ProfileViewHolder>firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<AllUserMember, ProfileViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull AllUserMember model) {
                        final String postkey = getRef(position).getKey();
                        holder.setProfileInchat(getApplication(), model.getName(),model.getUid(),model.getProf(),model.getUrl() );
                        String name = getItem(position).getName();
                        String url = getItem(position).getUrl();
                        String uid = getItem(position).getUid();
                        holder.sendmessagebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ChatActivity.this,MessageActivity.class);
                                intent.putExtra("n",name);
                                intent.putExtra("u",url);
                                intent.putExtra("uid",uid);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view  = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_profile_item,parent,false);
                        return new ProfileViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }


}