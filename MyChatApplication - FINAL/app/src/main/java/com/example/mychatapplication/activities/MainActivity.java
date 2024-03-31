package com.example.mychatapplication.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.example.mychatapplication.R;
import com.example.mychatapplication.adapters.ChatListAdapter;
import com.example.mychatapplication.databinding.ActivityMainBinding;
import com.example.mychatapplication.models.Chat;
import com.example.mychatapplication.utilities.Constants;
import com.example.mychatapplication.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    private ChatListAdapter adapter;
    private List<Chat> chatList;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        getToken();
        init();

    }

    private void init(){

        chatList = new ArrayList<>();
        adapter = new ChatListAdapter(this, chatList);
        binding.conversationsRecyclerView.setAdapter(adapter);
        binding.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showPopup(view);
            }
        });

        binding.fabNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
                startActivity(intent);
            }
        });

        db = FirebaseFirestore.getInstance();


        fetchChats();
    }



    private void fetchChats() {
        // Query to fetch chats the current user belongs to
        Query chatsRef = db.collection("chats")
                .whereArrayContains("participants",
                        preferenceManager.getString(Constants.KEY_USER_ID));

        chatsRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            binding.progressBar.setVisibility(View.GONE);
            if (e != null) {
                Log.v("MainActivity","error");
                return;
            }

            if (queryDocumentSnapshots != null) {
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.v("MainActivity","added");
                            // New chat added
                            try {
                                Chat chat = dc.getDocument().toObject(Chat.class);
                                Log.v("MainActivity",chat.getId());
                                chatList.add(chat);
                                sortChatsByTimestamp(chatList);
                                adapter.notifyDataSetChanged();
                            }catch (Exception ex){
                                Log.v("MainActivity",ex.getMessage().toString());
                            }
                            break;
                        case MODIFIED:
                            // Chat modified (e.g., new message)
                            // Update the chat in the list
                            Chat modifiedChat = dc.getDocument().toObject(Chat.class);
                            for (int i = 0; i < chatList.size(); i++) {
                                if (chatList.get(i).getId().equals(modifiedChat.getId())) {
                                    chatList.set(i, modifiedChat);
                                    break;
                                }
                            }
                            sortChatsByTimestamp(chatList);
                            adapter.notifyDataSetChanged();
                            break;
                        case REMOVED:
                            // Chat removed
                            // Remove the chat from the list
                            Chat removedChat = dc.getDocument().toObject(Chat.class);
                            for (int i = 0; i < chatList.size(); i++) {
                                if (chatList.get(i).getId().equals(removedChat.getId())) {
                                    chatList.remove(i);
                                    break;
                                }
                            }
                            sortChatsByTimestamp(chatList);
                            adapter.notifyDataSetChanged();
                            break;
                    }
                }
            }else {
                Log.v("MainActivity","null");
            }
        });
    }

    private void loadUserDetails(){
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }





    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }
    private void updateToken(String token){
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );

        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

    private void singOut(){
        showToast("Signing out...");
        FirebaseFirestore database= FirebaseFirestore.getInstance();
        DocumentReference documentReference=
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String , Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Unable to sign out"));
    }


    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.home_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_logout) {
                    singOut();
                    return true;
                } else if (item.getItemId() == R.id.action_create_group) {
                    Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
                    intent.putExtra("createType", "group");
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        popup.show();
    }


    public void sortChatsByTimestamp(List<Chat> chats) {
        Collections.sort(chats, new Comparator<Chat>() {
            @Override
            public int compare(Chat chat1, Chat chat2) {
                // Compare timestamps of chat1 and chat2
                return chat2.getTimestamp().compareTo(chat1.getTimestamp());
                // To sort in ascending order, swap chat1 and chat2 in the compareTo() method call
            }
        });
    }

}
