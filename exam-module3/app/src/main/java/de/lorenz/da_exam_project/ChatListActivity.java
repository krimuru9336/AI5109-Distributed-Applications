package de.lorenz.da_exam_project;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import de.lorenz.da_exam_project.adapters.ChatListRecyclerAdapter;
import de.lorenz.da_exam_project.models.User;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class ChatListActivity extends AppCompatActivity {

    ChatListRecyclerAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        recyclerView = findViewById(R.id.chat_list_recycler_view);

        setUserInformations();

        setupRecyclerView();
    }

    /**
     * Sets the user information text view to the current user id.
     */
    private void setUserInformations() {
        // set own user id information
        FirebaseUtil.getCurrentUserDetails().get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }

            User currentUser = task.getResult().toObject(User.class);
            String userInfo = currentUser.getUsername() + " (ID: " + FirebaseUtil.getCurrentUserId() + ")";
            TextView currentUserIdTextView = findViewById(R.id.current_user_id_text_view);
            currentUserIdTextView.setText(userInfo);
        });
    }

    /**
     * Registers the recycler view to the adapter.
     * This will make the recycler view display all users and updates automatically if a user is added or removed.
     */
    private void setupRecyclerView() {

        // get all users order by registration date
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(FirebaseUtil.getAllUsers().orderBy("registrationDate", Query.Direction.DESCENDING), User.class)
                .build();

        adapter = new ChatListRecyclerAdapter(options, this);
        LinearLayoutManagerWrapper linearLayoutManagerWrapper = new LinearLayoutManagerWrapper(this);
        recyclerView.setLayoutManager(linearLayoutManagerWrapper);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) adapter.startListening();
    }
}