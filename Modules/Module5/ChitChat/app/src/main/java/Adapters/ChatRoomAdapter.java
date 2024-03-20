package Adapters;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chitchat.ChatRoomActivity;
import com.example.chitchat.GroupChatActivity;
import com.example.chitchat.R;

import java.util.List;

import Models.AllMethods;
import Models.ChatRoom;
import Models.User;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {
    private List<ChatRoom> chatRooms;
    private Context context;

    public ChatRoomAdapter(List<ChatRoom> chatRooms, Context context) {
        this.chatRooms = chatRooms;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_room, parent, false);
        return new ChatRoomViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        ChatRoom chatRoom = chatRooms.get(position);
        if(chatRoom.getUsers().size() > 2){
            holder.usernameTextView.setText(chatRoom.getGroupName());
        }else {
            for (ChatRoom.ChatRoomUser u:chatRoom.getUsers()
                 ) {
                if(!u.getName().equals(AllMethods.name)){
                    holder.usernameTextView.setText(u.getName());
                    break;
                }
            }
        }

        holder.room = chatRoom;
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView usernameTextView;
        ChatRoom room;
        Context context;


        public ChatRoomViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            usernameTextView.setOnClickListener(this);
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            AllMethods.CurrentSelectedRoom = room;
            AllMethods.chatroomKey = room.getKey();
            context.startActivity(new Intent(v.getContext(), GroupChatActivity.class));
        }
    }
}
