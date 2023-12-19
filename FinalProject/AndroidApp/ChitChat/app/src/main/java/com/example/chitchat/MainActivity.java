package com.example.chitchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    TextInputEditText inputMsgDB;
    MessageRepository messageRepository;
    ConstraintLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.layout_act1);
        inputMsgDB = findViewById(R.id.input_msg_db);
        messageRepository = new MessageRepository(this);
    }
    public void saveMsg(View view){
        String msg = inputMsgDB.getText().toString();
        if(!msg.isEmpty()){
           messageRepository.saveMessage(msg);
           inputMsgDB.setText("");
            Toast.makeText(this,"Message saved",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Enter a message",Toast.LENGTH_SHORT).show();
        }
    }
    public void readMsg(View view){
        String msg = messageRepository.readMessage();
        if(msg != null && !msg.isEmpty()){
            Toast.makeText(this,"Last message: "+msg,Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"No messages yet",Toast.LENGTH_LONG).show();
        }
    }
}