package com.example.letschat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.adapter.RecentChatGroupsRecyclerAdapter;
import com.example.letschat.model.ChatGroup;
import com.example.letschat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class GroupFragment extends Fragment {

    RecyclerView recyclerView;
    RecentChatGroupsRecyclerAdapter adapter;

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        setUpRecyclerView();
        return view;
    }

    void setUpRecyclerView() {

        // get only current user chats
        Query query = FirebaseUtil.allGroupChatsCollectionReference();

        FirestoreRecyclerOptions<ChatGroup> options = new FirestoreRecyclerOptions.Builder<ChatGroup>()
                .setQuery(query, ChatGroup.class).build();

        adapter = new RecentChatGroupsRecyclerAdapter(options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
}
}
