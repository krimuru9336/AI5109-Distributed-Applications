package com.example.chatapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public TextView senderText;
        public TextView timestampText;

        public MessageViewHolder(View itemView, final MessageClickListener clickListener) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            senderText = itemView.findViewById(R.id.text_message_sender);
            timestampText = itemView.findViewById(R.id.text_message_timestamp);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onMessageClick(messageList.get(position));
                    }
                }
            });
        }
    }

    public interface MessageClickListener {
        void onMessageClick(Message message);
    }

    private MessageClickListener clickListener;

    public MessageAdapter(List<Message> messageList, MessageClickListener clickListener) {
        this.messageList = messageList;
        this.clickListener = clickListener;
    }

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageText.setText(message.getText());
        holder.senderText.setText(message.getSender());
        holder.timestampText.setText(formatTimestamp(message.getTimestamp()));

    }

    private String formatTimestamp(long timestamp) {
        // Assuming the timestamp is in milliseconds
        Date date = new Date(timestamp);
        SimpleDateFormat format = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return format.format(date);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
