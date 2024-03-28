package com.example.buddyapp.groupchat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buddyapp.MessageActivity;
import com.example.buddyapp.MessageMember;
import com.example.buddyapp.MessageViewholder;
import com.example.buddyapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class GroupMessageActivity extends AppCompatActivity {
    String currentid, rowid;
    PojoGroup pojoGroup;
    ArrayList<String> receiver;
    GroupMessagePojo groupMessagePojo;
    RecyclerView recyclerView;
    ImageButton sendbtn, micbtn, cambtn, videoSendBtn;
    TextView username, typingTv;
    EditText messageEt;
    UploadTask uploadTask;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/");
    MessageMember messageMember;
    Boolean typingchecker = false;
    DatabaseReference rootref1, rootRef2, typeref;
    MediaRecorder mediaRecorder;
    public static String fileName = "recorded.3gp";
    String receiver_name, receiver_id, sender, url;
    String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
    GroupMessagePojo groupMessagePojos = new GroupMessagePojo();

    Uri uri;
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final int PICK_GIF = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pojoGroup = (PojoGroup) bundle.getSerializable("groupinfo");
            currentid = bundle.getString("uid");
            rowid = bundle.getString("groupRow");
        } else {
            Toast.makeText(this, "User Missing", Toast.LENGTH_SHORT).show();
        }
        receiver = pojoGroup.getMemberid();
        receiver.remove(currentid);
        groupMessagePojo = new GroupMessagePojo();
        groupMessagePojo.setGroupname(pojoGroup.getGroupName());
        groupMessagePojo.setReceiver(receiver);
        groupMessagePojo.setRowid(rowid);
        groupMessagePojo.setSender(currentid);

        recyclerView = findViewById(R.id.group_message_dis);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(GroupMessageActivity.this));

        messageEt = findViewById(R.id.message_et_grp);
        username = findViewById(R.id.displayGrpName);
        username.setText(pojoGroup.getGroupName());
        sendbtn = findViewById(R.id.imageButtonsend_grp);

        micbtn = findViewById(R.id.imageButtonMic_grp);
        cambtn = findViewById(R.id.cam_sendmessage_grp);
        typingTv = findViewById(R.id.typing_status_grp);
        videoSendBtn = findViewById(R.id.video_send_message_grp);

        //code
        typeref = database.getReference("typing");
        rootref1 = database.getReference("groups").child(rowid).child("message");

        mediaRecorder = new MediaRecorder();
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("onCreate: ", e.getMessage());
        }

        mediaRecorder.setOutputFile(getFilePath());
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        cambtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/gif");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
        micbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });

        typeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(rowid).hasChild(pojoGroup.getGroupName())) {
                    typingTv.setVisibility(View.VISIBLE);
                } else {
                    typingTv.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Typing();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        videoSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the video selection process (e.g., use Intent to pick a video)
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_VIDEO);
            }
        });
    }

    private String getFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file1 = new File(musicDirectory, fileName);
        return file1.getPath();

    }

    private void Typing() {
        typingchecker = true;

        typeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (typingchecker.equals(true)) {
                    if (snapshot.child(rowid).hasChild(pojoGroup.getGroupName())) {
                        typingchecker = false;
                    } else {
                        typeref.child(rowid).child(pojoGroup.getGroupName()).setValue(true);
                        typingchecker = false;
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createDialog() {
        LayoutInflater inflater = LayoutInflater.from(GroupMessageActivity.this);
        View view = inflater.inflate(R.layout.record_layout, null);
        TextView Status = view.findViewById(R.id.tv_record_status);
        Button start = view.findViewById(R.id.btn_start_rc);
        Button stop = view.findViewById(R.id.btn_stop_rc);
        Button sendfile = view.findViewById(R.id.btn_send_rc);

        AlertDialog alertDialog = new AlertDialog.Builder(GroupMessageActivity.this)
                .setView(view)
                .create();
        alertDialog.show();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    Toast.makeText(GroupMessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("Media recorder Testing: ", e.getMessage());
                    throw new RuntimeException(e);
                }
                Status.setText("Audio Recording........");

            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                mediaRecorder.release();
                Status.setText("Recording Stopped...");
            }
        });
        sendfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri audioFile = Uri.fromFile(new File(getFilePath()));
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("Audio Files");
            }
        });
    }

    private void sendMessage() {
        String message = messageEt.getText().toString();

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String savedate = currentdate.format(cdate.getTime());
        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
        final String savetime = currenttime.format(ctime.getTime());

        String time = savedate + ":" + savetime;


        if (message.isEmpty()) {
            Toast.makeText(GroupMessageActivity.this, "Cannot send empty Message", Toast.LENGTH_SHORT).show();
        } else {
            groupMessagePojo.setDate(savedate);
            groupMessagePojo.setTime(savetime);
            groupMessagePojo.setMessage(message);
            groupMessagePojo.setSender(currentid);
            groupMessagePojo.setType("t");

            String id = rootref1.push().getKey();
            rootref1.child(id).setValue(groupMessagePojo);
            messageEt.setText("");
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        typeref.child(rowid).child(pojoGroup.getGroupName()).removeValue();
    }

//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            uri = data.getData();
//            String url = uri.toString();
//            Intent intent = new Intent(GroupMessageActivity.this, SentImage.class);
//            intent.putExtra("u", url);
//            intent.putExtra("n", receiver_name);
//            intent.putExtra("ruid", rowid);
//            intent.putExtra("suid", sender);
//            startActivity(intent);
//        } else if (requestCode == PICK_VIDEO && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri videoUri = data.getData();
//            String videoUrl = videoUri.toString();
//            Intent intent = new Intent(GroupMessageActivity.this, SentVideo.class);
//            intent.putExtra("v", videoUrl);
//            intent.putExtra("n", receiver_name);
//            intent.putExtra("ruid", rowid);
//            intent.putExtra("suid", sender);
//            startActivity(intent);
//        }
//    }


    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<GroupMessagePojo> options =
                new FirebaseRecyclerOptions.Builder<GroupMessagePojo>()
                        .setQuery(rootref1, GroupMessagePojo.class)
                        .build();
        FirebaseRecyclerAdapter<GroupMessagePojo, MessageViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<GroupMessagePojo, MessageViewholder>(options) {
                    /**
                     * @param holder
                     * @param position
                     * @param model    the model object containing the data that should be used to populate the view.
                     */
                    @Override
                    protected void onBindViewHolder(@NonNull MessageViewholder holder, int position, @NonNull GroupMessagePojo model) {
                        holder.setGroupMessage(getApplication(), model);
                        String audio = getItem(position).getAudio();
                        Long delete = getItem(position).getDelete();
                        String type = getItem(position).getType();
                        String imageuri = getItem(position).getImage();
                        String date = getItem(position).getDate();
                        String time = getItem(position).getTime();
                        String sendername = getItem(position).getSender();
                        String video = getItem(position).getVideo();
                        String gifUrl = getItem(position).getGifUrl();


                        holder.play_sender.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MediaPlayer mediaPlayer = new MediaPlayer();
                                holder.play_sender.setImageResource(R.drawable.baseline_pause_white);
                                try {
                                    mediaPlayer.setDataSource(audio);
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();
                                    holder.play_sender.setClickable(false);
                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            holder.play_sender.setImageResource(R.drawable.play_white);
                                            mediaPlayer.stop();
                                            holder.play_sender.setClickable(true);
                                        }
                                    });
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
//
                        holder.senderTv.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                Log.d("onLongClick Postion ----: ",getRef(position).getKey()+" "+model.getRowid() );
                                createMessageDialog(model.getRowid(),getRef(position).getKey(),delete, type, imageuri, date, time, sendername, audio);
                                return false;
                            }
                        });

                        holder.iv_sender.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                createMessageDialog(model.getRowid(),getRef(position).getKey(),delete, type, imageuri, date, time, sendername, audio);
                                return false;
                            }
                        });

                        holder.play_receiver.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MediaPlayer mediaPlayer = new MediaPlayer();
                                holder.play_receiver.setImageResource(R.drawable.baseline_pause_black);
                                try {
                                    mediaPlayer.setDataSource(audio);
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();
                                    holder.play_receiver.setClickable(false);
                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            holder.play_receiver.setImageResource(R.drawable.play_black);
                                            mediaPlayer.stop();
                                            holder.play_receiver.setClickable(true);
                                        }
                                    });
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });

                    }


                    @NonNull
                    @Override
                    public MessageViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.message_layout, parent, false);
                        return new MessageViewholder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void createMessageDialog(String rowid1,String rowid2, Long delete, String type, String imageuri, String date, String time, String sendername, String audio)  {
        final Dialog dialog = new Dialog(GroupMessageActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.message_group_option);

        TextView unSend = dialog.findViewById(R.id.unsend_groupid);
        TextView details = dialog.findViewById(R.id.details_groupid);
        TextView downloads = dialog.findViewById(R.id.download_groupid);
        TextView Edit = dialog.findViewById(R.id.edit_groupid);
        TextView dateMo = dialog.findViewById(R.id.date_groupmo);
        TextView timeMo = dialog.findViewById(R.id.time_groupmo);


        downloads.setVisibility(type.equals("t") ? View.GONE : View.VISIBLE);

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateMo.setVisibility(View.VISIBLE);
                timeMo.setVisibility(View.VISIBLE);
                dateMo.setText("Date :" + date);
                timeMo.setText("Time :" + time);

            }
        });

        unSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMessage(rowid1,rowid2);
                Log.d("DELETE MESAGE: ", "47555");
                dialog.dismiss();
            }
        });

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle edit message functionality here
                // For example, you can show an EditText in a dialog for editing the message
                showEditDialog(rowid1,rowid2);
                dialog.dismiss();
            }
        });

        downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("i")) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageuri));
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                            DownloadManager.Request.NETWORK_MOBILE);
                    request.setTitle("Download");
                    request.setDescription("Downloading image...");
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, sendername + System.currentTimeMillis() + ".jpg");
                    DownloadManager manager = (DownloadManager) GroupMessageActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                    Toast.makeText(GroupMessageActivity.this, "Downloading", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else if (type.equals("a")) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageuri));
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                            DownloadManager.Request.NETWORK_MOBILE);
                    request.setTitle("Download");
                    request.setDescription("Downloading audio...");
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, sendername + System.currentTimeMillis() + ".mp3");
                    DownloadManager manager = (DownloadManager) GroupMessageActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                    Toast.makeText(GroupMessageActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();
                    StorageReference reference1 = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                    reference1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(GroupMessageActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }

    private void deleteMessage(String rowid1,String messageId) {
        //DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("group").child(rowid1).child("message").child(messageId);
        Log.d("Message: ", messageId);
        rootref1.child(messageId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("DeleteTextMessage", "Text message deleted successfully");
                        Toast.makeText(GroupMessageActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GroupMessageActivity.this, "Not Deleted", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Method to show a dialog for editing the message
    private void showEditDialog(String rowid1,String messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupMessageActivity.this);
        builder.setTitle("Edit Message");

        // Set up the input
        final EditText input = new EditText(GroupMessageActivity.this);
        builder.setView(input);

        // Set up buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newMessage = input.getText().toString();
                groupMessagePojo.setMessage(newMessage);


                if (!newMessage.isEmpty()) {
                    editMessage(rowid1,messageId, newMessage);
                    Log.d("Mesage Hello: ", newMessage);
                } else {
                    Toast.makeText(GroupMessageActivity.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the dialog
        builder.show();
    }

    private void editMessage(String rowid1,String messageId, String newMessage) {
        Log.d("editMessage:------ ",newMessage);
        //DatabaseReference messagesRef = FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("group").child(rowid1).child("message").child(messageId);
        HashMap<String, Object> updatedMessage = new HashMap<>(); // Use Object instead of String if you're updating multiple fields
        updatedMessage.put("message", newMessage); // Assuming "message" is the field you want to update

        rootref1.child(messageId).setValue(groupMessagePojo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(GroupMessageActivity.this, "Message Updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GroupMessageActivity.this, "Message not Updated", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}