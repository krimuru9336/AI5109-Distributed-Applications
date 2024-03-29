package com.example.chitchat;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResultLauncher;
import android.app.Activity;
import android.net.Uri;
import androidx.annotation.NonNull;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.MenuItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


public class ChatActivity extends AppCompatActivity implements DataChangedListener{
    private EditText messageEditText;
    private WebSocketHandler webSocketHandler;
    private ChatAdapter chatAdapter;
    private String userDest = "";
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.messageEditText = findViewById(R.id.messageEditText);
        TextView partnerNameView = findViewById(R.id.chatPartnerNameView);
        Intent intent = getIntent();
        if(intent != null){
            this.userDest = intent.getStringExtra("USERDEST");
            setTitle(this.userDest);
            partnerNameView.setText(this.userDest);
        }
        this.webSocketHandler = WebSocketHandler.getInstance(getApplicationContext());
        MessageListener ml = MessageListener.getInstance();
        this.chatAdapter = ml.createChatAdapter(this.userDest,this,this);
        this.recyclerView = findViewById(R.id.messageRecyclerView);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(this.chatAdapter);
        registerForContextMenu(this.recyclerView);
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v->finish());
    }
    public void sendMessage(View view){
        String msgContent = this.messageEditText.getText().toString();
        if(!msgContent.isEmpty()){
            Message msg = new Message(msgContent,userDest,false);
            long timestamp = msg.getTimestamp().getTime();
            UUID msgID = msg.getID();
            webSocketHandler.sendMessage(this.userDest,msgContent,timestamp,msgID);
            this.chatAdapter.addMessage(msg);
            this.messageEditText.setText("");
        }
    }
    private final ActivityResultLauncher<Intent> registerMediaResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result->{
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Uri uri = result.getData().getData();
            if (uri != null) {
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sendMedia(uri);
            }
        }
    });
    public void selectMedia(View view){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] types =  {"image/*","video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,types);
        registerMediaResult.launch(intent);
    }
    public void sendMedia(Uri uri){
        Message msg = new Message("media",userDest,false);
        long timestamp = msg.getTimestamp().getTime();
        UUID msgID = msg.getID();

        String type = getContentResolver().getType(uri);
        msg.setContent(type+"_"+msgID);

        byte[] fileContent = readFileContent(uri);
        String base64data = Base64.encodeToString(fileContent, Base64.DEFAULT);
        msg.setBase64data(base64data);
        msg.setType(type);

        webSocketHandler.sendMedia(this.userDest,msg.getContent(),timestamp,msgID,base64data,type);
        this.chatAdapter.addMessage(msg);
        this.messageEditText.setText("");
    }

    private byte[] readFileContent(Uri uri) {
        try {
            InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void editMsg(Message msg, String newContent) {
        chatAdapter.editMsg(msg, newContent,System.currentTimeMillis());
        webSocketHandler.editMsg(userDest, msg.getID(), newContent, msg.getChangedTimestamp().getTime());
    }
    public void deleteMsgForMe(Message msg) {
        chatAdapter.deleteMsg(msg,System.currentTimeMillis());
    }
    public void deleteMsgForAll(Message msg) {
        chatAdapter.deleteMsg(msg,System.currentTimeMillis());
        webSocketHandler.deleteMsg(userDest, msg.getID(),msg.getChangedTimestamp().getTime());
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            if (chatAdapter != null) {
                Message selectedMessage = chatAdapter.getItem(chatAdapter.getPos());
                if (item.getItemId() == R.id.context_edit) {
                    MessageContext.showInputField(
                            this,
                            selectedMessage,
                            getString(R.string.edit_text),
                            getString(R.string.edit_input_text),
                            (userInput) -> editMsg(selectedMessage, userInput)
                    );
                    return true;
                } else if (item.getItemId() == R.id.context_delete_for_me) {
                    MessageContext.showContextMenu(
                            this,
                            getString(R.string.delete_for_me),
                            getString(R.string.delete_for_me_desc),
                            (dialog, which) -> deleteMsgForMe(selectedMessage)
                    );
                    return true;
                } else if (item.getItemId() == R.id.context_delete_for_all) {
                    MessageContext.showContextMenu(
                            this,
                            getString(R.string.delete_for_all),
                            getString(R.string.delete_for_all_desc),
                            (dialog, which) -> deleteMsgForAll(selectedMessage)
                    );
                    return true;
                }else {
                    return super.onContextItemSelected(item);
                }
            } else {
                return false;
            }
        } catch (NullPointerException npe) {
            return false;
        }
    }
    @Override
    public void onDataChanged() {
        recyclerView.smoothScrollToPosition(this.chatAdapter.getItemCount() - 1);
    }
}
