package com.example.whatsdownapp.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.whatsdownapp.R;
import com.example.whatsdownapp.model.UserModel;
import com.google.android.material.snackbar.Snackbar;

public class AndroidUtil {

    public static void showToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel model){
        intent.putExtra("username", model.getUsername());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("userId", model.getUserId());
    }

    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        return userModel;
    }

    public static void showSnackBar(View v, String message, Resources resources){
        Snackbar snackbar = Snackbar.make(v, message, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(resources.getColor(R.color.green));
        snackbar.show();

    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);

    }
}
