package com.example.buddyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UpdatePhoto extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;
    ImageView imageView;
    UploadTask uploadTask;
    ProgressBar progressBar;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://buddyapp-5a091.appspot.com");
    String currentuid;
    Button button;
    private final static int PICK_IMAGE = 1;
    Uri imageUri,url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_photo);

        imageView = findViewById(R.id.iv_updatephoto);
        button = findViewById(R.id.btn_updatePhoto);
        progressBar = findViewById(R.id.pv_updatephoto);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();

        storageReference = firebaseStorage.getReference("Profile Images");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImage();
            }
        });

    }
    private String getFileExt(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode==PICK_IMAGE || resultCode==RESULT_OK || data != null || data.getData()!=null){
                imageUri=data.getData();
                Picasso.get().load(imageUri).into(imageView);

            }
        }catch (Exception e){
            Toast.makeText(UpdatePhoto.this,"Error"+e,Toast.LENGTH_SHORT).show();
        }
    }
    private void updateImage() {
        final StorageReference reference1= storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUri));
        uploadTask=reference1.putFile(imageUri);

        Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                try {
                    if(task.isSuccessful()) {
                        //throw  task.getException();
                        return reference1.getDownloadUrl();
                    }
                    else {
                        throw  task.getException();
                    }
                }
                catch (Exception exception){
                    Log.d("Error:",exception.getMessage());
                }
                finally {
                    return reference1.getDownloadUrl();
                }

                //Toast.makeText(CreateProfile.this,"hello",Toast.LENGTH_SHORT).show();
                //return reference.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    Uri downloadUri=task.getResult();

                    final DocumentReference sDoc = db.collection("user").document(currentuid);
                    db.runTransaction(new Transaction.Function<Void>() {
                                @Override
                                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                    //DocumentSnapshot snapshot = transaction.get(sfDocRef);

                                    transaction.update(sDoc, "url",downloadUri );

                                    // Success
                                    return null;
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(UpdatePhoto.this, "updated", Toast.LENGTH_SHORT).show();

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference db1,db2;

                                    Map<String, Object>profile = new HashMap<>();
                                    profile.put("url",downloadUri.toString());

                                    db1 = database.getReference("All posts");

                                    Query query = db1.orderByChild("uid").equalTo(currentuid);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                                snapshot1.getRef().updateChildren(profile)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(UpdatePhoto.this, "Done", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    db2 = database.getReference("All Questions");

                                    Query query1 = db2.orderByChild("uid").equalTo(currentuid);
                                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                                snapshot1.getRef().updateChildren(profile)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(UpdatePhoto.this, "Done", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UpdatePhoto.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });

}


    public void chooseImage(View view) {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);

    }
}