package de.lorenz.da_exam_project.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import de.lorenz.da_exam_project.LinearLayoutManagerWrapper;
import de.lorenz.da_exam_project.R;
import de.lorenz.da_exam_project.adapters.UserListRecyclerAdapter;
import de.lorenz.da_exam_project.listeners.user_list.AddGroupButtonClickListener;
import de.lorenz.da_exam_project.models.User;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class UsersFragment extends Fragment {

    UserListRecyclerAdapter adapter;
    RecyclerView recyclerView;
    FloatingActionButton addGroupButton;
    List<String> selectedUsers;

    public UsersFragment(FloatingActionButton addGroupButton) {
        this.addGroupButton = addGroupButton;
        this.selectedUsers = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.user_list_recycler_view);

        setupRecyclerView();
        setupAddGroupButton();

        return view;
    }

    /**
     * Registers the add group button to create a new group on click
     */
    private void setupAddGroupButton() {
        AddGroupButtonClickListener addGroupButtonClickListener = new AddGroupButtonClickListener(requireContext(), this.selectedUsers);
        addGroupButton.setOnClickListener(addGroupButtonClickListener);
    }

    /**
     * Registers the recycler view to the adapter.
     * This will make the recycler view display all users and updates automatically if a user is added or removed.
     */
    private void setupRecyclerView() {

        // get all users order by registration date
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(FirebaseUtil.getAllUsers().orderBy("username", Query.Direction.DESCENDING), User.class)
                .build();

        adapter = new UserListRecyclerAdapter(options, requireContext(), this.selectedUsers, this.addGroupButton);
        LinearLayoutManagerWrapper linearLayoutManagerWrapper = new LinearLayoutManagerWrapper(requireContext());
        recyclerView.setLayoutManager(linearLayoutManagerWrapper);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) adapter.startListening();
    }
}
