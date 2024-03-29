package com.example.mysheetchatda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import com.example.mysheetchatda.Adapter.FragmentAdapter;
import com.example.mysheetchatda.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

/*
- Name: Adrianus Jonathan Engelbracht
- Matriculation number: 1151826
- Date: 02.02.2024
*/

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        binding.viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout){
            Toast.makeText(MainActivity.this, "Succesfully logged out", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, Signin.class);
            startActivity(intent);
//        }else if(item.getItemId() == R.id.groupChat){
//            Intent intentGroupChat = new Intent(MainActivity.this,GroupChatActivity.class);
//            startActivity(intentGroupChat);
        }

        return super.onOptionsItemSelected(item);
    }

}