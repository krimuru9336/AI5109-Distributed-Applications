package com.example.buddyapp.groupchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buddyapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupRoom extends AppCompatActivity {
    DatabaseReference profileRef,documentReferenceGroup;
    ArrayList<String> groupInfo;
    FirebaseRecyclerAdapter<PojoGroup, GroupsViewViewHolder> firebaseRecyclerAdapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/");
    RecyclerView recyclerView;
    EditText groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupInfo=new ArrayList<>();
        setContentView(R.layout.activity_group_room);
        profileRef = database.getReference("groups");
        Button createGroup=findViewById(R.id.groupcreate_button);
        recyclerView = findViewById(R.id.rv_view_groups);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(GroupRoom.this));
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GroupRoom.this,CreateGroup.class);
                startActivity(intent);
            }
        });
        onStartLoading();
        startProcessing();
    }

    protected void onStartLoading() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        FirebaseRecyclerOptions<PojoGroup> options =
                new FirebaseRecyclerOptions.Builder<PojoGroup>()
                        .setQuery(profileRef, PojoGroup.class)
                        .build();
        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<PojoGroup, GroupsViewViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull GroupsViewViewHolder holder, int position, @NonNull PojoGroup model) {
                        final String postkey = getRef(position).getKey();
                        Log.d("constains",model.memberid.contains(currentuid)+" "+model.memberid);
                            if(model.memberid.contains(currentuid)){
                                holder.setGroupSelection(getApplication(), model.getGroupName(), model.getCreatedby(), model.memberid);
                                holder.sendGroupbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent=new Intent(GroupRoom.this, GroupMessageActivity.class);
                                        intent.putExtra("groupRow",postkey);
//                                        intent.putExtra("grp_name",model.groupName);
                                        intent.putExtra("groupinfo",(Serializable) model);
                                        intent.putExtra("uid",currentuid);
                                        startActivity(intent);
                                    }
                                });
                            }
//                        holder.setUserSelection1(getApplication(), model.groupName, model.createdby);
//                        String name = getItem(position).groupName;
//                        String url = getItem(position).createdby;
//                        ArrayList<String> uid = getItem(position).memberid;
                    }

                    @NonNull
                    @Override
                    public GroupsViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.group_view, parent, false);
                        return new GroupsViewViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    protected void startProcessing(){
        Log.d("arrayList",groupInfo+"");
    }
}