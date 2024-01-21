package demo.campuschat;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://campuschat-13dbc-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference usersRef = database.getReference("users");
        DatabaseReference messagesRef = database.getReference("messages");
        DatabaseReference chatSummariesRef = database.getReference("chat_summaries");



        if (currentUser != null) {
            Intent homeIntent = new Intent(MainActivity.this, BaseActivity.class);
            startActivity(homeIntent);
            finish();
        } else {
            // User not logged in, redirect to LoginActivity
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

        DataSeeder dataSeeder = new DataSeeder();
//        dataSeeder.seedUsers(usersRef);
        dataSeeder.seedMessages(messagesRef);
        dataSeeder.seedChatSummaries(chatSummariesRef);

    }
}
