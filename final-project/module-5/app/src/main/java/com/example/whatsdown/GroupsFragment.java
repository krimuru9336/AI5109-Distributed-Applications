package com.example.whatsdown;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class GroupsFragment extends Fragment {
    private View groupFragmentView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> groupsList = new ArrayList<>();

    private DatabaseReference groupsReference;

    public GroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);

        groupsReference = FirebaseDatabase
                                .getInstance("https://whatsdown-7baba-default-rtdb.europe-west1.firebasedatabase.app")
                                .getReference()
                                .child("Groups");

        IntializeFields();

        GetAndDisplayGroups();

        listView.setOnItemClickListener(listViewOnItemClick);

        return groupFragmentView;
    }

    private void GetAndDisplayGroups() {
        groupsReference.addValueEventListener(getAndDisplayGroupsListener);
    }

    private void IntializeFields() {
        listView = (ListView) groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, groupsList);
        listView.setAdapter(arrayAdapter);
    }

    private final ValueEventListener getAndDisplayGroupsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot)
        {
            Set<String> uniqueSet = new HashSet<>();
            Iterator iterator = snapshot.getChildren().iterator();

            while (iterator.hasNext())
            {
                uniqueSet.add(((DataSnapshot)iterator.next()).getKey());
            }

            groupsList.clear();
            groupsList.addAll(uniqueSet);
            arrayAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private final AdapterView.OnItemClickListener listViewOnItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
        {
            String currentGroupName = adapterView.getItemAtPosition(position).toString();

            Intent intent = new Intent(getContext(), GroupChatActivity.class);
            intent.putExtra("groupName" , currentGroupName);
            startActivity(intent);
        }
    };}