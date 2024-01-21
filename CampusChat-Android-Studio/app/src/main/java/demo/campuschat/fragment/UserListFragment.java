package demo.campuschat.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import demo.campuschat.ConversationActivity;
import demo.campuschat.R;
import demo.campuschat.adapter.UserAdapter;
import demo.campuschat.model.User;

public class UserListFragment extends Fragment {

    private UserAdapter adapter;
    private List<User> userList;
    private DatabaseReference usersRef;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_user_list, container, false);
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
        loadUsers();
    }

    private void loadUsers() {

        String currentUserId = currentUser != null ? currentUser.getUid() : null;
        usersRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("UserListActivity", "Total users: " + dataSnapshot.getChildrenCount());
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


    private void onUserSelected(User user) {
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra("RECEIVER_ID", user.getUserId());
        intent.putExtra("RECEIVER_NAME", user.getUserName());
        startActivity(intent);
    }
}
