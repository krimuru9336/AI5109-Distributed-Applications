package com.example.letschat.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.Toast;

import com.example.letschat.R;
import com.example.letschat.model.User;
import com.google.android.material.snackbar.Snackbar;

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

    public static void showSnackBar(View v, String message, Resources resources){
        Snackbar snackbar = Snackbar.make(v, message, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(resources.getColor(R.color.red));
        snackbar.show();

    }

}
