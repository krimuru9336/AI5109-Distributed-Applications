package com.example.chitchat;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


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
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v->finish());
    }
    public void sendMessage(View view){
        String msgContent = this.messageEditText.getText().toString();
        if(!msgContent.isEmpty()){
            long timestamp = System.currentTimeMillis();
            webSocketHandler.sendMessage(this.userDest,msgContent,timestamp);
            this.chatAdapter.addMessage(new Message(msgContent,userDest,false,timestamp));
            this.messageEditText.setText("");
        }
    }
    @Override
    public void onDataChanged() {
        recyclerView.smoothScrollToPosition(this.chatAdapter.getItemCount() - 1);
    }
}
