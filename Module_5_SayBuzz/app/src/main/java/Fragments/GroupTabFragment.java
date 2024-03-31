package Fragments;
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


import java.util.ArrayList;
import java.util.function.Consumer;

import Managers.ChatRoomManager;
import Models.ChatRoom;


import com.example.chitchat.GroupChatActivity;
import com.example.chitchat.R;

public class GroupTabFragment extends Fragment {

    private EditText editTextgroup;
    private EditText editTextGroupName;
    private Button buttonSubmitGroup;
    private Button buttonAddGroup;
    private TextView textViewGroupinfo;

    private ArrayList<String> groupMembers = new ArrayList<>();


    public GroupTabFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.group_tab_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextgroup = view.findViewById(R.id.editTextGroup);
        buttonAddGroup = view.findViewById(R.id.buttonAddGroup);
        textViewGroupinfo = view.findViewById(R.id.textViewGroupinfo);
        editTextGroupName = view.findViewById(R.id.editTextGroupName);
        buttonSubmitGroup = view.findViewById(R.id.buttonSubmitGroup);

        buttonAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = editTextgroup.getText().toString();
                groupMembers.add(groupName);
                textViewGroupinfo.setText("");
                StringBuilder str = new StringBuilder();
                for (String s:groupMembers
                     ) {
                    str.append(s).append(",");
                }
                editTextgroup.setText("");
                textViewGroupinfo.setText(str.substring(0,str.length()-1));
            }
        });
        buttonSubmitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredGroupName = editTextGroupName.getText().toString();
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
