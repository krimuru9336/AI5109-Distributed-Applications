package com.example.chatapplication;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.github.appintro.AppIntro2;
import com.github.appintro.AppIntroCustomLayoutFragment;
import com.github.appintro.AppIntroPageTransformerType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.Nullable;

public class OnBoardingActivity extends AppIntro2 {

    private Screens screens;
    private boolean isTakeTour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screens = new Screens(getApplicationContext());

        SessionManager.get().setOnBoardingDone(true);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            screens.showClearTopScreen(MainActivity.class);
        } else {
            screens.showClearTopScreen(LoginActivity.class);
        }
    }