package com.example.myapplication5;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication5.adapter.ChatRecyclerAdapter;
import com.example.myapplication5.adapter.GifOptionsAdapter;
import com.example.myapplication5.adapter.OnGifClickListener;

import com.example.myapplication5.adapter.UserAdapter;
import com.example.myapplication5.utils.AndroidUtil;
import com.example.myapplication5.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//import GiphyService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    HelperClass sender;
    ChatroomModel chatroomModel;

    ChatRecyclerAdapter chat_adapter;
    List<ChatMessageModel> chatMessageList;
    boolean isAdapterSet = false; // Flag to check if the adapter has been set
    String chatroomId;

    ImageButton backButton;
    TextView sender_username;
    RecyclerView recyclerView;

    ImageButton attachment;
    EditText messageInput;

    //groups
    TextView groupNameTextView;
    boolean isGroupChat;
    String groupId;
//    GroupChatroomModel group;


    ImageButton sendMessageButton; //send message button


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("entered chat");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sender_username = findViewById(R.id.sender_username);
        backButton = findViewById(R.id.back_button);
        attachment = findViewById(R.id.attach);  //button for adding attachments
        messageInput = findViewById(R.id.chat_message_input);
        Drawable[] drawables = messageInput.getCompoundDrawables();
        final Drawable gifDrawable = drawables[2];
        sendMessageButton = findViewById(R.id.message_send_btn);
        recyclerView = findViewById(R.id.chat_recycler_view);


        //button actions -1. backbutton
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            // Optional: finish the ChatActivity to remove it from the back stack

        });

        //2-attaching images
//        ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        Intent data = result.getData();
//                        if (data != null) {
//                            Uri selectedImageUri = data.getData();
//                            uploadImageToFirebase(selectedImageUri);
//                        }
//                    }
//                });

//        attachment.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            mGetContent.launch(intent);
//
//        });

// Step 1: Register an ActivityResultLauncher
        ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri selectedMediaUri = data.getData();
                            String mimeType = getContentResolver().getType(selectedMediaUri);
                            if (mimeType != null) {
                                if (mimeType.startsWith("image/")) {
                                    // Handle image
                                    uploadImageToFirebase(selectedMediaUri);
                                } else if (mimeType.startsWith("video/")) {
                                    // Handle video
                                    uploadVideoToFirebase(selectedMediaUri);
                                }
                            }
                        }
                    }
                }
        );


        attachment.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setType("image/* video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            mGetContent.launch(intent);
        });



//3. gif button
        messageInput.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2; // Index of compound drawable end
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (messageInput.getRight() - messageInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Click event occurred on the GIF button (compound drawable end)
                        showGifSelectionDialog();
                        return true; // Consume the event
                    }
                }
                return false; // Continue with other touch events
            }
        });


//group new code
        isGroupChat = getIntent().getBooleanExtra("isGroupChat", false);
        if (isGroupChat) {
            // Adjustments for group chat
            System.out.println("Group chat flag set");

            Intent intent = getIntent();
            String groupId = intent.getStringExtra("id");
            String groupName = intent.getStringExtra("groupName");


            // Retrieve the members map
            ArrayList<HashMap<String, String>> serializableMembers = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("members");
            List<Map<String, String>> members = new ArrayList<>();
            for (HashMap<String, String> map : serializableMembers) {
                members.add(map);
            }

// Retrieve the memberIDs
            ArrayList<String> memberIDsList = intent.getStringArrayListExtra("memberIDs");
            List<String> memberIDs = new ArrayList<>();
            if (memberIDsList != null) {
                memberIDs.addAll(memberIDsList);
            }

            System.out.println(members+"\t"+memberIDs);



            sender_username.setText(groupName);  //setting group name
            //set members names also
            chatroomId = groupId;


            System.out.println("group name , id  \t" + groupName + "\t" + groupId +"\n");

        } else {


            //db steps for one-one chat
            //returns a helper class with userid, username data from intent
            sender = AndroidUtil.getUserModelFromIntent(getIntent());   // other user
            System.out.println("one-one chat line 1:  " + sender.getUsername() + sender.getUserId());

            System.out.println("one-one chat line 2");

            //creating chatroom id
            chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), sender.getUserId());

            System.out.println("one-one chat line 3: chatroomId\t" + chatroomId);

            sender_username.setText(sender.getUsername());

            System.out.println("one-one chat line 4 ");


        }

        //group new

//        sender_username.setText(sender.getUsername());
        // Initialize the adapter only once when the ChatActivity is created
        List<ChatMessageModel> chatMessageList = new ArrayList<>();
        chat_adapter = new ChatRecyclerAdapter(chatMessageList, this, chatroomId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chat_adapter);

        System.out.println("both grp n one-one chat line 5 ");


        //4.send button
        sendMessageButton.setOnClickListener((v -> {
            System.out.println("click send message button ");
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty())
                return;
            //sendMessageToUser(message);
            sendMessage(message, null);
        }));

        System.out.println("one-one chat line 6 ");


        if (!isGroupChat) {
            System.out.println("one-one chat line  7 ");
            getOrCreateChatroomModel();
        }


        setupChatRecyclerView();

        System.out.println("one-one chat line  17 ");
    }



    //no change needed for grp
    private void uploadImageToFirebase(Uri selectedImageUri) {
        String fileName = UUID.randomUUID().toString();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + fileName);

        storageReference.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // download URL
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {

                        sendMessageWithImageUrl(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void sendMessageWithImageUrl(String imageUrl) {
        ChatMessageModel message = new ChatMessageModel();
        message.setMessageType(ChatMessageModel.MessageType.IMAGE);
        message.setMediaUrl(imageUrl);
        sendMessage(imageUrl, ChatMessageModel.MessageType.IMAGE);
    }



    // Rename and adjust the existing method to handle both images and videos
    private void uploadMediaToFirebase(Uri selectedMediaUri, String mediaType) {
        String fileName = UUID.randomUUID().toString();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(mediaType + "/" + fileName);

        storageReference.putFile(selectedMediaUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // download URL
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        if ("image".equals(mediaType)) {
                            sendMessageWithImageUrl(uri.toString());
                        } else if ("video".equals(mediaType)) {
                            sendMessageWithVideoUrl(uri.toString());
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(this, "Failed to upload media", Toast.LENGTH_SHORT).show();
                });
    }

    // New method for handling video uploads
    private void uploadVideoToFirebase(Uri selectedVideoUri) {
        uploadMediaToFirebase(selectedVideoUri, "video");
    }


    // New method for sending messages with video URLs
    private void sendMessageWithVideoUrl(String videoUrl) {
        ChatMessageModel message = new ChatMessageModel();
        message.setMessageType(ChatMessageModel.MessageType.VIDEO);
        message.setMediaUrl(videoUrl);
        sendMessage(videoUrl, ChatMessageModel.MessageType.VIDEO);
    }



    //gifs
    private void showGifSelectionDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_select_gif, null);
        builder.setView(dialogView);

        EditText gifNameEditText = dialogView.findViewById(R.id.gif_name_edittext);
        RecyclerView gifOptionsRecyclerView = dialogView.findViewById(R.id.gif_options_recyclerview);

        final AlertDialog dialog = builder.create();
        dialog.show();

        // Initialize the adapter with an empty list
        GifOptionsAdapter gifOptionsAdapter = new GifOptionsAdapter(new ArrayList<>(), new GifOptionsAdapter.OnGifClickListener() {
            @Override
            public void onGifClick(String gifUrl) {

                System.out.println("gif url in chat activity->" + gifUrl);

                sendMessage(gifUrl, ChatMessageModel.MessageType.GIF);
                // Dismiss the dialog
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

            }
        });


        gifOptionsRecyclerView.setAdapter(gifOptionsAdapter);
        gifOptionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Listen for text changes in the EditText to trigger a search
        gifNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Trigger a search when the user types a query
                searchGifs(s.toString(), gifOptionsAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        // Override the "Send" button's OnClickListener after the dialog has been shown
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


    }


    private void searchGifs(String query, GifOptionsAdapter gifOptionsAdapter) {
        GiphyService giphyService = new GiphyService();
        giphyService.searchGifs(query, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    List<String> gifModels = giphyService.parseJsonResponse(response.body().string());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gifOptionsAdapter.updateGifOptions(gifModels);
                        }
                    });
                }
            }
        });
    }


//    void setupChatRecyclerView() {
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference chatsCollection = db.collection("chatrooms").document(chatroomId).collection("chats");
//
//        chatsCollection.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//                    System.out.println("Error getting chats: " + error.getMessage());
//                    return;
//                }
//
//                chat_adapter.clearData();
//
//                for (QueryDocumentSnapshot document : querySnapshot) {
//                    ChatMessageModel chatMessage = document.toObject(ChatMessageModel.class);
//                    chat_adapter.addData(chatMessage);
//                }
//
//                chat_adapter.notifyDataSetChanged();
//            }
//        });
//    }


    void setupChatRecyclerView() {

        System.out.println("one-one chat line  8: inside recycler view ");

        FirebaseFirestore db = FirebaseFirestore.getInstance();



        CollectionReference chatsCollection = isGroupChat ?
                FirebaseFirestore.getInstance().collection("groupChats").document(chatroomId).collection("chats") :
                FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId).collection("chats");

        System.out.println("one-one chat line  8.1: inside recycler view -> \t " + chatroomId);


        System.out.println("one-one chat line  9 ");
        chatsCollection.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {

                    System.out.println("one-one chat line  10 ");
                    System.out.println("Error getting chats: " + error.getMessage());
                    return;
                }

                System.out.println("one-one chat line  11 ");

                chat_adapter.clearData();

                System.out.println("one-one chat line  12 ");

                for (QueryDocumentSnapshot document : querySnapshot) {
                    System.out.println("one-one chat line  13 ");
                    ChatMessageModel chatMessage = document.toObject(ChatMessageModel.class);
                    System.out.println("one-one chat line  14 ");
                    chat_adapter.addData(chatMessage);
                    System.out.println("one-one chat line  15 ");
                }

                chat_adapter.notifyDataSetChanged();
                System.out.println("one-one chat line  16 ");
            }
        });
    }

//String groupChatroomId (String groupId){
//
//
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
////    String groupId = groupId; // The group ID you have
//
//    db.collection("groupchats")
//            .whereEqualTo("id", groupId)
//            .get()
//            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String groupChatRoomId = document.getId(); // This is your group chat room ID
//
//                            // Now you can use this ID to perform operations on the group chat room
//                            // For example, sending a message to the group chat room
////                            sendMessageToGroupChat(groupChatRoomId, message, messageType);
//                        }
//                    }
////                    } else {
////                        Log.d(TAG, "Error getting documents: ", task.getException());
////                    }
//                }
//            });
//
//    return groupId;
//


    //for group
    void sendMessage(String message, ChatMessageModel.MessageType messageType) {

        String messageId = UUID.randomUUID().toString();


//        String room = isGroupChat? FirebaseUtil.groupRoom:FirebaseUtil.oneOneRoom;  //group


        if (!isGroupChat) {
            if (chatroomModel == null) {
                chatroomModel = new ChatroomModel();
                // Set initial values for chatroomModel
            }

            // Update chatroom model with the last message details
            chatroomModel.setLastMessageTimestamp(Timestamp.now());
            chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
            chatroomModel.setLastMessage(message);


            System.out.println("one-one in send message line 1\t");
            FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
        }

        ChatMessageModel chatMessageModel;

        // Set the message type if provided
        if (messageType != null) {

            chatMessageModel = new ChatMessageModel(null, FirebaseUtil.currentUserId(), Timestamp.now(), messageId, messageType);
            chatMessageModel.setMediaUrl(message);  //setting GIF URL


        } else {
            //meaning a text type
            chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now(), messageId, ChatMessageModel.MessageType.TEXT);

        }


        System.out.println("one-one in send message line 2");
        if (!isGroupChat) {
            FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel) //new param
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                messageInput.setText("");
                            }
                        }
                    });


            chat_adapter.notifyDataSetChanged();


    } else

    {
        //group
        CollectionReference messageCollectionRef =
                FirebaseFirestore.getInstance()
                        .collection("groupChats")
                        .document(chatroomId)
                        .collection("chats");

        // Add the message to Firestore
        messageCollectionRef.add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            messageInput.setText("");
                        }
                    }
                });

        // Update the UI
        chat_adapter.notifyDataSetChanged();


    }

}


//only for one-one
    void getOrCreateChatroomModel(){
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel==null){
                    //first time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(),sender.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }



//    void getOrCreateChatroomModel(boolean isGroupChat, String groupId) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference docRef;
//
//        if (isGroupChat) {
//            docRef = db.collection("groupchats").document(groupId);
//        } else {
//            docRef = FirebaseUtil.getChatroomReference(chatroomId);
//        }
//
//        docRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    // Document exists, fetch the chat model
//                    if (isGroupChat) {
//                        chatroomModel = document.toObject(ChatroomModel.class);  //need verification
//                    } else {
//                        // Handle one-on-one chat model fetching
//                        chatroomModel = task.getResult().toObject(ChatroomModel.class);
//                        //creatte and set
//                        if(chatroomModel==null){
//                            //first time chat
//                            chatroomModel = new ChatroomModel(
//                                    chatroomId,
//                                    Arrays.asList(FirebaseUtil.currentUserId(),sender.getUserId()),
//                                    Timestamp.now(),
//                                    ""
//                            );
//                            FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);}
//                    }
//                }
//            }
//        });
//    }






//    void sendMessage(String message, ChatMessageModel.MessageType messageType) {
//        // Generate a unique messageId for the message
//        String messageId = FirebaseUtil.generateMessageId(chatroomId);
//
//
//        // Update chatroom model with the last message details
//        chatroomModel.setLastMessageTimestamp(Timestamp.now());
//        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
//        chatroomModel.setLastMessage(message);
//
//        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
//
////set message type to text for temp
//        ChatMessageModel chatMessageModel;
//
//        // Set the message type if provided
//        if (messageType != null) {
//
//            chatMessageModel = new ChatMessageModel(null, FirebaseUtil.currentUserId(), Timestamp.now(), messageId, messageType);
//            chatMessageModel.setMediaUrl(message);  //setting GIF URL
//
//
//        } else {
//            //meaning a text type
//            chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now(), messageId, ChatMessageModel.MessageType.TEXT);
//
//        }
//
//
//
//        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
//                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                        if (task.isSuccessful()) {
//                            messageInput.setText("");
//                        }
//                    }
//                });
//
//
//        chat_adapter.notifyDataSetChanged();
//
//    }




}

