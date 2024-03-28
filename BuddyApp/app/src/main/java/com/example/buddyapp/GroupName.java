package com.example.buddyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GroupName extends AppCompatActivity {

    ImageView iv;
    Button pickbtn,uploadbtn;
    EditText groupnameET;
    ProgressBar progressBar;
    DatabaseReference groupRef,memberRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String currentuid,name,url,phone,about;
    Uri imageUri;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    GroupModal modal;
    DocumentReference dr;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String savetime,savedate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_name);

        iv = findViewById(R.id.iv_group);
        pickbtn = findViewById(R.id.pick_groupiv);
        uploadbtn = findViewById(R.id.save_groupbtn);
        groupnameET = findViewById(R.id.groupname_Et);
        progressBar = findViewById(R.id.pb_groupname);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();

        modal = new GroupModal();
        groupRef = database.getReference("groups").child(currentuid);
        memberRef = database.getReference("members");

        storageReference = firebaseStorage.getInstance().getReference("prof Images");

        pickbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent,8);
            }
        });
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
        savedate=currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime= new SimpleDateFormat("HH:mm:ss");
        savetime = currenttime.format(ctime.getTime());

        String time = savedate + ":"+savetime;

        dr = db.collection("user").document(currentuid);

        dr.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            name = task.getResult().getString("name");
                            about = task.getResult().getString("about");
                            phone = task.getResult().getString("phone");
                            url = task.getResult().getString("url");
                        }else {

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void uploadImage() {
        progressBar.setVisibility(View.VISIBLE);
        final  StorageReference reference = storageReference.child(System.currentTimeMillis() + ".jpg");

        UploadTask uploadTask = reference.putFile(imageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }
        })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    String groupname = groupnameET.getText().toString().trim();
                    String message = URLEncoder.encode("Send First Message");
                    final String address = groupname+currentuid+System.currentTimeMillis();

                    modal.setAddress(address);
                    modal.setAdmin(name);
                    modal.setAdminid(currentuid);
                    modal.setDelete(String.valueOf(System.currentTimeMillis()));
                    modal.setSearch(groupname.toLowerCase());
                    modal.setUrl(downloadUri.toString());
                    modal.setTime(savetime);
                    modal.setGroupname(groupname);
                    modal.setLastm(message);
                    modal.setLastmtime("");

                    String key = groupRef.push().getKey();

                    groupRef.child(address).setValue(modal);

                    MemberModal memberModal = new MemberModal();

                    memberModal.setUid(currentuid);
                    memberModal.setTime("Joined on" + savedate+savetime);
                    memberRef.child(address).child(currentuid).setValue(memberModal);

                    progressBar.setVisibility(View.GONE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(GroupName.this, AddUser.class);
                            intent.putExtra("gname",groupname);
                            intent.putExtra("address",address);
                            intent.putExtra("url",downloadUri);
                            intent.putExtra("admin",name);
                            startActivity(intent);
                        }
                    },1000);
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 8 || resultCode == RESULT_OK || data!=null || data.getData()!=null)
        {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(iv);
        }
    }
}