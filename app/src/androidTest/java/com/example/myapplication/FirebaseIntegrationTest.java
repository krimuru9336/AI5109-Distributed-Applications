package com.example.myapplication;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FirebaseIntegrationTest {
    @Test
    public void firebaseInitialized() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();     
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();     
        assertNotNull("Firebase Authentication is not initialized", firebaseAuth);
    }
}
