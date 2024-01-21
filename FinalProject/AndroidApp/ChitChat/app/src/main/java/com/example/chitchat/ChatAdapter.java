package com.example.chitchat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private static Map<String, List<Message>> messagesOfUser;
    private final List <Message> messageList;
    private final String username;
    private final DataChangedListener dataChangedListener;

    public ChatAdapter(List<Message> messageList,String username, DataChangedListener dcl){
        this.dataChangedListener = dcl;
        if(messagesOfUser==null){
            messagesOfUser = new HashMap<>();
        }
        this.messageList = messageList;
        this.username = username;
        loadUserMessages();
    }
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message,parent,false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder cvh,int pos){
        Message msg = messageList.get(pos);
        cvh.bind(msg);
    }
    @Override
    public int getItemCount(){
        return messageList.size();
    }

    public String currentUsername(){
        return username;
    }
    private void loadUserMessages(){
        if (messageList != null && messageList.size()>0){
            notifyItemRangeInserted(0,messageList.size()-1);
        }
    }

    public void addMessage(Message msg){
        MessageStore.addMessage(username,msg);
        showNewMessage();
    }
    public void showNewMessage(){
        notifyItemInserted(messageList.size()-1);
        if(dataChangedListener != null){
            dataChangedListener.onDataChanged();
        }
    }
    static class ChatViewHolder extends RecyclerView.ViewHolder{
        SimpleDateFormat sdf;
        private final TextView  msgTextView;
        private final TextView usernameTextView;
        private final TextView timestampTextView;
        private final LinearLayout backgroundContainer;
        private final LinearLayout chatContainer;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            this.sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
            this.msgTextView = itemView.findViewById(R.id.messageTextView);
            this.usernameTextView = itemView.findViewById(R.id.usernameTextView);
            this.timestampTextView = itemView.findViewById(R.id.timestampTextView);
            this.backgroundContainer = itemView.findViewById(R.id.msgBgContainer);
            this.chatContainer = itemView.findViewById(R.id.messageContainer);
        }
        public void bind(Message msg){
            this.msgTextView.setText(msg.getContent());
            String displayName = msg.getIsIncoming() ? msg.getSendername() : "You";
            this.usernameTextView.setText(displayName);
            this.timestampTextView.setText(this.sdf.format(msg.getTimestamp()));
            int bgIndex = msg.getIsIncoming()?R.drawable.recv_msg_bg:R.drawable.sent_msg_bg;
            this.backgroundContainer.setBackgroundResource(bgIndex);
            int gravity = msg.getIsIncoming()? Gravity.START : Gravity.END;
            this.chatContainer.setGravity(gravity);
        }
    }
}
