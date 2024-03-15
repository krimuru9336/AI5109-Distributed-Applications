package com.example.whatsdown;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

public class firebase {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

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

    Map<String, Object> message = new HashMap<>();
    message.put("sender", "user1");
    message.put("content", "Hello world!");
    db.collection("chats").document("chat_id").collection("messages").add(message)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // Message sent successfully
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Error sending message
            }
        });

    StorageReference storageRef = storage.getReference().child("images/" + fileName);
    storageRef.putFile(filePath)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Upload successful
                Uri downloadUri = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                // ... use downloadUri
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Upload failed
            }
        });
}


