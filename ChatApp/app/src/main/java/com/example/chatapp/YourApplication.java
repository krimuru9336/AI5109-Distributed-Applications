package com.example.chatapp;

import android.app.Application;
import com.google.firebase.FirebaseApp;
//import com.google.firebase.appcheck.AppCheck;
import com.google.firebase.appcheck.AppCheckProviderFactory;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
//import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;


public class YourApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance());
    }
}