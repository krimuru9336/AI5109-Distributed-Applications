package com.example.mysheetchatda.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mysheetchatda.Adapter.UserAdapter;
import com.example.mysheetchatda.ChatPage;
import com.example.mysheetchatda.GroupChatActivity;
import com.example.mysheetchatda.Listener.RecyclerItemClickListener;
import com.example.mysheetchatda.Models.User;
import com.example.mysheetchatda.databinding.FragmentChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
- Name: Adrianus Jonathan Engelbracht
- Matriculation number: 1151826
- Date: 02.02.2024
*/

public class ChatFragment extends Fragment {



    public ChatFragment() {
        // required empty public constructor
    }

    FragmentChatBinding binding;
    ArrayList<User> list = new ArrayList<>();
    FirebaseDatabase database;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChatBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();

        UserAdapter adapter = new UserAdapter(list, getContext());
        binding.chatRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        // dummy "GroupChat" entry at the top of the list
        User groupChatEntry = new User();
        groupChatEntry.setUserName("GroupChat");
        list.add(0, groupChatEntry);

        // get users to display as chats
        database.getReference().child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                while (list.size() > 1) {
                    list.remove(1);
                }
                //list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setUserId(dataSnapshot.getKey());
                    if (!user.getUserId().equals(FirebaseAuth.getInstance().getUid())) {
                        list.add(user);
                    }

                }
                // sort users alphabetically by username
                Collections.sort(list.subList(1, list.size()), new Comparator<User>() {
                    @Override
                    public int compare(User u1, User u2) {
                        return u1.getUserName().compareToIgnoreCase(u2.getUserName());
                    }
                });


                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.chatRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), binding.chatRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position == 0) {
                            // GroupChat clicked
                            Intent intent = new Intent(getActivity(), GroupChatActivity.class);
                            startActivity(intent);
                        } else {
                            // User Chat clicked
                            User clickedUser = list.get(position);
                            Intent intent = new Intent(getActivity(), ChatPage.class);
                            intent.putExtra("userId", clickedUser.getUserId());
                            intent.putExtra("userName", clickedUser.getUserName());
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );

        return binding.getRoot();
    }
}