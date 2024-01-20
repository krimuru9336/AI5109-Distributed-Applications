package com.example.distributedapplicationsproject.android;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.distributedapplicationsproject.R;
import com.example.distributedapplicationsproject.activities.ChatActivity;
import com.example.distributedapplicationsproject.models.chat.Chat;
import com.example.distributedapplicationsproject.models.chat.ChatInfo;
import com.example.distributedapplicationsproject.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder> {

    List<Chat> chatList;

    public ChatRecyclerAdapter(List<Chat> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @NotNull
    @Override
    public ChatRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_recycler_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatRecyclerAdapter.ViewHolder viewModel, int i) {
        Chat chat = chatList.get(i);
        ChatInfo chatInfo = Utils.getChatInfoFromChat(chat);
        viewModel.textTitle.setText(chatInfo.title);
        viewModel.textLastMessage.setText("TEST");
        viewModel.textLastMessageTime.setText("10:00");

        if (chat.getType() == Chat.ChatType.GROUP) {
            viewModel.imgChat.setImageResource(viewModel.itemView.getResources().getIdentifier("ic_group", "drawable", viewModel.itemView.getContext().getPackageName()));
        }

        viewModel.itemView.setOnClickListener(v -> {
            Intent intent = Utils.putChatInfoIntoIntent(new Intent(viewModel.itemView.getContext(), ChatActivity.class), chat);
            viewModel.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgChat;
        TextView textTitle;
        TextView textLastMessage;
        TextView textLastMessageTime;


        public ViewHolder(View view) {
            super(view);
            this.imgChat = view.findViewById(R.id.img_chat);
            this.textTitle = view.findViewById(R.id.text_chat_title);
            this.textLastMessage = view.findViewById(R.id.text_chat_last_message);
            this.textLastMessageTime = view.findViewById(R.id.text_chat_last_message_time);
        }

    }
}
