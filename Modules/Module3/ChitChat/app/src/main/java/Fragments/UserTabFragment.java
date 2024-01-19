package Fragments;// UserTabFragment.java
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

import com.example.chitchat.GroupChatActivity;
import com.example.chitchat.R;

import java.util.function.Consumer;

import Managers.ChatRoomManager;
import Models.AllMethods;
import Models.ChatRoom;

public class UserTabFragment extends Fragment {

    private EditText usernameEditText;
    private Button submitUserButton;

    public UserTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.user_tab_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get references to UI components
        usernameEditText = view.findViewById(R.id.usernameEditText);
        submitUserButton = view.findViewById(R.id.submitUserButton);

        // Set up onClickListener for the submit button
        submitUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Handle button click, you can access usernameEditText.getText().toString() here
                String enteredUsername = usernameEditText.getText().toString();
                ChatRoomManager manager = new ChatRoomManager();
                manager.searchOrCreateChatRoom(enteredUsername, new Consumer<ChatRoom>() {
                    @Override
                    public void accept(ChatRoom chatRoom) {
                        Log.i("0",chatRoom.toString());
                        AllMethods.CurrentSelectedRoom = chatRoom;
                        OverlayMenuFragment fragment = (OverlayMenuFragment) getParentFragment();
                        assert fragment != null;
                        fragment.dismiss();

                        startActivity(new Intent(getContext(), GroupChatActivity.class));
                    }
                });
            }
        });
    }


}
