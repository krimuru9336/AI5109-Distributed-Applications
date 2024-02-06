package com.example.chatstnr.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.net.Uri;
import android.widget.ImageView;

import com.example.chatstnr.models.UserModel;

public class AndroidUtil {
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel userModel){
        intent.putExtra("username", userModel.getUsername());
        intent.putExtra("phone", userModel.getPhone());
        intent.putExtra("userId", userModel.getUserId());
    }

    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
//        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        return userModel;
    }

}
