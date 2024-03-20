package com.example.chitchat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Adapters.MessageAdapter;
import Models.AllMethods;
import Models.ChatRoom;
import Models.Message;
import Models.Message.MessageType;
import Models.MessageRes;
import Models.User;

public class GroupChatActivity extends AppCompatActivity  {


    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    MessageAdapter adapter;
    User u;
    ArrayList<Message> messages;

    RecyclerView rvMessage;
    ObjectMapper objectMapper = new ObjectMapper();
    EditText uInput;
    ImageButton sendBtn, attachBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        init();

    }

    private void  init( ){
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        u = new User();

        rvMessage = findViewById(R.id.messages);
        uInput = findViewById(R.id.userMessage);
        sendBtn = findViewById(R.id.SendButton);
        attachBtn = findViewById(R.id.UploadButton);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(uInput.getText())){
                    Date currentDate = new Date();

                    // Format the date and time
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String formattedDateTime = dateFormat.format(currentDate);
                    Message message = new Message(uInput.getText().toString(), formattedDateTime,u.getName(),AllMethods.uId, Message.MessageType.Text);
                    uInput.setText("");

                    reference.push().setValue(message);
                }
            }
        });

        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFilePicker();
            }
        });

        messages = new ArrayList<>();
    }

    private static final int PICK_FILE_REQUEST_CODE = 1;
    private ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri selectedFileUri = data.getData();

                        // Now you have the URI of the selected file, proceed with file upload
                        uploadFile(selectedFileUri);
                    }
                }
            }
    );

    private void uploadFile(Uri fileUri) {
        // Reference to the Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a reference to a specific location within the "files" folder, using a unique filename
        String fileName = "file_" + System.currentTimeMillis();
        StorageReference filesRef = storageRef.child("files/" + fileName);
        ContentResolver contentResolver = getContentResolver();
        String mimeType = contentResolver.getType(fileUri);
        MessageType type;
        if (mimeType != null) {
            if (mimeType.startsWith("image/")) {
                type = MessageType.Image;
            } else if (mimeType.startsWith("video/")) {
                type = MessageType.Video;
            } else if (mimeType.equals("image/gif")) {
                type = MessageType.GIF;
            } else {
                type = MessageType.Text;
            }
        } else {
            type = null;
        }


        // Upload the file to Firebase Storage
        UploadTask uploadTask = filesRef.putFile(fileUri);

        // Listen for the success or failure of the upload
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // File uploaded successfully
            filesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Get the download URL of the uploaded file
                String fileUrl = uri.toString();

                // Now, you can save this URL to the Realtime Database along with other message details
                SaveMediaMessageToDatabase(fileUrl,type);

            }).addOnFailureListener(exception -> {
                // Handle any errors that occurred during URL retrieval
                Log.i("failure",exception.toString());
            });
        }).addOnFailureListener(exception -> {
            Log.i("failure",exception.toString());
        });
    }

    void  SaveMediaMessageToDatabase( String uri, MessageType type){
        Date currentDate = new Date();

        // Format the date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDateTime = dateFormat.format(currentDate);
        Message message = new Message(uInput.getText().toString(), formattedDateTime,u.getName(),AllMethods.uId, Message.MessageType.Text);
        uInput.setText("");
        message.setType(type);
        message.setMedia_url(uri);
        reference.push().setValue(message);
    }

    private void OpenFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // All file types
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        filePickerLauncher.launch(intent);
    }




    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser firebaseUser = auth.getCurrentUser();
        u.setUid(firebaseUser.getUid());
        u.setEmail(firebaseUser.getEmail());

        database.getReference("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                u = snapshot.getValue(User.class);
                u.setUid(firebaseUser.getUid());
                AllMethods.name = u.getName();
                AllMethods.uId = firebaseUser.getUid();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        reference = database.getReference("chat_rooms").child(AllMethods.chatroomKey).child("messages");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                ArrayList<HashMap<String,Object>> m = (ArrayList<HashMap<String, Object>>) snapshot.getValue();
//                messages = new ArrayList<>();
//                for (HashMap<String,Object> map:m
//                     ) {
//                    messages.add(objectMapper.convertValue(map,Message.class));
//                }
//                displayMessages(messages);
                Message m = snapshot.getValue(Message.class);
                m.setKey(snapshot.getKey());
                if(!messages.stream().anyMatch(o->m.getKey().equals(o.getKey()))){
                    messages.add(m);
                    displayMessages(messages);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                ArrayList<HashMap<String,Object>> m = (ArrayList<HashMap<String, Object>>) snapshot.getValue();
//                int i = 0,count = 0;
//                for (Message map:messages
//                ) {
//                    Message newM =objectMapper.convertValue(map,Message.class);
//                    if(map.getKey().equals(newM.getKey())){
//                        count = i;
//                        break;
//                    }
//                    i++;
//                }
//                messages.remove(count);
//                assert m != null;
//                messages.add(count,objectMapper.convertValue(m.get(0),Message.class));
//
//                displayMessages(messages);


//                ArrayList<HashMap<String,Object>> m = (ArrayList<HashMap<String, Object>>) snapshot.getValue();
//                messages = new ArrayList<>();
//                for (HashMap<String,Object> map:m
//                ) {
//                    messages.add(objectMapper.convertValue(map,Message.class));
//                }
//                displayMessages(messages);

                int i = 0,count = 0;
                Message m = snapshot.getValue(Message.class);
                m.setKey(snapshot.getKey());
                for (Message marr : messages){
                    if(marr.getKey().equals(m.getKey())){
                        count = i;
                    }
                    i++;
                }
                messages.remove(count);
                m.setKey(snapshot.getKey());
                messages.add(count,m);
                displayMessages(messages);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

//                ArrayList<HashMap<String,Object>> m = (ArrayList<HashMap<String, Object>>) snapshot.getValue();
//                int i = 0,count = 0;
//                for (Message map:messages
//                ) {
//                    Message newM =objectMapper.convertValue(map,Message.class);
//                    if(map.getKey().equals(newM.getKey())){
//                        count = i;
//                        break;
//                    }
//                    i++;
//                }
//                messages.remove(count);
//                displayMessages(messages);


//                ArrayList<HashMap<String,Object>> m = (ArrayList<HashMap<String, Object>>) snapshot.getValue();
//                messages = new ArrayList<>();
//                for (HashMap<String,Object> map:m
//                ) {
//                    messages.add(objectMapper.convertValue(map,Message.class));
//                }
//                displayMessages(messages);

                int i = 0,count = 0;
                Message m = snapshot.getValue(Message.class);
                m.setKey(snapshot.getKey());
                for (Message marr : messages){
                    Log.i("0","value matched : "+marr.getKey()+" : "+m.getKey());
                    if(marr.getKey().equals(m.getKey())){

                        count = i;
                    }
                    i++;
                }
                messages.remove(count);
                displayMessages(messages);
//                messages.add(count,m);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        messages = new ArrayList<>();
    }

    private  void  displayMessages(ArrayList<Message> messages){
        rvMessage.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
        adapter = new MessageAdapter(GroupChatActivity.this,messages,reference);
        rvMessage.setAdapter(adapter);

    }
}

