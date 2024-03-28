package com.example.buddyapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.*;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

public class UpdateProfile extends AppCompatActivity {
    EditText etname,etbio,etprofession,etwebsite,etemail;
    Button btnsave;
    String currentuid;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseFirestore db =FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_updateprofile);
        btnsave=findViewById(R.id.btn_up);
        etname=findViewById(R.id.et_name_up);
        etbio=findViewById(R.id.et_bio_up);
        etprofession=findViewById(R.id.et_profession_up);
        etwebsite=findViewById(R.id.et_website_up);
        etemail=findViewById(R.id.et_email_up);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        currentuid= user.getUid();
        documentReference=db.collection("user").document(currentuid);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    String nameResult = task.getResult().getString("name");
                    String bioResult = task.getResult().getString("bio");
                    String emailResult = task.getResult().getString("email");
                    String webResult = task.getResult().getString("web");
                    String urlResult = task.getResult().getString("url");
                    String profResult = task.getResult().getString("prof");

                    etname.setText(nameResult);
                    etbio.setText(bioResult);
                    etemail.setText(emailResult);
                    etprofession.setText(profResult);
                    etwebsite.setText(webResult);
                }else {
                    Toast.makeText(UpdateProfile.this, "No Profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateProfile() {
        String name= etname.getText().toString();
        String bio=etbio.getText().toString();
        String web=etwebsite.getText().toString();
        String prof=etprofession.getText().toString();
        String email=etemail.getText().toString();

        final DocumentReference sDoc = db.collection("user").document(currentuid);
        db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        //DocumentSnapshot snapshot = transaction.get(sfDocRef);

                        transaction.update(sDoc, "name",name );
                        transaction.update(sDoc, "bio",bio );
                        transaction.update(sDoc, "email",email );
                        transaction.update(sDoc, "web",web );
                        transaction.update(sDoc, "prof",prof );

                        // Success
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateProfile.this, "updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
