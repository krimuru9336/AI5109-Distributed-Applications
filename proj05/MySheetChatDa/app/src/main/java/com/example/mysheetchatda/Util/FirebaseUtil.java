package com.example.mysheetchatda.Util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mysheetchatda.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
- Name: Adrianus Jonathan Engelbracht
- Matriculation number: 1151826
- Date: 29.03.2024
*/
public class FirebaseUtil {

    public static void getCurrentUserName(FirebaseAuth auth, FirebaseDatabase database, final UsernameCallback callback) {
        String currentUserId = auth.getCurrentUser().getUid();

        database.getReference().child("User").child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            callback.onUsernameFetched(user.getUserName());
                        } else {
                            Log.e("FirebaseUtil", "User data is null.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseUtil", "Failed to read user data: " + error.toException());
                    }
                });
    }
}
