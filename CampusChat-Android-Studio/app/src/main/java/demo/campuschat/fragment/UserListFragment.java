package demo.campuschat.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import demo.campuschat.ConversationActivity;
import demo.campuschat.R;
import demo.campuschat.adapter.UserAdapter;
import demo.campuschat.model.ChatSummary;
import demo.campuschat.model.Group;
import demo.campuschat.model.User;

public class UserListFragment extends Fragment {

    private UserAdapter adapter;
    private List<User> userList;
    private DatabaseReference usersRef, groupRef, groupChatSummaryRef;
    private FirebaseUser currentUser;
    private ImageButton createGroupButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_user_list, container, false);

        createGroupButton = view.findViewById(R.id.button_create_group);
        createGroupButton.setOnClickListener(v -> {
            Log.d("selected", "onCreateView: "+ adapter.getSelectedUserIds());
            if (adapter.getSelectedUserIds().size() < 2) {
                Toast.makeText(getContext(), "Please select at least 2 users to create a group", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_create_group, null);
                EditText editTextGroupName = dialogView.findViewById(R.id.editText_group_name);
                
                builder.setView(dialogView)
                        .setTitle("Create Group")
                        .setPositiveButton("Create", (dialog, which) -> {
                            String groupName = editTextGroupName.getText().toString().trim();
                            if (!groupName.isEmpty()){
                                createGroup(groupName, adapter.getSelectedUserIds());
                            } else {
                                Toast.makeText(getContext(), "Group name cannot be empty", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();

        // Temporary: Add static data to the list
        adapter = new UserAdapter(userList, this::onUserSelected);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://campuschat-13dbc-default-rtdb.europe-west1.firebasedatabase.app");
        usersRef = database.getReference("users");
        groupRef = database.getReference("groups");
        groupChatSummaryRef = database.getReference("group_summaries");
        loadUsers();
    }

    private void loadUsers() {

        String currentUserId = currentUser != null ? currentUser.getUid() : null;
        usersRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && !user.getUserId().equals(currentUserId)) {
                        Log.d("UserListActivity", "Loaded user: " + user.getUserName());
                        userList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserListActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void createGroup(String groupName, Set<String> memberIds) {

        String groupId = groupRef.push().getKey();
        memberIds.add(currentUser.getUid());
        List<String> memeberIdsList = new ArrayList<>(memberIds);

        Group group = new Group(groupId, groupName, memeberIdsList);
        assert groupId != null;
        groupRef.child(groupId).setValue(group)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Your Group has been successfully created!", Toast.LENGTH_SHORT).show();

                    // Create ChatSummary for each member
                    for (String memberId : memberIds) {
                        ChatSummary chatSummary = new ChatSummary(
                                groupId,
                                groupName,
                                "You were added to the group: "+groupName,
                                System.currentTimeMillis(),
                                true
                        );
                        groupChatSummaryRef.child(memberId).child(groupId).setValue(chatSummary);
                    }
                    navigateToChatActivity(groupId, groupName);

                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to create group", Toast.LENGTH_SHORT).show());
    }

    private void navigateToChatActivity(String groupId, String groupName) {
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra("GROUP_ID", groupId);
        intent.putExtra("GROUP_NAME", groupName);
        intent.putExtra("IS_GROUP_CHAT", true);
        startActivity(intent);
    }


    private void onUserSelected(User user) {
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra("RECEIVER_ID", user.getUserId());
        intent.putExtra("RECEIVER_NAME", user.getUserName());
        startActivity(intent);
    }
}
