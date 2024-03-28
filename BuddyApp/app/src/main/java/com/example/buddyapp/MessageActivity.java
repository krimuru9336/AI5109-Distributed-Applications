package com.example.buddyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

public class MessageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView imageView;
    ImageButton sendbtn, micbtn, cambtn, videoSendBtn,camgifbtn;
    TextView username, typingTv;
    EditText messageEt;
    UploadTask uploadTask;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference rootref1, rootRef2, typeref;
    MessageMember messageMember;
    Boolean typingchecker = false;
    String receiver_name, receiver_id, sender_uid, url;
    MediaRecorder mediaRecorder;
    public static String fileName = "recorded.3gp";
    String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;


    Uri uri;
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final int PICK_GIF = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_message);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("u");
            receiver_name = bundle.getString("n");
            receiver_id = bundle.getString("uid");

        } else {
            Toast.makeText(this, "User Missing", Toast.LENGTH_SHORT).show();
        }
        messageMember = new MessageMember();

        recyclerView = findViewById(R.id.rv_message);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));

        imageView = findViewById(R.id.iv_message);
        messageEt = findViewById(R.id.message_et);
        username = findViewById(R.id.username_messageTv);
        sendbtn = findViewById(R.id.imageButtonsend);

        micbtn = findViewById(R.id.imageButtonMic);
        cambtn = findViewById(R.id.cam_sendmessage);
        camgifbtn = findViewById(R.id.cam_sendgif);
        typingTv = findViewById(R.id.typing_status);
        videoSendBtn = findViewById(R.id.video_send_message);

        Picasso.get().load(url).into(imageView);
        username.setText(receiver_name);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        sender_uid = user.getUid();

        rootref1 = database.getReference("Message").child(sender_uid).child(receiver_id);
        rootRef2 = database.getReference("Message").child(receiver_id).child(sender_uid);
        typeref = database.getReference("typing");


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
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
        camgifbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("gif/*");
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
                if (snapshot.child(sender_uid).hasChild(receiver_id)) {
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

    private void Typing() {
        typingchecker = true;

        typeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (typingchecker.equals(true)) {
                    if (snapshot.child(receiver_id).hasChild(sender_uid)) {
                        typingchecker = false;
                    } else {
                        typeref.child(receiver_id).child(sender_uid).setValue(true);
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
        LayoutInflater inflater = LayoutInflater.from(MessageActivity.this);
        View view = inflater.inflate(R.layout.record_layout, null);
        TextView Status = view.findViewById(R.id.tv_record_status);
        Button start = view.findViewById(R.id.btn_start_rc);
        Button stop = view.findViewById(R.id.btn_stop_rc);
        Button sendfile = view.findViewById(R.id.btn_send_rc);

        AlertDialog alertDialog = new AlertDialog.Builder(MessageActivity.this)
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
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                final StorageReference reference1 = storageReference.child(System.currentTimeMillis() + fileName);
                uploadTask = reference1.putFile(audioFile);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
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
                            SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                            final String savedate = currentdate.format(cdate.getTime());
                            Calendar ctime = Calendar.getInstance();
                            SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
                            final String savetime = currenttime.format(ctime.getTime());

                            String time = savedate + ":" + savetime;
                            long deletetime = System.currentTimeMillis();
                            messageMember.setDate(savedate);
                            messageMember.setTime(savetime);
                            messageMember.setAudio(downloadUri.toString());
                            messageMember.setReceiveruid(receiver_id);
                            messageMember.setSenderuid(sender_uid);
                            messageMember.setDelete(deletetime);
                            messageMember.setType("a");


                            String id = rootref1.push().getKey();
                            rootref1.child(id).setValue(messageMember);

                            String id1 = rootRef2.push().getKey();
                            rootRef2.child(id1).setValue(messageMember);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDialog.dismiss();
                                    Toast.makeText(MessageActivity.this, "File Sent", Toast.LENGTH_SHORT).show();
                                }
                            }, 1000);

                        } else {
                            Toast.makeText(MessageActivity.this, "", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<MessageMember> options =
                new FirebaseRecyclerOptions.Builder<MessageMember>()
                        .setQuery(rootref1, MessageMember.class)
                        .build();
        FirebaseRecyclerAdapter<MessageMember, MessageViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MessageMember, MessageViewholder>(options) {
                    /**
                     * @param holder
                     * @param position
                     * @param model    the model object containing the data that should be used to populate the view.
                     */
                    @Override
                    protected void onBindViewHolder(@NonNull MessageViewholder holder, int position, @NonNull MessageMember model) {
                        holder.setMessage(getApplication(), model.getMessage(), model.getTime(), model.getDate()
                                , model.getType(), model.getSenderuid(), model.getReceiveruid(), model.getSendername(), model.getAudio(), model.getImage(), model.getVideo(), model.getGifUrl());

                        String audio = getItem(position).getAudio();
                        Long delete = getItem(position).getDelete();
                        String type = getItem(position).getType();
                        String imageuri = getItem(position).getImage();
                        String date = getItem(position).getDate();
                        String time = getItem(position).getTime();
                        String sendername = getItem(position).getSendername();
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

                        holder.senderTv.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                createMessageDialog(getRef(position).getKey(), delete, type, imageuri, date, time, sendername, audio);
                                String rowid = getRef(position).getKey();
                                Log.d("Row ID _______----: ", rowid);
                                return false;
                            }
                        });

                        holder.iv_sender.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                createMessageDialog(getRef(position).getKey(), delete, type, imageuri, date, time, sendername, audio);
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

    private void createMessageDialog(String rowid, Long delete, String type, String imageuri, String date, String time, String sendername, String audio) {
        final Dialog dialog = new Dialog(MessageActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.message_option);

        TextView unSend = dialog.findViewById(R.id.unsend_id);
        TextView details = dialog.findViewById(R.id.details_id);
        TextView downloads = dialog.findViewById(R.id.download_id);
        TextView Edit = dialog.findViewById(R.id.option2_id);
        TextView dateMo = dialog.findViewById(R.id.date_mo);
        TextView timeMo = dialog.findViewById(R.id.time_mo);


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
                deleteMessage(rowid);
                Log.d("DELETE MESAGE: ", "47555");
                dialog.dismiss();
            }
        });

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle edit message functionality here
                // For example, you can show an EditText in a dialog for editing the message
                showEditDialog(rowid);
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
                    DownloadManager manager = (DownloadManager) MessageActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                    Toast.makeText(MessageActivity.this, "Downloading", Toast.LENGTH_SHORT).show();
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
                    DownloadManager manager = (DownloadManager) MessageActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                    Toast.makeText(MessageActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();
                    StorageReference reference1 = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                    reference1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MessageActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
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

    private void deleteMessage(String messageId) {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("Message").child(sender_uid).child(receiver_id).child(messageId);
        Log.d("Message: ", messageId);
        rootref1.child(messageId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("DeleteTextMessage", "Text message deleted successfully");
                        Toast.makeText(MessageActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MessageActivity.this, "Not Deleted", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Method to show a dialog for editing the message
    private void showEditDialog(String messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
        builder.setTitle("Edit Message");

        // Set up the input
        final EditText input = new EditText(MessageActivity.this);
        builder.setView(input);

        // Set up buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newMessage = input.getText().toString();
                messageMember.setMessage(newMessage);

                if (!newMessage.isEmpty()) {
                    editMessage(messageId, newMessage);
                    Log.d("Mesage Hello: ", newMessage);
                } else {
                    Toast.makeText(MessageActivity.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
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

    private void editMessage(String messageId, String newMessage) {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(messageId);
        HashMap<String, String> newMesages = new HashMap<>();
//        newMesages.put("Message",messageMember);
        rootref1.child(messageId).setValue(messageMember)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MessageActivity.this, "Message Updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MessageActivity.this, "Message not Updated", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MessageActivity.this, "Cannot send empty Message", Toast.LENGTH_SHORT).show();
        } else {
            long deletetime = System.currentTimeMillis();
            messageMember.setDate(savedate);
            messageMember.setTime(savetime);
            messageMember.setMessage(message);
            messageMember.setReceiveruid(receiver_id);
            messageMember.setSenderuid(sender_uid);
            messageMember.setType("t");
            messageMember.setDelete(deletetime);

            String id = rootref1.push().getKey();
            rootref1.child(id).setValue(messageMember);

            String id1 = rootRef2.push().getKey();
            rootRef2.child(id1).setValue(messageMember);

            messageEt.setText("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Handle image selection
            Uri imageUri = data.getData();
            String imageUrl = imageUri.toString();
            Intent intent = new Intent(MessageActivity.this, SentImage.class);
            intent.putExtra("u", imageUrl);
            intent.putExtra("n", receiver_name);
            intent.putExtra("ruid", receiver_id);
            intent.putExtra("suid", sender_uid);
            startActivity(intent);
        }else if (requestCode == PICK_VIDEO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Handle video selection
            Uri videoUri = data.getData();
            String videoUrl = videoUri.toString();
            Intent intent = new Intent(MessageActivity.this, SentVideo.class);
            intent.putExtra("v", videoUrl);
            intent.putExtra("n", receiver_name);
            intent.putExtra("ruid", receiver_id);
            intent.putExtra("suid", sender_uid);
            startActivity(intent);
        }  if (requestCode == PICK_GIF && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedFileUri = data.getData();
            String selectedFileUrl = selectedFileUri.toString();

            // Determine the type of file selected
            String fileType = getContentResolver().getType(selectedFileUri);
            Log.d("onActivityResult: ", fileType);

            if ("image/gif".equals(fileType)) {
                // Handle GIF selection
                Intent gifIntent = new Intent(MessageActivity.this, SentGif.class);
                gifIntent.putExtra("g", selectedFileUrl);
                gifIntent.putExtra("n", receiver_name);
                gifIntent.putExtra("ruid", receiver_id);
                gifIntent.putExtra("suid", sender_uid);
                startActivity(gifIntent);
            } else {
                Toast.makeText(this, "Please select a GIF file", Toast.LENGTH_SHORT).show();
            }
        } else {
            // No file selected
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        typeref.child(receiver_id).child(sender_uid).removeValue();
    }

    private String getFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file1 = new File(musicDirectory, fileName);

        return file1.getPath();

    }
}