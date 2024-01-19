package com.example.whatsdown;

public class firebase {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

// Inside your registration method
mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Registration successful
                FirebaseUser user = mAuth.getCurrentUser();
                // You can do something with the user, e.g., update UI, show a welcome message, etc.
            } else {
                // If registration fails, display a message to the user.
                Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        }
    });


mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Login successful
                FirebaseUser user = mAuth.getCurrentUser();
                // You can do something with the user, e.g., update UI, navigate to another activity, etc.
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        }
    });


mAuth.signOut();
}
