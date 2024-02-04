package demo.campuschat.adapter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import demo.campuschat.R;
import demo.campuschat.model.Message;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final List<Message> messageList;
    private final int VIEW_TYPE_SENT = 1;
    private final int VIEW_TYPE_RECEIVED = 2;

    private final MessageLongClickListener longClickListener;

    public MessageAdapter(List<Message> messageList, MessageLongClickListener messageLongClickListener) {
        this.messageList = messageList;
        this.longClickListener = messageLongClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Message message = messageList.get(position);
        if (currentUser != null && message.getSenderId().equals(currentUser.getUid())) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_sent, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_received, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageTextView.setText(message.getMessageText());
        holder.timestampView.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", message.getTimestamp()));

        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            holder.itemView.setOnLongClickListener(view -> {
                if (longClickListener != null) {
                    longClickListener.onMessageLongClicked(view, message, position);
                }
                return true; // Indicates the callback consumed the long press
            });
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timestampView;
        View itemView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            messageTextView = itemView.findViewById(R.id.message_text_view);
            timestampView = itemView.findViewById(R.id.timestamp_view);
        }
    }
}
