package Fragments;
import android.content.Intent;
import android.os.Bundle;

import Managers.ChatRoomManager;
import Models.ChatRoom;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.chitchat.GroupChatActivity;
import com.example.chitchat.R;

import java.util.function.Consumer;
public class UserTabFragment extends Fragment {

    private EditText editTextUserName;
    private Button buttonSubmit;

    public UserTabFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_tab_layout, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextUserName = view.findViewById(R.id.editTextUserName);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredUsername = editTextUserName.getText().toString();
                ChatRoomManager manager = new ChatRoomManager();
                manager.searchOrCreateChatRoom(enteredUsername, new Consumer<ChatRoom>() {
                    @Override
                    public void accept(ChatRoom chatRoom) {
                        Log.i("0",chatRoom.toString());
                        startActivity(new Intent(getContext(), GroupChatActivity.class));
                    }
                });
                OverlayMenuFragment fragment = (OverlayMenuFragment) getParentFragment();
                assert fragment != null;
                fragment.dismiss();
            }
        });
    }
}
