package com.example.myapplication5.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.myapplication5.HelperClass;

public class AndroidUtil {

    public static  void showToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }


    public static void passUserModelAsIntent(Intent intent, HelperClass model){
        intent.putExtra("username",model.getUsername());
        intent.putExtra("userId",model.getUserId());

    }

    public static HelperClass getUserModelFromIntent(Intent intent){
        HelperClass userModel = new HelperClass();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setUserId(intent.getStringExtra("userId"));
        return userModel;
    }


}