package com.example.chitchat;

import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private static Map<String, List<Message>> messagesOfUser;
    private static List <Message> messageList;
    private final String username;
    private final DataChangedListener dataChangedListener;
    private int pos;

    public ChatAdapter(List<Message> messageList,String username, DataChangedListener dcl){
        this.dataChangedListener = dcl;
        if(messagesOfUser==null){
            messagesOfUser = new HashMap<>();
        }
        ChatAdapter.messageList = messageList;
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
        cvh.reset();
        Message msg = messageList.get(pos);
        cvh.bind(msg);
        cvh.itemView.setOnLongClickListener(v -> {
            setPosition(cvh.getAdapterPosition());
            return false;
        });
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

    public Message getItem(int position) {
        if (position >= 0 && position < messageList.size()) {
            return messageList.get(position);
        }
        return null;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public int getPos() {
        return pos;
    }

    public void setPosition(int pos) {
        this.pos = pos;
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

    public void editMsg(UUID msgID, String newContent, long newTimestamp) {
        Message msg = findMsgById(msgID);
        if (msg != null) {
            editMsg(msg, newContent,newTimestamp);
        }
    }

    public void editMsg(Message msg, String newContent,long newTimestamp) {
        msg.setContent(newContent);
        msg.setState(Message.State.EDITED);
        msg.setChangedTimestamp(newTimestamp);
        notifyItemChanged(messageList.indexOf(msg));
    }

    public void deleteMsg(UUID msgID,long newTimestamp) {
        Message msg = findMsgById(msgID);
        if (msg != null) {
            deleteMsg(msg,newTimestamp);
        }
    }

    public void deleteMsg(Message msg,long newTimestamp) {
        String deleteMessageText = "Deleted";
        msg.setContent(deleteMessageText);
        msg.setState(Message.State.DELETED);
        msg.setChangedTimestamp(newTimestamp);
        notifyItemChanged(messageList.indexOf(msg));
    }

    private Message findMsgById(UUID msgID) {
        for (Message msg : messageList) {
            if (msg.getID().equals(msgID)) {
                return msg;
            }
        }
        return null;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        SimpleDateFormat sdf;
        private final TextView  msgTextView;
        private final TextView usernameTextView;
        private final TextView timestampTextView;
        private final LinearLayout backgroundContainer;
        private final LinearLayout chatContainer;

        private final TextView changedTimestampTextView;

        private final TextView changedLabelTextView;
        private final View spacerLine;

        private final LinearLayout editContextContainer;

        private boolean isIncoming;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            this.sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
            this.msgTextView = itemView.findViewById(R.id.messageTextView);
            this.usernameTextView = itemView.findViewById(R.id.usernameTextView);
            this.timestampTextView = itemView.findViewById(R.id.timestampTextView);
            this.backgroundContainer = itemView.findViewById(R.id.msgBgContainer);
            this.chatContainer = itemView.findViewById(R.id.messageContainer);
            this.editContextContainer = itemView.findViewById(R.id.editContextContainer);
            this.spacerLine = itemView.findViewById(R.id.spacerLine);
            this.changedTimestampTextView = itemView.findViewById(R.id.changedTimestampTextView);
            this.changedLabelTextView = itemView.findViewById(R.id.changedLabelTextView);

            itemView.setOnCreateContextMenuListener(this);
        }
        public void bind(Message msg){
            this.msgTextView.setText(msg.getContent());
            String displayName = msg.getIsIncoming() ? msg.getSendername() : "You";
            this.usernameTextView.setText(displayName);
            this.timestampTextView.setText(this.sdf.format(msg.getTimestamp()));
            int bgIndex = msg.getIsIncoming()?R.drawable.recv_msg_bg:R.drawable.sent_msg_bg;
            this.backgroundContainer.setBackgroundResource(bgIndex);
            int gravity = msg.getIsIncoming()? Gravity.START : Gravity.END;
            this.isIncoming = msg.getIsIncoming();
            this.chatContainer.setGravity(gravity);

            Message.State messageState = msg.getState();
            if(messageState == Message.State.DELETED || messageState == Message.State.EDITED){
                this.editContextContainer.setVisibility(View.VISIBLE);
                this.spacerLine.setVisibility(View.VISIBLE);
                this.changedTimestampTextView.setText(this.sdf.format(msg.getChangedTimestamp()));
                if(messageState == Message.State.DELETED){
                    this.backgroundContainer.setBackgroundResource(R.drawable.deleted_msg_bg);
                    this.changedLabelTextView.setText(R.string.deleted_on);
                }
                else{
                    this.changedLabelTextView.setText(R.string.edited_on);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo){
            int position = getAdapterPosition();
            Message msg = messageList.get(position);

            boolean isDeleted = (msg.getState() == Message.State.DELETED);
            if(isIncoming){
                MenuItem dfm = menu.add(0,R.id.context_delete_for_me,0,"Delete for me");
                dfm.setEnabled(!isDeleted);
            }else{
                MenuItem eMsg = menu.add(0,R.id.context_edit,0, "Edit message");
                MenuItem dfm = menu.add(0,R.id.context_delete_for_me,1,"Delete for me");
                MenuItem dfa = menu.add(0,R.id.context_delete_for_all,2,"Delete for all");
                eMsg.setEnabled(!isDeleted);
                dfm.setEnabled(!isDeleted);
                dfa.setEnabled(!isDeleted);
            }
        }

        public void reset(){
            editContextContainer.setVisibility(View.GONE);
            spacerLine.setVisibility(View.GONE);
        }
    }
}
