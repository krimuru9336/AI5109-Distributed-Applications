package com.example.buddyapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CreateProfile extends AppCompatActivity {

    EditText etname,etbio,etprofession,etwebsite,etemail;
    Button btnsave;
    ImageView imageView;
    ProgressBar progressBar;
    Uri imageUri;
    UploadTask uploadTask;
    StorageReference reference;
    FirebaseDatabase database=FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference databaseReference;
    FirebaseFirestore db =FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    private static final int PICK_IMAGE=1;
    AllUserMember allUserMember;
    String CurrentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allUserMember=new AllUserMember();
        setContentView(R.layout.activity_createprofile);
        imageView=findViewById(R.id.iv_cp);
        btnsave=findViewById(R.id.btn_cp);
        etname=findViewById(R.id.et_name_cp);
        etbio=findViewById(R.id.et_bio_cp);
        etprofession=findViewById(R.id.et_profession_cp);
        etwebsite=findViewById(R.id.et_website_cp);
        etemail=findViewById(R.id.et_email_cp);
        progressBar=findViewById(R.id.progressbar_cp);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        CurrentUserId=user.getUid();
        Toast.makeText(this, CurrentUserId, Toast.LENGTH_SHORT).show();
        Log.d("uid from db",CurrentUserId);
        documentReference=db.collection("user").document(CurrentUserId);
        reference= FirebaseStorage.getInstance().getReference("Profile Images");
        databaseReference=database.getReference("All Users");
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });
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
            Log.d( "Eroor",e.getMessage());
            Toast.makeText(CreateProfile.this,"Error"+e,Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExt(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    private void uploadData() {

        String name= etname.getText().toString();
        String bio=etbio.getText().toString();
        String web=etwebsite.getText().toString();
        String prof=etprofession.getText().toString();
        String email=etemail.getText().toString();
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(bio) || TextUtils.isEmpty(web) ||TextUtils.isEmpty(prof) ||TextUtils.isEmpty(email) || imageUri != null ){

            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference1= reference.child(System.currentTimeMillis()+"."+getFileExt(imageUri));
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
                        Toast.makeText(CreateProfile.this,exception.getMessage(),Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CreateProfile.this,"hello",Toast.LENGTH_SHORT).show();
                        Uri downloadUri=task.getResult();
                        Map<String,String>profile = new HashMap<>();
                        profile.put("name",name);
                        profile.put("prof",prof);
                        profile.put("url",downloadUri.toString());
                        profile.put("email",email);
                        profile.put("web",web);
                        profile.put("bio",bio);
                        profile.put("uid",CurrentUserId);
                        profile.put("privacy","Public");

                        allUserMember.setName(name);
                        allUserMember.setProf(prof);
                        allUserMember.setUrl(downloadUri.toString());
                        allUserMember.setUid(CurrentUserId);

                        databaseReference.child(CurrentUserId).setValue(allUserMember);
                        documentReference.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(CreateProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(CreateProfile.this, Fragment1.class);
                                        startActivity(intent);
                                    }
                                },2000);
                            }
                        });
                    }
                }
            });
        }else {
            Toast.makeText(this, "Please Fill all the fields", Toast.LENGTH_SHORT).show();
        }
    }

}
