package com.example.buddyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Related extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related);

        recyclerView = findViewById(R.id.rv_realted);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuserid = user.getUid();

        reference = database.getReference("favoriteList").child(currentuserid);

        FirebaseRecyclerOptions<QuestionMember> options = new FirebaseRecyclerOptions.Builder<QuestionMember>()
                .setQuery(reference,QuestionMember.class)
                .build();

        FirebaseRecyclerAdapter<QuestionMember,ViewHolder_questions> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<QuestionMember, ViewHolder_questions>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_questions holder, int position, @NonNull QuestionMember model) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentuserid = user.getUid();

                final String postkey = getRef(position).getKey();
                Log.d("question member obj",model.getUserid());

                holder.setitemRelated(getApplication(),model.getName(),model.getUrl(),model.getUserid(),model.getKey(),model.getQuestion(),model.getPrivacy(),model.getTime());

                String que=getItem(position).getQuestion();
//                String name=getItem(position).getName();
//                String url=getItem(position).getUrl();
//                String time = getItem(position).getTime();
//                String privacy= getItem(position).getPrivacy();
                final String userid=getItem(position).getUserid();
try {
    if(holder.replybtn==null){
        Log.d("null msg","reply btn is null");
    }
    holder.replybtn1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Related.this, ReplyActivity.class);
            intent.putExtra("uid", userid);
            intent.putExtra("q", que);
            intent.putExtra("postkey", postkey);
            startActivity(intent);
        }
    });
}catch (Exception e){
    Log.d("error",e.getMessage());
    Toast.makeText(Related.this, "e", Toast.LENGTH_SHORT).show();
}

            }

            @NonNull
            @Override
            public ViewHolder_questions onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.related_item,parent,false);
                return new ViewHolder_questions(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}