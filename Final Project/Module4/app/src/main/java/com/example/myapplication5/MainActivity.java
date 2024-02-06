package com.example.myapplication5;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

//Home page
public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;

    ChatsFragment chatsFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatsFragment = new ChatsFragment();
        profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.new_chat_button);

        searchButton.setOnClickListener((v)->{
            startActivity(new Intent(MainActivity.this,SearchUserActivity.class));
        });

        //listener for menu
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //on clicking menu icon - open chats fragment
                if(item.getItemId()==R.id.menu_chat){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,chatsFragment).commit();
                }
                //on clicking profile icon - open profile fragment
                if(item.getItemId()==R.id.menu_profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,profileFragment).commit();
                }
                return true;
            }
        });
        // default view upon home page loading
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);



    }


}