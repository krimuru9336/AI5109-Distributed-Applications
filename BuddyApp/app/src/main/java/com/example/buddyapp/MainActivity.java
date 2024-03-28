package com.example.buddyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    public static final int PROFILE_BOTTOM_ID = R.id.profile_bottom;
    public static final int ASK_BOTTOM_ID = R.id.ask_bottom;
    public static final int REQUEST_BOTTOM_ID = R.id.request_bottom;
    public static final int HOME_BOTTOM_ID = R.id.home_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();

        BottomNavigationView bottomnavigationview=findViewById(R.id.bottom_nav);
        bottomnavigationview.setOnNavigationItemSelectedListener(onNav);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new Fragment1()).commit();

    }
    private BottomNavigationView.OnNavigationItemSelectedListener onNav = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected=null;
            //
            if(item.getItemId()==PROFILE_BOTTOM_ID){
                selected = new Fragment1();
            }
            else if (item.getItemId()==ASK_BOTTOM_ID){
                selected = new Fragment2();
            }
            else if (item.getItemId()==REQUEST_BOTTOM_ID){
                selected = new Fragment3();
            }
            else if (item.getItemId()==HOME_BOTTOM_ID){
                selected = new Fragment4();
            }
//            switch (item.getItemId()) {
//                case PROFILE_BOTTOM_ID:
//                    selected = new Fragment1();
//                    break;
//                case ASK_BOTTOM_ID:
//                    selected = new Fragment2();
//                    break;
//                case REQUEST_BOTTOM_ID:
//                    selected = new Fragment3();
//                    break;
//                case HOME_BOTTOM_ID:
//                    selected = new Fragment4();
//                    break;
//
//                default:break;
//            }
            if(selected!=null){
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,selected).commit();
                return true;
            }
            else {
                return false;
            }
        }
    };

    public void logout(View view) {
        auth.signOut();
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }

}