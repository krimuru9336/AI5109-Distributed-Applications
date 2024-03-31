package com.example.chatapplication;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.BROADCAST_DOWNLOAD_EVENT;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.DELAY_ONE_SEC;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.DOWNLOAD_DATA;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EMPTY;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_ATTACH_DATA;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_ATTACH_DURATION;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_ATTACH_FILE;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_ATTACH_NAME;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_ATTACH_PATH;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_ATTACH_SIZE;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_ATTACH_TYPE;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_DATETIME;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_ID;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_IMGPATH;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_MESSAGE;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_RECEIVER;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_SEEN;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_SENDER;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_TYPE;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_TYPING;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_TYPINGWITH;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_TYPING_DELAY;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_USER_ID;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXT_MP3;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXT_VCF;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.FALSE;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.FCM_URL;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.ONE;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.PERMISSION_VIDEO;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.REF_CHATS;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.REF_CHAT_ATTACHMENT;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.REF_CHAT_PHOTO_UPLOAD;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.REF_OTHERS;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.REF_TOKENS;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.REF_USERS;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.REQUEST_CODE_CONTACT;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.REQUEST_CODE_PLAY_SERVICES;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.SLASH;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.STATUS_ONLINE;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.TRUE;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.TWO;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.TYPE_CONTACT;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.TYPE_IMAGE;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.TYPE_TEXT;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.VIBRATE_HUNDRED;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.ZERO;
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
import android.util.Log;
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
import com.bytesbee.firebase.chat.activities.adapters.MessageAdapters;
import com.bytesbee.firebase.chat.activities.async.BaseTask;
import com.bytesbee.firebase.chat.activities.async.TaskRunner;
import com.bytesbee.firebase.chat.activities.fcm.APIService;
import com.bytesbee.firebase.chat.activities.fcm.RetroClient;
import com.bytesbee.firebase.chat.activities.fcmmodels.Data;
import com.bytesbee.firebase.chat.activities.fcmmodels.MyResponse;
import com.bytesbee.firebase.chat.activities.fcmmodels.Sender;
import com.bytesbee.firebase.chat.activities.fcmmodels.Token;
import com.bytesbee.firebase.chat.activities.managers.DownloadUtil;
import com.bytesbee.firebase.chat.activities.managers.FirebaseUploader;
import com.bytesbee.firebase.chat.activities.managers.SessionManager;
import com.bytesbee.firebase.chat.activities.managers.Utils;
import com.bytesbee.firebase.chat.activities.models.Attachment;
import com.bytesbee.firebase.chat.activities.models.AttachmentTypes;
import com.bytesbee.firebase.chat.activities.models.Chat;
import com.bytesbee.firebase.chat.activities.models.DownloadFileEvent;
import com.bytesbee.firebase.chat.activities.models.LocationAddress;
import com.bytesbee.firebase.chat.activities.models.Others;
import com.bytesbee.firebase.chat.activities.models.User;
import com.bytesbee.firebase.chat.activities.views.SingleClickListener;
import com.bytesbee.firebase.chat.activities.views.files.FileUtils;
import com.bytesbee.firebase.chat.activities.views.files.MediaFile;
import com.bytesbee.firebase.chat.activities.views.files.PickerManager;
import com.bytesbee.firebase.chat.activities.views.files.PickerManagerCallbacks;
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
        findViewById(R.id.btnAttachmentGallery).setOnClickListener(this);
        initListener();
        pickerManager = new PickerManager(this, this, this);
    }

  

    private void showEditTextLayout() {
        if (isStart) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                imgAttachmentEmoji.setVisibility(View.VISIBLE);
                newMessage.setVisibility(View.VISIBLE);
                imgAddAttachment.setVisibility(View.VISIBLE);
                imgCamera.setVisibility(View.VISIBLE);
            }, 10);
        }
        isStart = false;
    }

    private void hideEditTextLayout() {
        isStart = true;
        imgAttachmentEmoji.setVisibility(View.GONE);
        newMessage.setVisibility(View.INVISIBLE);
        imgAddAttachment.setVisibility(View.GONE);
        imgCamera.setVisibility(View.GONE);
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
        if (id == R.id.imgAttachmentEmoji) {
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
        } else if (id == R.id.btnAttachmentGallery) {
            hideAttachmentView();
            openImage();
        }else if (id == R.id.attachmentBGView) {
            hideAttachmentView();
        }
    }


    private void openImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intentLauncher.launch(intent);
    }


    private void openVideoPicker() {

        //Testing
        Log.e("MethodCalled", "Your method has been called!"); 


        if (permissionsAvailable(permissionsStorage)) {
            Log.e("MethodCalled_inside_if", "Your method has been called!"); 

            Intent target = FileUtils.getVideoIntent();
            Intent intent = Intent.createChooser(target, getString(R.string.choose_file));
            try {
                pickerLauncher.launch(intent);
            } catch (Exception ignored) {
            }
        } else {
            ActivityCompat.requestPermissions(this, permissionsStorage, PERMISSION_VIDEO);
        }
    }



 
   
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case PERMISSION_VIDEO:
                if (permissionsAvailable(permissions))
                    openVideoPicker();
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

                uploadImage();

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

    final ActivityResultLauncher<Intent> placeLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
                final Intent data = result.getData();
                assert data != null;
                final Place place = PingPlacePicker.getPlace(data);
                assert place != null;
                final String name = Utils.isEmpty(place.getName()) ? EMPTY : place.getName();
                final LocationAddress locationAddress = new LocationAddress(name, place.getAddress(), Objects.requireNonNull(place.getLatLng()).latitude, Objects.requireNonNull(place.getLatLng()).longitude);
                Attachment attachment = new Attachment();
                attachment.setData(new Gson().toJson(locationAddress));
                attachment.setFileName(name);
                attachment.setName(name);
                sendMessage(AttachmentTypes.getTypeName(AttachmentTypes.LOCATION), place.getAddress(), attachment);
            } catch (Exception e) {
                Utils.getErrors(e);
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
        hashMap.put(EXTRA_TYPE, TYPE_TEXT);//This is for older version users(Default TEXT, all other set as IMAGE)

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
        } catch (Exception ignored) {
        }
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
   

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(mActivity);
        pd.setMessage(getString(R.string.msg_image_upload));
        pd.show();
        imageUri = Uri.parse("file:///data/user/0/com.bytesbee.firebase.chat.activities/cache/cropped871955575902982255.jpg");

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + Utils.getExtension(mActivity, imageUri));
            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }

                        return fileReference.getDownloadUrl();
                    })
                    .addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                        if (task.isSuccessful()) {
                            final Uri downloadUri = task.getResult();
                            final String mUrl = downloadUri.toString();
                            sendMessage(TYPE_IMAGE, mUrl, null);
                        } else {
                            screens.showToast(R.string.msgFailedToUpload);
                        }
                        pd.dismiss();
                    }).addOnFailureListener(e -> {
                        Utils.getErrors(e);
                        screens.showToast(e.getMessage());
                        pd.dismiss();
                    });
        } else {
            screens.showToast(R.string.msgNoImageSelected);
        }
    }

   
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                imageUri = result.getUri();
                Log.e("imageUri", imageUri+"" );
                if (uploadTask != null && uploadTask.isInProgress()) {
                    screens.showToast(R.string.msgUploadInProgress);
                } else {
                    uploadImage();
                }
            }
         }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CONTACT:
                    try {
                        assert data != null;
                        List<ContactResult> results = MultiContactPicker.obtainResult(data);
                        getSendVCard(results);
                    } catch (Exception e) {
                        Utils.getErrors(e);
                    }
                    break;

                case REQUEST_CODE_PLAY_SERVICES:
               
            }
        }
    }

    private void hideAttachmentView() {
        if (mainAttachmentLayout.getVisibility() == View.VISIBLE) {
            mainAttachmentLayout.setVisibility(View.GONE);
            attachmentBGView.setVisibility(View.GONE);
            imgAddAttachment.animate().setDuration(400).rotationBy(-45).start();
        }
    }

    private void showAttachmentView() {
        mainAttachmentLayout.setVisibility(View.VISIBLE);
        attachmentBGView.setVisibility(View.VISIBLE);
        imgAddAttachment.animate().setDuration(400).rotationBy(45).start();
        emojiIcon.dismiss();
    }




    private final ArrayList<Integer> positionList = new ArrayList<>();

    private final BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null)
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    if (positionList.size() > ZERO && messageAdapters != null) {
                        for (int pos : positionList) {
                            if (pos != -1) {

                                messageAdapters.notifyItemChanged(pos);
                            }
                        }
                    }
                    positionList.clear();
                }
        }
    };



    @Override
    public void PickerManagerOnStartListener() {
        final Handler mPickHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // This is where you do your work in the UI thread. Your worker tells you in the message what to do.
                if (progressBar.isShowing()) {
                    progressBar.cancel();
                }
                final AlertDialog.Builder mPro = new AlertDialog.Builder(new ContextThemeWrapper(mActivity, R.style.myDialog));
                @SuppressLint("InflateParams") final View mPView = LayoutInflater.from(mActivity).inflate(R.layout.dailog_layout, null);
                percentText = mPView.findViewById(R.id.percentText);

                percentText.setOnClickListener(new SingleClickListener() {
                    @Override
                    public void onClickView(View view) {
                        pickerManager.cancelTask();
                        if (mdialog != null && mdialog.isShowing()) {
                            mdialog.cancel();
                        }
                    }
                });

                mProgressBar = mPView.findViewById(R.id.mProgressBar);
                mProgressBar.setMax(100);
                mPro.setView(mPView);
                mdialog = mPro.create();
                mdialog.show();
            }
        };
        mPickHandler.sendEmptyMessage(ZERO);
    }

    @Override
    public void PickerManagerOnProgressUpdate(int progress) {
        try {
            Handler mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    final String progressPlusPercent = progress + "%";
                    percentText.setText(progressPlusPercent);
                    mProgressBar.setProgress(progress);
                }
            };
            mHandler.sendEmptyMessage(ZERO);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

  
    @Override
    public void PickerManagerOnCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String reason) {
        if (mdialog != null && mdialog.isShowing()) {
            mdialog.cancel();
        }
        Utils.sout("Picker Path :: " + new File(path).exists() + " >> " + path + " :drive: " + wasDriveFile + " :<Success>: " + wasSuccessful);

        int fileType = 0;
        try {
            fileType = Objects.requireNonNull(MediaFile.getFileType(path)).fileType;
        } catch (Exception e) {
          
        }

        if (wasSuccessful) {
          
            final int file_size = Integer.parseInt(String.valueOf(new File(path).length() / 1024));

            if (MediaFile.isAudioFileType(fileType)) {
                if (file_size > Utils.getAudioSizeLimit()) {
                    screens.showToast(String.format(getString(R.string.msgFileTooBig), Utils.MAX_SIZE_AUDIO));
                } else {
                    myFileUploadTask(path, AttachmentTypes.AUDIO, null);
                }
            } else if (MediaFile.isVideoFileType(fileType)) {
                if (file_size > Utils.getVideoSizeLimit()) {
                    screens.showToast(String.format(getString(R.string.msgFileTooBig), Utils.MAX_SIZE_VIDEO));
                } else {
                    uploadThumbnail(Uri.parse(path).getPath());
                }
            } else {
                if (file_size > Utils.getDocumentSizeLimit()) {
                    screens.showToast(String.format(getString(R.string.msgFileTooBig), Utils.MAX_SIZE_DOCUMENT));
                } else {
                    myFileUploadTask(path, AttachmentTypes.DOCUMENT, null);
                }
            }

        } else {
            screens.showToast(R.string.msgChooseFileFromOtherLocation);
        }
    }


    @Override
    public void onBackPressed() {
        try {
            pickerManager.deleteTemporaryFile(this);
        } catch (Exception ignored) {
        }
        if (mainAttachmentLayout.getVisibility() == View.VISIBLE) {
            hideAttachmentView();
        } else {
            finish();
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            seenReferenceSender.removeEventListener(seenListenerSender);
            stopTyping();
        } catch (Exception ignored) {
        }
        try {
            if (!isChangingConfigurations()) {
                pickerManager.deleteTemporaryFile(this);
            }
        } catch (Exception ignored) {
        }
    }
}