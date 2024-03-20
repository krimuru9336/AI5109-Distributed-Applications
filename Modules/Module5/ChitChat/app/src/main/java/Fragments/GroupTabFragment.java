package Fragments;// GroupTabFragment.java
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chitchat.GroupChatActivity;
import com.example.chitchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import Managers.ChatRoomManager;
import Models.ChatRoom;

public class GroupTabFragment extends Fragment {

    private EditText groupEditText;
    private Button addGroupButton;
    private TextView groupInfoTextView;
    private EditText groupNameEditText;
    private Button submitGroupButton;
    private ArrayList<String> groupMembers = new ArrayList<>();


    public GroupTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.group_tab_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get references to UI components
        groupEditText = view.findViewById(R.id.groupEditText);
        addGroupButton = view.findViewById(R.id.addGroupButton);
        groupInfoTextView = view.findViewById(R.id.groupInfoTextView);
        groupNameEditText = view.findViewById(R.id.groupNameEditText);
        submitGroupButton = view.findViewById(R.id.submitGroupButton);

        // Set up onClickListener for the add group button
        addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click, you can access groupEditText.getText().toString() here
                String enteredGroup = groupEditText.getText().toString();
                groupMembers.add(enteredGroup);
                groupInfoTextView.setText("");
                StringBuilder str = new StringBuilder();
                for (String s:groupMembers
                     ) {
                    str.append(s).append(",");
                }
                groupEditText.setText("");
                groupInfoTextView.setText(str.substring(0,str.length()-1));
                // Perform necessary actions with the entered group
            }
        });

        // Set up onClickListener for the submit group button
        submitGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click, you can access groupNameEditText.getText().toString() here
                String enteredGroupName = groupNameEditText.getText().toString();
                // Perform necessary actions with the entered group name
                ChatRoomManager manager = new ChatRoomManager();
                manager.createNewChatGroup(groupMembers, enteredGroupName, new Consumer<ChatRoom>() {
                    @Override
                    public void accept(ChatRoom chatRoom) {
                        Log.i("0",chatRoom.toString());
                        startActivity(new Intent(v.getContext(), GroupChatActivity.class));
                    }
                });

                OverlayMenuFragment fragment = (OverlayMenuFragment) getParentFragment();
                assert fragment != null;
                fragment.dismiss();
            }
        });
    }
}
