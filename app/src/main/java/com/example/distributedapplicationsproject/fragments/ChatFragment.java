package com.example.distributedapplicationsproject.fragments;

import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.distributedapplicationsproject.R;
import com.example.distributedapplicationsproject.android.ChatRecyclerAdapter;
import com.example.distributedapplicationsproject.firebase.DatabaseService;

public class ChatFragment extends Fragment {

    RecyclerView recyclerViewChats;
    ChatRecyclerAdapter chatRecyclerAdapter;
    DatabaseService databaseService = DatabaseService.getInstance();
    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerViewChats = view.findViewById(R.id.recycler_view_chats);
        setupRecyclerViewChats();

        return view;
    }

    public void setupRecyclerViewChats() {
        databaseService.getChatsOfCurrentUser(chatList -> {
            chatRecyclerAdapter = new ChatRecyclerAdapter(chatList);
            recyclerViewChats.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewChats.setAdapter(chatRecyclerAdapter);
        });
    }
}
