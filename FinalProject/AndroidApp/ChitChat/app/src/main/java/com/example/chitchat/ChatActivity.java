package com.example.chitchat;

import androidx.annotation.NonNull;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.MenuItem;
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
        this.chatAdapter = ml.createChatAdapter(this.userDest,this);
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
