package com.example.whatsdown;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;

    ChatFragment chatFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.main_search_btn);

        searchButton.setOnClickListener((v)->{
            startActivity(new Intent(MainActivity.this,SearchUserActivity.class));
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.menu_chat){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,chatFragment).commit();
                }
                if(item.getItemId()==R.id.menu_profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,profileFragment).commit();
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);

//        getFCMToken();

    }

//    void getFCMToken(){
//        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                String token = task.getResult();
//                FirebaseUtil.currentUserDetails().update("fcmToken",token);
//
//            }
//        });
//    }
}
