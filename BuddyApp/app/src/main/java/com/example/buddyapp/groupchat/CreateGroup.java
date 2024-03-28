package com.example.buddyapp.groupchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buddyapp.AllUserMember;
import com.example.buddyapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;

public class CreateGroup extends AppCompatActivity {

    DatabaseReference profileRef,documentReferenceGroup;
    HashSet<String> ids;
    FirebaseRecyclerAdapter<AllUserMember, UserSelectionViewHolder> firebaseRecyclerAdapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/");
    RecyclerView recyclerView;
    EditText groupName;
    Boolean startChecking = false;
    Button createGroupbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        documentReferenceGroup =database.getReference("groups");
        ids = new HashSet<String>();
        setContentView(R.layout.activity_create_group);
        recyclerView = findViewById(R.id.rv_users_for_group);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CreateGroup.this));
        profileRef = database.getReference("All Users");
        groupName = findViewById(R.id.groupname_create);
        createGroupbtn = findViewById(R.id.creategroup_btn);
        createGroupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChecking = true;
                startCreatingGroup();
            }
        });
    }

    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AllUserMember> options =
                new FirebaseRecyclerOptions.Builder<AllUserMember>()
                        .setQuery(profileRef, AllUserMember.class)
                        .build();
        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<AllUserMember, UserSelectionViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserSelectionViewHolder holder, int position, @NonNull AllUserMember model) {
                        final String postkey = getRef(position).getKey();
                        holder.setUserSelection(getApplication(), model.getName(), model.getUid(), model.getProf(), model.getUrl());
                        String name = getItem(position).getName();
                        String url = getItem(position).getUrl();
                        String uid = getItem(position).getUid();
                    }

                    @NonNull
                    @Override
                    public UserSelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.user_selection_for_group, parent, false);
                        return new UserSelectionViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    protected void startCreatingGroup() {
        ids.clear();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ids.add(user.getUid());
        for (int i = 0; i < firebaseRecyclerAdapter.getItemCount(); i++) {
            UserSelectionViewHolder viewHolder = (UserSelectionViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder != null) {
                if (viewHolder.checkBox.isChecked()) {
                    // This user is selected, you can do whatever you want here
                    // For example, you can add the user's ID to your group
                    AllUserMember selectedUser = firebaseRecyclerAdapter.getItem(i);
                    if (selectedUser != null) {
                        ids.add(selectedUser.getUid()); // Assuming getUid() returns the user ID
                    }
                }
            }
        }
        if(ids.size()>2 && !(groupName.getText().equals(null))){
            PojoGroup pojoGroup=new PojoGroup(new ArrayList<>(ids),user.getUid(),groupName.getText().toString());
            String keychild=documentReferenceGroup.push().getKey();
            documentReferenceGroup.child(keychild).setValue(pojoGroup);
            Intent intent=new Intent(CreateGroup.this, GroupRoom.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(CreateGroup.this,"Select atleast 2 member",Toast.LENGTH_LONG).show();
        }
    }
}