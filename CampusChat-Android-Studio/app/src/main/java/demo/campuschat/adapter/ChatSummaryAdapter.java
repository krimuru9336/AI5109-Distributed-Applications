package demo.campuschat.adapter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import demo.campuschat.R;
import demo.campuschat.model.ChatSummary;

public class ChatSummaryAdapter extends RecyclerView.Adapter<ChatSummaryAdapter.ChatSummaryViewHolder> {
    private static List<ChatSummary> chatSummaries;
    private final OnChatSummaryClickListener listener;

    public ChatSummaryAdapter(List<ChatSummary> chatSummaries, OnChatSummaryClickListener listener) {
        ChatSummaryAdapter.chatSummaries = chatSummaries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatSummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_summary_item, parent, false);
        return new ChatSummaryViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ChatSummaryViewHolder holder, int position) {
        ChatSummary chatSummary = chatSummaries.get(position);
        holder.chatPartnerNameView.setText(chatSummary.getChatPartnerName());
        holder.lastMessageView.setText(chatSummary.getLastMessage());
        holder.timestampView.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", chatSummary.getLastMessageTimestamp()));
    }

    @Override
    public int getItemCount() {
        return chatSummaries.size();
    }

    static class ChatSummaryViewHolder extends RecyclerView.ViewHolder {
        TextView chatPartnerNameView;
        TextView lastMessageView;
        TextView timestampView;

        public ChatSummaryViewHolder(View itemView, OnChatSummaryClickListener listener) {
            super(itemView);
            chatPartnerNameView = itemView.findViewById(R.id.chat_partner_name);
            lastMessageView = itemView.findViewById(R.id.last_message);
            timestampView = itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(v -> {
                // Get the position of the ViewHolder
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onChatSummaryClicked(chatSummaries.get(position));
                }
            });
        }
    }


}
