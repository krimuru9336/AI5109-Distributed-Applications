package com.bytesbee.firebase.chat.activities;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.devlomi.record_view.RecordView;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.rtchagas.pingplacepicker.PingPlacePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends BaseActivity implements View.OnClickListener, PickerManagerCallbacks {

    private CircleImageView mImageView;
    private TextView mTxtUsername, txtTyping;
    private LinearLayoutManager layoutManager;
    private RecyclerView mRecyclerView;
    private String currentId, userId, userName = "Sender";
    private String strSender, strReceiver;
    private Toolbar mToolbar;
    private ArrayList<Chat> chats;
    private MessageAdapters messageAdapters;

    private ValueEventListener seenListenerSender;
    private Query seenReferenceSender;

    private APIService apiService;

    boolean notify = false;

    private String onlineStatus, strUsername, strCurrentImage;
    private Uri imageUri = null;
    private StorageTask uploadTask;
    private FirebaseStorage storage;
    private StorageReference storageReference, storageAttachment;
    private LinearLayout btnGoToBottom;
    private EmojiPopup emojiIcon;
    private CardView mainAttachmentLayout;
    private View attachmentBGView;
    private EmojiEditText newMessage;
    private ImageView imgAddAttachment, imgAttachmentEmoji, imgCamera;
    private RelativeLayout rootView;

    private PickerManager pickerManager;

    private RelativeLayout rlChatView;

    private String vCardData, displayName, phoneNumber;
    private File fileUri = null;
    private Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mActivity = this;

        apiService = RetroClient.getClient(FCM_URL).create(APIService.class);

        initUI();

        txtTyping.setText(EMPTY);

        try {
            setSupportActionBar(mToolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(EMPTY);
        } catch (Exception ignored) {
        }
        mToolbar.setNavigationOnClickListener(new SingleClickListener() {
            @Override
            public void onClickView(View v) {
                onBackPressed();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        currentId = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(currentId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    strUsername = user.getUsername();
                    strCurrentImage = user.getImageURL();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Intent intent = getIntent();
        userId = intent.getStringExtra(EXTRA_USER_ID);

        strSender = currentId + SLASH + userId;
        strReceiver = userId + SLASH + currentId;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference(REF_CHAT_PHOTO_UPLOAD + SLASH + strSender);
        storageAttachment = storage.getReference(REF_CHAT_ATTACHMENT + SLASH + strSender);

        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        btnGoToBottom.setVisibility(View.GONE);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                try {
                    if (firstVisible == -1)
                        firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition();
                    else
                        firstVisible = messageAdapters.getItemCount() >= TWO ? messageAdapters.getItemCount() - TWO : ZERO;
                } catch (Exception e) {
                    firstVisible = ZERO;
                }

                if (layoutManager.findLastVisibleItemPosition() < firstVisible) {
                    btnGoToBottom.setVisibility(View.VISIBLE);
                } else {
                    btnGoToBottom.setVisibility(View.GONE);
                }
            }
        });

        btnGoToBottom.setOnClickListener(new SingleClickListener() {
            @Override
            public void onClickView(View v) {
                try {
                    if (firstVisible != -1) {
                        mRecyclerView.smoothScrollToPosition(messageAdapters.getItemCount() - ONE);
                    }
                    btnGoToBottom.setVisibility(View.GONE);
                } catch (Exception ignored) {

                }
            }
        });

        rlChatView.setVisibility(View.VISIBLE);
        reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    final User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    mTxtUsername.setText(user.getUsername());
                    userName = user.getUsername();
                    onlineStatus = Utils.showOnlineOffline(mActivity, user.getIsOnline());

                    txtTyping.setText(onlineStatus);

                    Utils.setProfileImage(getApplicationContext(), user.getImageURL(), mImageView);

                    readMessages(user.getImageURL());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final LinearLayout viewProfile = findViewById(R.id.viewProfile);
        viewProfile.setOnClickListener(new SingleClickListener() {
            @Override
            public void onClickView(View v) {
                screens.openViewProfileActivity(userId);
            }
        });

        emojiIcon = EmojiPopup.Builder.fromRootView(rootView).setOnEmojiPopupShownListener(() -> {
            hideAttachmentView();
            imgAttachmentEmoji.setImageResource(R.drawable.ic_keyboard_24dp);
        }).setOnEmojiPopupDismissListener(() -> imgAttachmentEmoji.setImageResource(R.drawable.ic_insert_emoticon_gray)).setKeyboardAnimationStyle(R.style.emoji_fade_animation_style).build(newMessage);

        newMessage.setOnTouchListener((v, event) -> {
            hideAttachmentView();
            return false;
        });
        Utils.uploadTypingStatus();
        typingListening();
        readTyping();
        seenMessage();

        final Handler handler = new Handler(Looper.getMainLooper());
          handler.postDelayed(this::permissionRecording, 800);
    }

    private void initUI() {
        mImageView = findViewById(R.id.imageView);
        txtTyping = findViewById(R.id.txtTyping);
        mTxtUsername = findViewById(R.id.txtUsername);
        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.recyclerView);

        rootView = findViewById(R.id.rootView);
        rlChatView = findViewById(R.id.rlChatView);
        btnGoToBottom = findViewById(R.id.btnBottom);
        newMessage = findViewById(R.id.newMessage);
        imgAddAttachment = findViewById(R.id.imgAddAttachment);
        imgCamera = findViewById(R.id.imgCamera);
        mainAttachmentLayout = findViewById(R.id.mainAttachmentLayout);
        mainAttachmentLayout.setVisibility(View.GONE);
        attachmentBGView = findViewById(R.id.attachmentBGView);
        attachmentBGView.setVisibility(View.GONE);
        attachmentBGView.setOnClickListener(this);

        imgAttachmentEmoji = findViewById(R.id.imgAttachmentEmoji);

        imgAddAttachment.setOnClickListener(this);
        imgCamera.setOnClickListener(this);
        imgAttachmentEmoji.setOnClickListener(this);
        findViewById(R.id.btnAttachmentVideo).setOnClickListener(this);
        findViewById(R.id.btnAttachmentContact).setOnClickListener(this);
        findViewById(R.id.btnAttachmentGallery).setOnClickListener(this);
        findViewById(R.id.btnAttachmentAudio).setOnClickListener(this);
        findViewById(R.id.btnAttachmentLocation).setOnClickListener(this);
        findViewById(R.id.btnAttachmentDocument).setOnClickListener(this);


        initListener();

        pickerManager = new PickerManager(this, this, this);
    }


    private void clickToSend() {
        if (TextUtils.isEmpty(Objects.requireNonNull(newMessage.getText()).toString().trim())) {
            screens.showToast(R.string.strEmptyMsg);
        } else {
            sendMessage(TYPE_TEXT, Objects.requireNonNull(newMessage.getText()).toString().trim(), null);
        }
        newMessage.setText(EMPTY);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.recordButton) {
            hideAttachmentView();
            clickToSend();
        } else if (id == R.id.imgAttachmentEmoji) {
            emojiIcon.toggle();
        } else if (id == R.id.imgAddAttachment) {
            if (!blockUnblockCheckBeforeSend()) {
                fileUri = null;
                imgUri = null;
                Utils.closeKeyboard(mActivity, view);
                if (mainAttachmentLayout.getVisibility() == View.VISIBLE) {
                    hideAttachmentView();
                } else {
                    showAttachmentView();
                }
            }
        } else if (id == R.id.imgCamera) {
            if (!blockUnblockCheckBeforeSend()) {
                fileUri = null;
                imgUri = null;
                hideAttachmentView();
                openCamera();
            }
        } else if (id == R.id.btnAttachmentGallery) {
            hideAttachmentView();
            openImage();
        } else if (id == R.id.btnAttachmentAudio) {
            hideAttachmentView();
            openAudioPicker();
        } else if (id == R.id.btnAttachmentLocation) {
            hideAttachmentView();
            openPlacePicker();
        } else if (id == R.id.btnAttachmentVideo) {
            hideAttachmentView();
            openVideoPicker();
        } else if (id == R.id.btnAttachmentDocument) {
            hideAttachmentView();
            openDocumentPicker();
        } else if (id == R.id.btnAttachmentContact) {
            hideAttachmentView();
            openContactPicker();
        } else if (id == R.id.attachmentBGView) {
            hideAttachmentView();
        }
    }

   


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CONTACT:
                if (permissionsAvailable(permissions))
                    openContactPicker();
                break;
            case PERMISSION_AUDIO:
                if (permissionsAvailable(permissions))
                    openAudioPicker();
                break;
            case PERMISSION_DOCUMENT:
                if (permissionsAvailable(permissions))
                    openDocumentPicker();
                break;
            case PERMISSION_VIDEO:
                if (permissionsAvailable(permissions))
                    openVideoPicker();
                break;
            case REQUEST_PERMISSION_RECORD:
                if (permissionsAvailable(permissions)) {
                    try {
                        if (messageAdapters != null)
                            messageAdapters.notifyDataSetChanged();
                    } catch (Exception ignored) {

                    }
                }
                break;
        }
    }

    final ActivityResultLauncher<Intent> intentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (fileUri != null) { 
                    imgUri = Uri.fromFile(fileUri);
                } else { 
                    Intent data = result.getData();
                    assert data != null;
                    imgUri = data.getData();
                }

                try {
                    CropImage.activity(imgUri)
                            .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setFixAspectRatio(true)
                            .start(mActivity);
                } catch (Exception e) {
                    Utils.getErrors(e);
                }
            }
        }
    });

    final ActivityResultLauncher<Intent> pickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
               
                final Intent data = result.getData();
                assert data != null;
                final Uri uriData = data.getData();
                Utils.sout("PickerManager uri: " + uriData.toString());
                pickerManager.getPath(uriData, Build.VERSION.SDK_INT); 
            }
        }
    });

    private void sendMessage(String type, String message, Attachment attachment) {
        if (blockUnblockCheckBeforeSend()) {
            return;
        }
        notify = true;
        String defaultMsg;
        final String sender = currentId;
        final String receiver = userId;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(EXTRA_SENDER, sender);
        hashMap.put(EXTRA_RECEIVER, receiver);
        hashMap.put(EXTRA_MESSAGE, message);
        hashMap.put(EXTRA_ATTACH_TYPE, type);

        hashMap.put(EXTRA_TYPE, TYPE_TEXT);

        try {
            if (!type.equalsIgnoreCase(TYPE_TEXT) && !type.equalsIgnoreCase(TYPE_IMAGE)) {
                defaultMsg = Utils.getDefaultMessage();
                hashMap.put(EXTRA_MESSAGE, defaultMsg);
            }
        } catch (Exception ignored) {
        }

        try {
            if (type.equalsIgnoreCase(TYPE_TEXT)) {
                
            } else if (type.equalsIgnoreCase(TYPE_IMAGE)) {
                hashMap.put(EXTRA_TYPE, TYPE_IMAGE);
                hashMap.put(EXTRA_IMGPATH, message);
            } else {
                hashMap.put(EXTRA_ATTACH_PATH, message);
                try {
                    if (attachment != null) {
                        hashMap.put(EXTRA_ATTACH_NAME, attachment.getName());
                        hashMap.put(EXTRA_ATTACH_FILE, attachment.getFileName());
                        hashMap.put(EXTRA_ATTACH_SIZE, attachment.getBytesCount());
                        if (attachment.getData() != null) {
                            hashMap.put(EXTRA_ATTACH_DATA, attachment.getData());
                        }
                        if (attachment.getDuration() != null) {
                            hashMap.put(EXTRA_ATTACH_DURATION, attachment.getDuration());
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }

        hashMap.put(EXTRA_SEEN, FALSE);
        hashMap.put(EXTRA_DATETIME, Utils.getDateTime());

        final String key = Utils.getChatUniqueId();
        reference.child(REF_CHATS).child(strSender).child(key).setValue(hashMap);
        reference.child(REF_CHATS).child(strReceiver).child(key).setValue(hashMap);

        Utils.chatSendSound(getApplicationContext());

        try {
            String msg = message;
            if (!type.equalsIgnoreCase(TYPE_TEXT) && !type.equalsIgnoreCase(TYPE_IMAGE)) {
                try {
                    String firstCapital = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
                    if (attachment != null) {
                        msg = "New " + firstCapital + "(" + attachment.getName() + ")";
                    } else {
                        msg = firstCapital;
                    }
                } catch (Exception e) {
                    msg = message;
                }
            }

            if (notify) {
                sendNotification(receiver, strUsername, msg, type);
            }
            notify = false;
        } catch (Exception ignored) {ßßß
        }
    }

    private void sendNotification(String receiver, final String username, final String message, final String type) {
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference(REF_TOKENS);
        Query query = tokenRef.orderByKey().equalTo(receiver);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Token token = snapshot.getValue(Token.class);

                        final Data data = new Data(currentId, R.drawable.ic_stat_ic_notification, username, message, getString(R.string.strNewMessage), userId, type);

                        assert token != null;
                        final Sender sender = new Sender(data, token.getToken());

                        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(@NotNull Call<MyResponse> call, @NotNull Response<MyResponse> response) {
                                assert response.code() != 200 || response.body() != null;
                            }

                            @Override
                            public void onFailure(@NotNull Call<MyResponse> call, @NotNull Throwable t) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void readMessages(final String imageUrl) {
        chats = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(strReceiver);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            Chat chat = snapshot.getValue(Chat.class);
                            assert chat != null;
                            if (!Utils.isEmpty(chat.getMessage())) {
                                chat.setId(snapshot.getKey());
                                chats.add(chat);
                            }

                        } catch (Exception ignored) {
                        }
                    }
                }
                try {
                    messageAdapters = new MessageAdapters(mActivity, chats, userName, strCurrentImage, imageUrl);
                    mRecyclerView.setAdapter(messageAdapters);
                } catch (Exception e) {
                    Utils.getErrors(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenMessage() {
        seenReferenceSender = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(strSender).orderByChild(EXTRA_SEEN).equalTo(false);
        seenListenerSender = seenReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            Chat chat = snapshot.getValue(Chat.class);
                            assert chat != null;
                            if (!Utils.isEmpty(chat.getMessage())) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put(EXTRA_SEEN, TRUE);
                                snapshot.getRef().updateChildren(hashMap);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    MenuItem itemBlockUnblock;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_groups, menu);
        MenuItem itemViewUser = menu.findItem(R.id.itemGroupInfo);
        itemBlockUnblock = menu.findItem(R.id.itemBlockUnblock);
        MenuItem itemAdd = menu.findItem(R.id.itemAddGroup);
        MenuItem itemEdit = menu.findItem(R.id.itemEditGroup);
        MenuItem itemLeave = menu.findItem(R.id.itemLeaveGroup);
        MenuItem itemDelete = menu.findItem(R.id.itemDeleteGroup);
        itemAdd.setVisible(false);
        itemEdit.setVisible(false);
        itemLeave.setVisible(false);
        itemDelete.setVisible(false);
        itemViewUser.setTitle(R.string.strUserInfo);
        checkUserIsBlock();
        blockedByOpponent();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.itemGroupInfo) {
            screens.openViewProfileActivity(userId);
        } else if (itemId == R.id.itemClearMyChats) {
            Utils.showYesNoDialog(mActivity, R.string.strDelete, R.string.strDeleteOwnChats, this::deleteOwnChats);
        } else if (itemId == R.id.itemBlockUnblock) {
            if (itemBlockUnblock.getTitle().toString().equalsIgnoreCase(getString(R.string.strBlock))) {
                blockUser();
            } else {
                unblockUser();
            }
        }
        return true;
    }



    final Query chatsReceiver = FirebaseDatabase.getInstance().getReference(REF_CHATS).child(strReceiver).orderByChild(EXTRA_SENDER).equalTo(currentId);
        chatsReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chat chat = snapshot.getValue(Chat.class);
                            assert chat != null;
                            if (!Utils.isEmpty(chat.getAttachmentType())) {
                                Utils.deleteUploadedFilesFromCloud(storage, chat);
                            }
                            snapshot.getRef().removeValue();
                        }
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readTyping() {
        reference = FirebaseDatabase.getInstance().getReference(REF_OTHERS).child(currentId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChildren()) {
                        Others user = dataSnapshot.getValue(Others.class);
                        assert user != null;
                        if (user.isTyping() && user.getTypingwith().equalsIgnoreCase(userId)) {
                            txtTyping.setText(getString(R.string.strTyping));
                        } else {
                            txtTyping.setText(onlineStatus);
                        }
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

 
}