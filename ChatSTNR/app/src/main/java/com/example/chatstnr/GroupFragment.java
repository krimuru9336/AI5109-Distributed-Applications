package com.example.chatstnr;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.chatstnr.adapter.RecentChatRecyclerAdapter;
import com.example.chatstnr.adapter.RecentGroupChatRecyclerAdapter;
import com.example.chatstnr.models.GroupModel;
import com.example.chatstnr.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class GroupFragment extends Fragment {

    ImageButton newGroup;
    RecyclerView recyclerView;
    RecentGroupChatRecyclerAdapter adapter;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_group, container, false);

        newGroup = view.findViewById(R.id.floating_button);
        newGroup.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
            startActivity(intent);
        });

        recyclerView = view.findViewById(R.id.group_recyler_view);
        setupRecyclerView();

        return view;
    }

    void setupRecyclerView() {
        String userId = FirebaseUtil.currentUserid(); // Assuming you have a way to get the current user's ID

        Query query = FirebaseUtil.allGroupsCollectionReference()
                .whereArrayContains("userIds", userId);

        FirestoreRecyclerOptions<GroupModel> options = new FirestoreRecyclerOptions.Builder<GroupModel>()
                .setQuery(query, GroupModel.class)
                .build();

        adapter = new RecentGroupChatRecyclerAdapter(options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }

}