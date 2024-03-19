/**
 Author info
 Md Firoj Kabir
 1300934
 **/

package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateAccountSettings;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private StorageReference UserProfileImagesRef;
    private String currentUserID;
    private Toolbar SettingsToolBar;

    private static final int GalleryPick = 1;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingBar = new ProgressDialog(this);


        InitializeFields();

        userName.setVisibility(View.INVISIBLE);

        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        RetrieveUserInfo();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
    }

    private void InitializeFields() {
        updateAccountSettings = (Button) findViewById(R.id.update_settings_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        userProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);
        loadingBar = new ProgressDialog(this);

        SettingsToolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account settings");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick && resultCode==RESULT_OK && data!=null) {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Set profile image");
                loadingBar.setMessage("Please wait while updating your profile image..");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                            RootRef.child("Users").child(currentUserID).child("image")
                                    .setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SettingsActivity.this, "Image saved to DB successfully", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            } else {
                                                String message = task.getException().toString();
                                                Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
//        {
//            Uri ImageUri = data.getData();
//
//            CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1, 1)
//                    .start(this);
//        }
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
//        {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//            if (resultCode == RESULT_OK)
//            {
//                loadingBar.setTitle("Set Profile Image");
//                loadingBar.setMessage("Please wait, your profile image is updating...");
//                loadingBar.setCanceledOnTouchOutside(false);
//                loadingBar.show();
//
//                Uri resultUri = result.getUri();
//
//
//                final StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");
//
//                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
//                    {
//                        if (task.isSuccessful())
//                        {
//                            Toast.makeText(SettingsActivity.this, "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();
//
//
//                               final String downloaedUrl = task.getResult().getMetadata().getReference().getDownloadUrl().toString();
//
//                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    // Got the download URL for 'users/me/profile.png'
//                                    final String downloaedUrl =uri.toString();
//
//                                    RootRef.child("Users").child(currentUserID).child("image")
//                                            .setValue(downloaedUrl)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task)
//                                                {
//                                                    if (task.isSuccessful())
//                                                    {
//                                                        Toast.makeText(SettingsActivity.this, "Image save in Database, Successfully...", Toast.LENGTH_SHORT).show();
//                                                        loadingBar.dismiss();
//                                                    }
//                                                    else
//                                                    {
//                                                        String message = task.getException().toString();
//                                                        Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
//                                                        loadingBar.dismiss();
//                                                    }
//                                                }
//                                            });
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception exception) {
//                                    // Handle any errors
//                                }
//                            });
//
//
//
//                        }
//                        else
//                        {
//                            String message = task.getException().toString();
//                            Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
//                            loadingBar.dismiss();
//                        }
//                    }
//                });
//            }
//        }
//
//    }

    private void UpdateSettings() {
        String setUserName = userName.getText().toString();
        String setStatus = userStatus.getText().toString();

        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(this, "Your username, please! ", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setStatus)) {
            Toast.makeText(this, "Update your status!", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> profileMap = new HashMap<>();
//            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUserName);
            profileMap.put("status", setStatus);
            RootRef.child("Users").child(currentUserID).setValue(profileMap)
//            RootRef.child("Users").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void RetrieveUserInfo() {
        RootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if ((snapshot.exists()) && (snapshot.hasChild("name") && (snapshot.hasChild("image")))) {
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveStatus = snapshot.child("status").getValue().toString();
                            String retrieveProfileImage = snapshot.child("image").getValue().toString();

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);

//                            Picasso.get().load(retrieveProfileImage).into(userProfileImage);
                        }
                        else if ((snapshot.exists()) && (snapshot.hasChild("name"))) {
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveStatus = snapshot.child("status").getValue().toString();

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);
                        }
                        else {
                            userName.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this, "Update your profile information!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}