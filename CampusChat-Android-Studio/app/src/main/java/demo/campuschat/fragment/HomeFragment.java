package demo.campuschat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import demo.campuschat.LoginActivity;
import demo.campuschat.R;
import demo.campuschat.adapter.ChatSummaryAdapter;
import demo.campuschat.adapter.OnChatSummaryClickListener;
import demo.campuschat.model.ChatSummary;

public class HomeFragment extends Fragment {

    private ChatSummaryAdapter adapter;
    private List<ChatSummary> chatSummaries;
    private FirebaseDatabase database;
    private DatabaseReference csRef;

    FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance("https://campuschat-13dbc-default-rtdb.europe-west1.firebasedatabase.app");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        ImageButton logoutButton = view.findViewById(R.id.button_logout);


        chatSummaries = new ArrayList<>();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_chats);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatSummaryAdapter(chatSummaries, new OnChatSummaryClickListener() {
            @Override
            public void onChatSummaryClicked(ChatSummary chatSummary) {
                Intent intent = new Intent(getActivity(), ConversationActivity.class);
                intent.putExtra("RECEIVER_ID", chatSummary.getChatPartnerId());
                intent.putExtra("RECEIVER_NAME", chatSummary.getChatPartnerName());
                Log.d("TAG", "onChatSummaryClicked: "+ chatSummary.getChatPartnerName());
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent( getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        recyclerView.setAdapter(adapter);


        csRef = database.getReference("chat_summaries").child(currentUser.getUid());
        loadChatSummaries();

    }

    private void loadChatSummaries() {

        if (currentUser != null){
            csRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chatSummaries.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Assuming you have a method to create a ChatSummary from a snapshot
                        ChatSummary summary = snapshot.getValue(ChatSummary.class);
                        chatSummaries.add(summary);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("HomeFragment", "Database error: " + databaseError.getMessage());
                }
            });

        }
    }


}
