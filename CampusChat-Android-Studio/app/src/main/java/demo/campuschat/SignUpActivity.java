package demo.campuschat;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import demo.campuschat.model.User;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up); // make sure you have this layout

        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmail); // replace with your actual ID
        editTextPassword = findViewById(R.id.editTextPassword); // replace with your actual ID
        Button buttonSignUp = findViewById(R.id.buttonSignUp); // replace with your actual ID
        database = FirebaseDatabase.getInstance("https://campuschat-13dbc-default-rtdb.europe-west1.firebasedatabase.app");

        buttonSignUp.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Basic validation
            if (!email.isEmpty() && !password.isEmpty()) {
                registerUser(email, password);
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            } else {
                System.out.println();
            }
        });
    }

    private void registerUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // User is successfully registered
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        // Create a User object
                        User user = new User();
                        user.setUserId(firebaseUser.getUid());
                        String emailf = firebaseUser.getEmail();
                        user.setUserEmail(emailf);
                        user.setUserName(emailf.split("@")[0]);


                        // Store the User object in Firebase Database
                        usersRef = database.getReference("users");

                        usersRef.child(firebaseUser.getUid()).setValue(user)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        // Redirect to Home Activity
                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Handle error in storing user data
                                        Toast.makeText(SignUpActivity.this, "Failed to store user data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Handle the registration error
                        Toast.makeText(SignUpActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
