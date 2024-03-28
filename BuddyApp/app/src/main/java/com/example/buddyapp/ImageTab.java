package com.example.buddyapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class ImageTab extends Fragment {

    FirebaseDatabase database;
    DatabaseReference reference;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.images_tab,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        database = FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/");

        recyclerView = getActivity().findViewById(R.id.rv_imagestab);
        reference = database.getReference("All images").child(uid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Postmember> options = new FirebaseRecyclerOptions.Builder<Postmember>()
                .setQuery(reference,Postmember.class)
                .build();

        FirebaseRecyclerAdapter<Postmember,ImagesFragment> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Postmember, ImagesFragment>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ImagesFragment holder, int position, @NonNull Postmember model) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentuserid = user.getUid();

                final String postkey = getRef(position).getKey();
                holder.setImages(getActivity(), model.getName(), model.getUrl(),model.getPostUri(),model.getTime(),model.getUid(),
                        model.getType(),model.getDesc());
            }

            @NonNull
            @Override
            public ImagesFragment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.post_images,parent,false);
                return new ImagesFragment(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        GridLayoutManager glm = new GridLayoutManager(getActivity(),3,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(glm);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
