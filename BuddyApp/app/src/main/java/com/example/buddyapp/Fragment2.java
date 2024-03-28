package com.example.buddyapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class Fragment2 extends Fragment implements View.OnClickListener{

    FloatingActionButton fd;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference databaseReference,fvrtref,fvrt_listref;
    Boolean fvrtchecker= false;
    RecyclerView recyclerView;
    ImageView imageView;

    QuestionMember member;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment2,container,false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuserid = user.getUid();

        recyclerView=getActivity().findViewById(R.id.rv_f2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        databaseReference=database.getReference("All Questions");

        member = new QuestionMember();
        fvrtref = database.getReference("favourites");
        fvrt_listref = database.getReference("favoriteList").child(currentuserid);

        imageView = getActivity().findViewById(R.id.iv_f2);
        fd=getActivity().findViewById(R.id.floatingActionButton);
        reference = db.collection("user").document(currentuserid);

        fd.setOnClickListener(this);
        imageView.setOnClickListener(this);

        FirebaseRecyclerOptions<QuestionMember>options = new FirebaseRecyclerOptions.Builder<QuestionMember>()
                .setQuery(databaseReference,QuestionMember.class)
                .build();

        FirebaseRecyclerAdapter<QuestionMember,ViewHolder_questions>firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<QuestionMember, ViewHolder_questions>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_questions holder, int position, @NonNull QuestionMember model) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentuserid = user.getUid();

                final String postkey = getRef(position).getKey();
                Log.d("91 line model",model.getUserid()+"");
                Log.d("model name",model.getName()+" "+model.getQuestion());

                holder.setitem(getActivity(),model.getName(),model.getUrl(),model.getUserid(),model.getKey(),model.getQuestion(),model.getPrivacy(),model.getTime());

                String que=getItem(position).getQuestion();
                String name=getItem(position).getName();
                String url=getItem(position).getUrl();
                String time = getItem(position).getTime();
                String privacy= getItem(position).getPrivacy();
                final String userid=getItem(position).getUserid();
                Log.d("click fragment",userid+"");


                holder.replybtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ReplyActivity.class);
                        intent.putExtra("uid",userid);
                        intent.putExtra("q",que);
                        intent.putExtra("postkey",postkey);
                        //intent.putExtra("key",privacy);
                        startActivity(intent);
                    }
                });

                holder.favouriteChecker(postkey);
                holder.fvrt_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fvrtchecker = true;
                        fvrtref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(fvrtchecker.equals(true)){
                                    if(snapshot.child(postkey).hasChild(currentuserid)){
                                        fvrtref.child(postkey).child(currentuserid).removeValue();
                                        delete(time);
                                        fvrtchecker=false;
                                    }
                                    else {
                                        fvrtref.child(postkey).child(currentuserid).setValue(true);
                                        member.setName(name);
                                        member.setTime(time);
                                        member.setUrl(url);
                                        member.setPrivacy(privacy);
                                        member.setUserid(userid);
                                        member.setQuestion(que);

                                        //String id = fvrt_listref.push().getKey();
                                        fvrt_listref.child(postkey).setValue(member);
                                        fvrtchecker = false;
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
            public ViewHolder_questions onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.question_item,parent,false);
                return new ViewHolder_questions(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }

    void delete(String time){

        Query query = fvrt_listref.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();
                    Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    /**
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_f2) {
            BottomSheetF2 bottomSheetF2 = new BottomSheetF2();
            bottomSheetF2.show(getFragmentManager(), "bottom");
        } else if (v.getId() == R.id.floatingActionButton) {
            Intent intent = new Intent(getActivity(), AskActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        reference.get()
                .addOnCompleteListener((task)->{
                   if(task.getResult().exists()){
                       String url=task.getResult().getString("url");
                       Picasso.get().load(url).into(imageView);
                   }else{
                       Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                   }
                });
    }
}