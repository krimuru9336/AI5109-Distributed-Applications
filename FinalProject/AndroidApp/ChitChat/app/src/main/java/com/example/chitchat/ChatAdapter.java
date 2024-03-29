package com.example.chitchat;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private final Context context;

    public ChatAdapter(List<Message> messageList,String username, DataChangedListener dcl,ChatActivity context){
        this.dataChangedListener = dcl;
        if(messagesOfUser==null){
            messagesOfUser = new HashMap<>();
        }
        ChatAdapter.messageList = messageList;
        this.username = username;
        this.context = context;
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
        cvh.bind(msg,context);
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
        msg.setType("");
        msg.setBase64data("");
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

        private final ImageView imageView;

        private boolean isIncoming;

        private ExoPlayer exoPlayer;

        private final PlayerView exoPlayerView;

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
            this.imageView = itemView.findViewById(R.id.imageView);
            this.exoPlayerView = itemView.findViewById(R.id.exoPlayerView);

            itemView.setOnCreateContextMenuListener(this);
        }
        public void bind(Message msg,Context context) {
            this.msgTextView.setText(msg.getContent());
            String displayName = msg.getIsIncoming() ? msg.getDisplayname() : "You";
            this.usernameTextView.setText(displayName);
            this.timestampTextView.setText(this.sdf.format(msg.getTimestamp()));
            int bgIndex = msg.getIsIncoming()?R.drawable.recv_msg_bg:R.drawable.sent_msg_bg;
            this.backgroundContainer.setBackgroundResource(bgIndex);
            int gravity = msg.getIsIncoming()? Gravity.START : Gravity.END;
            this.isIncoming = msg.getIsIncoming();
            this.chatContainer.setGravity(gravity);
            this.imageView.setVisibility(View.GONE);
            this.exoPlayerView.setVisibility(View.GONE);
            this.editContextContainer.setVisibility(View.GONE);
            this.spacerLine.setVisibility(View.GONE);

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
            if(messageState != Message.State.DELETED){
                if(msg.getBase64data()!=null&&!msg.getBase64data().equals("")){
                    if(msg.getType().contains("gif")){
                        Glide.with(context).asGif().load(Base64.decode(msg.getBase64data(),Base64.DEFAULT)).into(this.imageView);
                        this.imageView.setVisibility(View.VISIBLE);
                    }
                    else if(msg.getType().contains("video")){
                        if (exoPlayer == null) {
                            exoPlayer = new ExoPlayer.Builder(context).build();
                            this.exoPlayerView.setPlayer(exoPlayer);
                        }
                        try{
                            byte[] videoData = Base64.decode(msg.getBase64data(),Base64.DEFAULT);
                            File tempFile = File.createTempFile("video","."+msg.getType().split("/")[1]);
                            FileOutputStream fos = new FileOutputStream(tempFile);
                            fos.write(videoData);
                            fos.close();

                            MediaItem mediaItem = MediaItem.fromUri(Uri.fromFile(tempFile));
                            exoPlayer.setMediaItem(mediaItem);
                            this.exoPlayerView.setVisibility(View.VISIBLE);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    else if(msg.getType().contains("image")){
                        Glide.with(context).asBitmap().load(Base64.decode(msg.getBase64data(),Base64.DEFAULT)).into(this.imageView);
                        this.imageView.setVisibility(View.VISIBLE);
                    }
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
