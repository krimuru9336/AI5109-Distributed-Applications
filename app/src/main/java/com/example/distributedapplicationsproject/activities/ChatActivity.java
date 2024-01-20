package com.example.distributedapplicationsproject.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.distributedapplicationsproject.R;
import com.example.distributedapplicationsproject.android.MessageRecyclerAdapter;
import com.example.distributedapplicationsproject.android.dialogs.CustomProgressDialog;
import com.example.distributedapplicationsproject.android.listeners.MessageEventListener;
import com.example.distributedapplicationsproject.db.MediaCachingService;
import com.example.distributedapplicationsproject.firebase.DatabaseService;
import com.example.distributedapplicationsproject.firebase.StorageService;
import com.example.distributedapplicationsproject.models.Message;
import com.example.distributedapplicationsproject.models.chat.ChatInfo;
import com.example.distributedapplicationsproject.utils.DataShare;
import com.example.distributedapplicationsproject.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    NestedScrollView nestedScrollView;
    ImageButton btnBack;
    TextView textTitle;
    RecyclerView recyclerViewContent;
    TextInputLayout textInputLayoutMessage;
    TextInputEditText textInputEditText;
    ProgressBar progressBarMessagesLoaded;
    ImageButton btnAttachFile;
    ImageButton btnAttachFilePreview;
    ImageButton btnSend;

    DatabaseService databaseService = DatabaseService.getInstance();
    StorageService storageService = StorageService.getInstance();
    MessageRecyclerAdapter messageRecyclerAdapter;
    ChatInfo chatInfo;

    List<Message> messageList;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    Uri selectedMediaUri;

    MessageEventListener messageEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // hacky init for sqlite
        DataShare.getInstance().setMediaCachingService(new MediaCachingService(this));

        // ui
        nestedScrollView = findViewById(R.id.nested_scroll_view);
        btnBack = findViewById(R.id.btn_back);
        textTitle = findViewById(R.id.text_title);
        recyclerViewContent = findViewById(R.id.recycler_view_content);
        progressBarMessagesLoaded = findViewById(R.id.progress_bar_messages_loaded);
        textInputLayoutMessage = findViewById(R.id.text_input_layout_message);
        textInputEditText = findViewById(R.id.text_input_edit_text);
        btnAttachFile = findViewById(R.id.btn_attach_file);
        btnAttachFilePreview = findViewById(R.id.btn_attach_file_preview);
        btnSend = findViewById(R.id.btn_send);

        // other
        chatInfo = Utils.getChatInfoFromIntent(getIntent());
        DataShare.getInstance().setCurrentChatInfo(chatInfo);

        textTitle.setText(chatInfo.title);


        // events/ callbacks
        // Registers a photo picker activity launcher in single-select mode.
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                selectedMediaUri = uri;
                btnAttachFile.setVisibility(View.GONE);
                btnAttachFilePreview.setVisibility(View.VISIBLE);
            }
        });

        // remove listener and go back
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                databaseService.removeMessageEventListener(chatInfo, messageEventListener);
                finish();
            }
        });

        setupMessageRecycler();
    }

    private void setupMessageRecycler() {
        messageList = new ArrayList<>();

        messageRecyclerAdapter = new MessageRecyclerAdapter(messageList, progressBarMessagesLoaded, nestedScrollView, getSupportFragmentManager());
        recyclerViewContent.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewContent.setAdapter(messageRecyclerAdapter);

        messageEventListener = new MessageEventListener(messageList, messageRecyclerAdapter);
        databaseService.addMessageEventListener(chatInfo, messageEventListener);
    }

    public void onBackButtonClick(View view) {
        this.getOnBackPressedDispatcher().onBackPressed();
    }

    public void onSendButtonClick(View view) {
        sendMessage();
    }

    private void sendMessage() {
        if (selectedMediaUri != null) {
            CustomProgressDialog dialog = new CustomProgressDialog(findViewById(android.R.id.content), getString(R.string.dialog_uploading));
            dialog.show(getSupportFragmentManager(), "DIALOG_UPLOADING");
            storageService.uploadMedia(chatInfo, getContentResolver(), selectedMediaUri, new StorageService.OnMediaUploadListener() {
                @Override
                public void onMediaUploaded() {
                    dialog.dismiss();
                }

                @Override
                public void onMediaFailed(Exception e) {
                    // Empty
                }

                @Override
                public void onMediaProgression(int progression) {
                    dialog.setProgress(progression);
                }

                @Override
                public void onMediaDownloadUrl(Uri mediaDownloadUri) {
                    Message msg = new Message();
                    int index = messageList.isEmpty() ? 0 : messageList.size();

                    msg.setIndex(index);
                    msg.setMessage(textInputLayoutMessage.getEditText().getText().toString());
                    msg.setMediaUrl(mediaDownloadUri.toString());
                    msg.setSenderId(DataShare.getInstance().getCurrentUser().getId());
                    msg.setLastEdited(Utils.generateCreatedAt());

                    textInputLayoutMessage.getEditText().setText("");
                    selectedMediaUri = null;

                    btnAttachFile.setVisibility(View.VISIBLE);
                    btnAttachFilePreview.setVisibility(View.GONE);

                    databaseService.sendMessage(chatInfo, msg, index);
                }
            });
            return;
        }
        Message msg = new Message();
        if (textInputLayoutMessage.getEditText() == null || textInputLayoutMessage.getEditText().getText().toString().trim().isEmpty()) {
            return;
        }

        int index = messageList.isEmpty() ? 0 : messageList.size();

        msg.setIndex(index);
        msg.setMessage(textInputLayoutMessage.getEditText().getText().toString());
        msg.setSenderId(DataShare.getInstance().getCurrentUser().getId());
        msg.setLastEdited(Utils.generateCreatedAt());

        textInputLayoutMessage.getEditText().setText("");
        selectedMediaUri = null;

        databaseService.sendMessage(chatInfo, msg, index);
    }

    public void onAttachFileButtonClick(View view) {
        //Launch the photo picker and let the user choose images and videos.
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());

    }

    public void onAttachFilePreviewButtonClick(View view) {
        selectedMediaUri = null;
        btnAttachFile.setVisibility(View.VISIBLE);
        btnAttachFilePreview.setVisibility(View.GONE);
    }
}
