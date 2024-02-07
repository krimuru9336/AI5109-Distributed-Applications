package de.lorenz.da_exam_project.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import de.lorenz.da_exam_project.LinearLayoutManagerWrapper;
import de.lorenz.da_exam_project.R;
import de.lorenz.da_exam_project.adapters.ChatListRecyclerAdapter;
import de.lorenz.da_exam_project.models.ChatRoom;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class ChatsFragment extends Fragment {

    ChatListRecyclerAdapter adapter;
    RecyclerView recyclerView;
    TextView noChatsTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.chat_list_recycler_view);
        noChatsTextView = view.findViewById(R.id.no_chats_text_view);

        setupRecyclerView(noChatsTextView);

        return view;
    }

    /**
     * Registers the recycler view to the adapter.
     * This will make the recycler view display all chats and updates automatically if a chat changes.
     */
    private void setupRecyclerView(TextView noChatsTextView) {

        // get all chats order by last message date
        FirestoreRecyclerOptions<ChatRoom> options = new FirestoreRecyclerOptions.Builder<ChatRoom>()
                .setQuery(FirebaseUtil.getAllChats().orderBy("lastMessageTimestamp", Query.Direction.DESCENDING), ChatRoom.class)
                .build();

        adapter = new ChatListRecyclerAdapter(options, requireContext(), noChatsTextView);
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