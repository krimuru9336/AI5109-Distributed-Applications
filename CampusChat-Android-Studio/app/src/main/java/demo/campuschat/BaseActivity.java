package demo.campuschat;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import demo.campuschat.fragment.AboutFragment;
import demo.campuschat.fragment.HomeFragment;
import demo.campuschat.fragment.UserListFragment;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_chat) {
                // Switch to Chat Summaries Fragment
                showFragment(new HomeFragment());
            } else if (itemId == R.id.nav_add_conversation) {
                // Switch to Add New Conversation Fragment
                showFragment(new UserListFragment());
            } else if (itemId == R.id.nav_about) {
                // Switch to About Fragment
                showFragment(new AboutFragment());
            }
            return true;
        });

        // Default fragment on start
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_chat);
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


}
