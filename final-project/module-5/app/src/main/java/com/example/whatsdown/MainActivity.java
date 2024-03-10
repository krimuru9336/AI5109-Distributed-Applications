package com.example.whatsdown;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter mytabsAccessorAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference root;
    private EditText groupNameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        root = FirebaseDatabase
                .getInstance("https://whatsdown-7baba-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WhatsDown");

        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        mytabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(mytabsAccessorAdapter);

        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null){
            SendUserToLoginActivity();
        }
        else{
            VerifyUser();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_log_out_option)
        {
            mAuth.signOut();
            SendUserToLoginActivity();
        }
        if (item.getItemId() == R.id.main_settings_option)
        {
            SendUserToSettingsActivity();
        }
        if (item.getItemId() == R.id.main_find_others_option)
        {
            SendUserToFindOthersActivity();
        }
        if (item.getItemId() == R.id.main_create_group_option)
        {
            RequestNewGroup();
        }

        return true;
    }

    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("What's the name of your group?");

        groupNameField = new EditText(MainActivity.this);
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", createButtonOnClick);
        builder.setNegativeButton("Cancel", cancelButtonOnClick);

        builder.show();
    }

    private void VerifyUser()
    {
        String currentUserID = mAuth.getCurrentUser().getUid();
        root.child("Users").child(currentUserID).addValueEventListener(verifyUserListener);
    }

    private final ValueEventListener verifyUserListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            if ((dataSnapshot.child("name").exists()))
            {

            }
            else
            {
                SendUserToSettingsActivity();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private final DialogInterface.OnClickListener createButtonOnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String groupName = groupNameField.getText().toString();

            if (TextUtils.isEmpty(groupName))
            {
                Toast.makeText(MainActivity.this, "A group name is required", Toast.LENGTH_SHORT).show();
            }
            else
            {
                CreateNewGroup(groupName);
            }
        }
    };

    private final DialogInterface.OnClickListener cancelButtonOnClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };

    private void CreateNewGroup(String groupName) {
        root
                .child("Groups")
                .child(groupName)
                .setValue("")
                .addOnCompleteListener(createNewGroupOnComplete);
    }

    private final OnCompleteListener<Void> createNewGroupOnComplete = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful())
            {
                Toast.makeText(MainActivity.this, "Group created", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void SendUserToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void SendUserToSettingsActivity() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void SendUserToFindOthersActivity() {
        Intent intent = new Intent(MainActivity.this, FindOthersActivity.class);
        startActivity(intent);
    }
}
