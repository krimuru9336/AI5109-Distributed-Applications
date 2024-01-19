package Fragments;// GroupTabFragment.java
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chitchat.R;

public class GroupTabFragment extends Fragment {

    private EditText groupEditText;
    private Button addGroupButton;
    private TextView groupInfoTextView;
    private EditText groupNameEditText;
    private Button submitGroupButton;

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
            }
        });
    }
}
