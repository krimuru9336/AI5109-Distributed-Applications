package com.example.buddyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ReplyActivity extends AppCompatActivity {

    private String uid, question, post_key, key;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference reference, reference1;
    private TextView nametv, questiontv, tvreply;
    private RecyclerView recyclerView;
    private ImageView imageViewQue, imageViewUser;
    FirebaseDatabase database  = FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference votesref,Allquestionref;
    Boolean votechecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        // Initialize views
        nametv = findViewById(R.id.name_reply_tv);
        questiontv = findViewById(R.id.que_reply_tv);
        imageViewQue = findViewById(R.id.iv_que_user);
        imageViewUser = findViewById(R.id.iv_reply_user);
        tvreply = findViewById(R.id.answer_tv);

        recyclerView = findViewById(R.id.recyclerview_reply);
        recyclerView.setLayoutManager(new LinearLayoutManager(ReplyActivity.this));




        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            uid = extra.getString("uid");
            post_key = extra.getString("postkey");
            question = extra.getString("q");
            //key = extra.getString("key");
        } else {
            Toast.makeText(this, "Oops, missing data", Toast.LENGTH_SHORT).show();
            // Handle the case when extras are null, for example, finish the activity or show an error message.
            finish();
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        Allquestionref = database.getReference("All Questions").child(post_key).child("Answer");
        votesref=database.getReference("votes");

        reference = db.collection("user").document(uid);
        reference1 = db.collection("user").document(currentUserId);


        // Set onClickListener for the reply button
        tvreply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle reply button click
                Intent intent = new Intent(ReplyActivity.this, AnswerActivity.class);
                intent.putExtra("uid",uid);
                //intent.putExtra("q",question);
                intent.putExtra("postkey",post_key);
                //intent.putExtra("key",privacy);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Fetch data for the question user
        reference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        runOnUiThread(() -> {
                            try {
                                String url = task.getResult().getString("url");
                                String name = task.getResult().getString("name");
                                Picasso.get().load(url).into(imageViewQue);
                                questiontv.setText(question);
                                nametv.setText(name);
                            } catch (Exception e) {
                                // Log the exception or show a more detailed error message
                                e.printStackTrace();
                                Toast.makeText(ReplyActivity.this, "Error updating UI for question user", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(ReplyActivity.this, "Error fetching question user data", Toast.LENGTH_SHORT).show();
                    }
                });

        // Fetch data for the replying user
        reference1.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        runOnUiThread(() -> {
                            try {
                                String url = task.getResult().getString("url");
                                Picasso.get().load(url).into(imageViewUser);
                            } catch (Exception e) {
                                // Log the exception or show a more detailed error message
                                e.printStackTrace();
                                Toast.makeText(ReplyActivity.this, "Error updating UI for replying user", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(ReplyActivity.this, "Error fetching replying user data", Toast.LENGTH_SHORT).show();
                    }
                });
        FirebaseRecyclerOptions<AnswerMember> options = new FirebaseRecyclerOptions.Builder<AnswerMember>()
                .setQuery(Allquestionref,AnswerMember.class)
                .build();

        FirebaseRecyclerAdapter<AnswerMember,AnsViewholder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<AnswerMember, AnsViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AnsViewholder holder, int position, @NonNull AnswerMember model) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentuserid = user.getUid();

                final String postkey = getRef(position).getKey();

                holder.setAnswer(getApplication(),model.getName(),model.getAnswer(),model.getUid()
                        ,model.getTime(),model.getUrl());
                holder.upvoteChecker(postkey);
                holder.upvoteTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        votechecker = true;
                        votesref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(votechecker.equals(true)){
                                    if(snapshot.child(postkey).hasChild(currentuserid)){
                                        votesref.child(postkey).child(currentuserid).removeValue();
                                        votechecker = false;
                                    }else{
                                        votesref.child(postkey).child(currentuserid).setValue(true);
                                        votechecker = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });


            }

            @NonNull
            @Override
            public AnsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ans_layout,parent,false);
                return new AnsViewholder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}