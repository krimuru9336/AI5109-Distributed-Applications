package com.example.buddyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SentVideo extends AppCompatActivity {
    String url, receiver_name, sender_uid, receiver_uid;
    //VideoView videoView;
    Uri videoUrl;
    ProgressBar progressBar;
    Button button;
    TextView textView;
    UploadTask uploadTask;

    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    DatabaseReference rootRef1, rootRef2;
    FirebaseDatabase database = FirebaseDatabase
            .getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/");

    private Uri uri;
    MessageMember messageMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_video);

        messageMember = new MessageMember();
        storageReference = firebaseStorage.getInstance().getReference("Message Videos");

        //videoView = findViewById(R.id.video_view_sent);
        button = findViewById(R.id.btn_sendvideo);
        progressBar = findViewById(R.id.pb_sendvideo);
        textView = findViewById(R.id.tv_dont_video);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("v");
            receiver_name = bundle.getString("n");
            receiver_uid = bundle.getString("ruid");
            sender_uid = bundle.getString("suid");

        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        videoUrl = Uri.parse(url);
        //videoView.setVideoURI(videoUrl);

        rootRef1 = database.getReference("Message").child(sender_uid).child(receiver_uid);
        rootRef2 = database.getReference("Message").child(receiver_uid).child(sender_uid);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVideo();
                textView.setVisibility(View.VISIBLE);
            }
        });

    }

    private String getFileExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    private void sendVideo() {
        if (videoUrl!= null) {
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference1 = storageReference.child
                    (System.currentTimeMillis() + "." + getFileExt(videoUrl));
            uploadTask = reference1.putFile(videoUrl);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference1.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Calendar cdate = Calendar.getInstance();
                        SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
                        final  String savedate=currentdate.format(cdate.getTime());
                        Calendar ctime = Calendar.getInstance();
                        SimpleDateFormat currenttime= new SimpleDateFormat("HH:mm:ss");
                        final String savetime = currenttime.format(ctime.getTime());

                        String time = savedate + ":"+savetime;

                        long deletetime = System.currentTimeMillis();

                        messageMember.setDate(savedate);
                        messageMember.setTime(savetime);
                        messageMember.setVideo(downloadUri.toString());
                        messageMember.setReceiveruid(receiver_uid);
                        messageMember.setSenderuid(sender_uid);
                        messageMember.setDelete(deletetime);
                        messageMember.setType("v");

                        String id =  rootRef1.push().getKey();
                        rootRef1.child(id).setValue(messageMember);

                        String id1 =  rootRef2.push().getKey();
                        rootRef2.child(id1).setValue(messageMember);

                        progressBar.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                    }
                }

            });

        } else {
            Toast.makeText(this, "Please select something", Toast.LENGTH_SHORT).show();
        }
    }
}