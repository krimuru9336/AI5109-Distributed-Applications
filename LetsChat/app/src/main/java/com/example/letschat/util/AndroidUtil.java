package com.example.letschat.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.letschat.model.User;

public class AndroidUtil {

    public static void  showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void passUserDataAsIntent(Intent intent, User user){
        intent.putExtra("username", user.getUsername());
        intent.putExtra("phone", user.getPhone());
        intent.putExtra("userId", user.getUserId());
    }

    public static User getUserFromIntent(Intent intent){
        User user = new User();
        user.setUserId(intent.getStringExtra("userId"));
        user.setUsername(intent.getStringExtra("username"));
        user.setPhone(intent.getStringExtra("phone"));
        return user;
    }
}
