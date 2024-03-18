package com.example.chatstnr;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.chatstnr.adapter.RecentChatRecyclerAdapter;
import com.example.chatstnr.models.ChatroomModel;
import com.example.chatstnr.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class GroupFragment extends Fragment {

    ImageButton newGroup;
    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;

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

    void setupRecyclerView(){

        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds",FirebaseUtil.currentUserid())
                .orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query,ChatroomModel.class).build();

        adapter = new RecentChatRecyclerAdapter(options,getContext());
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