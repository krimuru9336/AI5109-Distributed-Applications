package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity2 extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton searchBtn;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchBtn = findViewById(R.id.search_btn);

        searchBtn.setOnClickListener((v)->{
            startActivity(new Intent(MainActivity2.this, SearchUserActivity.class));
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.chat_menu){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
                }
                if(item.getItemId() == R.id.profile_menu){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit();

                }
                return  true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.chat_menu);


    }
}