import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {
    private TextView nameTextView, courseTextView;
    private RecyclerView chatRecyclerView;
    private Button logoutButton;
    private ImageButton newChatButton;
     private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();
        fetchChatData();

        
        nameTextView = findViewById(R.id.nameTextView);
        courseTextView = findViewById(R.id.courseTextView);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        logoutButton = findViewById(R.id.logoutButton);
        newChatButton = findViewById(R.id.newChatButton);

        
        nameTextView.setText("Rishabh Goswami");
        courseTextView.setText("1455991 (fdai7680) Distributed Application 2024");

        
        List<Chat> chatList = generateDummyChats();
        ChatAdapter chatAdapter = new ChatAdapter(chatList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                Toast.makeText(HomeActivity.this, "Logout clicked", Toast.LENGTH_SHORT).show();
                
            }
        });

        
        newChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                Toast.makeText(HomeActivity.this, "New chat clicked", Toast.LENGTH_SHORT).show();
                
            }
        });
    }

    // Dummy data generation
   private void fetchChatData() {
        db.collection("chats")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Chat> chats = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String description = document.getString("description");
                            String time = document.getString("lastMessageTime");
                            chats.add(new Chat(chats.size() + 1, name, description, time));
                        }
                        // Update RecyclerView adapter with fetched data
                        ChatAdapter chatAdapter = new ChatAdapter(chats);
                        chatRecyclerView.setAdapter(chatAdapter);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}
