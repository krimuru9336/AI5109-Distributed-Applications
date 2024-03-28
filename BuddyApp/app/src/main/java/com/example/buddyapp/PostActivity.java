package com.example.buddyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    Button btnchoosefile,btnuploadfile;
    private Uri selectedUri;
    private static  final  int PICK_FILE = 1;
    UploadTask uploadTask;
    EditText etdesc;
    VideoView videoView;
    String url,name;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference db1,db2,db3;

    MediaController mediaController;
    String type;
    Postmember postmember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postmember = new Postmember();
        setContentView(R.layout.activity_post);
        mediaController = new MediaController(this);
        progressBar = findViewById(R.id.pb_post);
        imageView = findViewById(R.id.iv_post);
        videoView = findViewById(R.id.vv_post);
        btnchoosefile = findViewById(R.id.btn_choosefile_post);
        btnuploadfile = findViewById(R.id.btn_uploadfile_post);
        etdesc = findViewById(R.id.et_desc_post);

        storageReference = FirebaseStorage.getInstance().getReference("User posts");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid= user.getUid();

        db1=database.getReference("All images").child(currentuid);
        db2=database.getReference("All videos").child(currentuid);
        db3=database.getReference("All posts");

        btnuploadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPost();
            }
        });
        btnchoosefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    private void chooseImage() {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        startActivityForResult(intent,PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FILE || resultCode == RESULT_OK ||
        data!=null||data.getData() !=null){
            selectedUri =data.getData();
            if(selectedUri.toString().contains("image")) {
                Picasso.get().load(selectedUri).into(imageView);
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                type = "iv";
            } else if (selectedUri.toString().contains("video")) {
                videoView.setMediaController(mediaController);
                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                videoView.setVideoURI(selectedUri);
                videoView.start();
                type = "vv";
            }else{
                Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String getFileExt(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid= user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("user").document(currentuid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    name= task.getResult().getString("name");
                    url = task.getResult().getString("url");
                    Log.d("uid on 136",name);
                }else {
                    Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void doPost() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid= user.getUid();

        String desc = etdesc.getText().toString();

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
        final  String savedate=currentdate.format(cdate.getTime());
        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime= new SimpleDateFormat("HH:mm:ss");
        final String savetime = currenttime.format(ctime.getTime());

        String time = savedate + ":"+savetime;

        if(TextUtils.isEmpty(name) || selectedUri !=null){
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference1= storageReference.child(System.currentTimeMillis()+"."+getFileExt(selectedUri));
            uploadTask=reference1.putFile(selectedUri);

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
                        Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    finally {
                        return reference1.getDownloadUrl();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Uri downloadUri=task.getResult();
                        if(type.equals("iv")){
                            postmember.setDesc(desc);
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            postmember.setTime(time);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setType("iv");

                            //for image
                            String id =  db1.push().getKey();
                            db1.child(id).setValue(postmember);
                            //For Both
                            String id1 = db3.push().getKey();
                            db3.child(id1).setValue(postmember);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                        } else if (type.equals("vv")) {
                            postmember.setDesc(desc);
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            postmember.setTime(time);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setType("vv");

                            //for video
                            String id3 =  db2.push().getKey();
                            db1.child(id3).setValue(postmember);
                            //For Both
                            String id4 = db3.push().getKey();
                            db3.child(id4).setValue(postmember);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
        }else {
            Toast.makeText(this, "Please Fill all the fields", Toast.LENGTH_SHORT).show();
        }
    }
}