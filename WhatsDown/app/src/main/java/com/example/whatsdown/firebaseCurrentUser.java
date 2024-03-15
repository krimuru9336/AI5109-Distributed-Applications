package com.example.whatsdown;

public class firebaseCurrentUser {
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
if (currentUser == null) {
        // Redirect to the authentication screen
        // Example: startActivity(new Intent(this, AuthenticationActivity.class));
        finish(); // Finish the current activity to prevent the user from coming back to the chat screen without authentication
    }

}
