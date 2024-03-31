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
    private List<ChatRoom> charRoom;
    private Context context;

    public ChatRoomAdapter(List<ChatRoom> charRoom, Context context) {
        this.charRoom = charRoom;
        this.context = context;
    }
    @Override
    public int getItemCount() {
        return charRoom.size();
    }
    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_room, parent, false);
        return new ChatRoomViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        ChatRoom chatRoom = charRoom.get(position);
        if(chatRoom.getUsers().size() > 2){
            holder.userTextView.setText(chatRoom.getGroupName());
        }else {
            for (ChatRoom.ChatRoomUser u:chatRoom.getUsers()
                 ) {
                if(!u.getName().equals(AllMethods.name)){
                    holder.userTextView.setText(u.getName());
                    break;
                }
            }
        }

        holder.room = chatRoom;
    }



    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView userTextView;
        Context context;
        ChatRoom room;


        public ChatRoomViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            userTextView = itemView.findViewById(R.id.userTextView);
            userTextView.setOnClickListener(this);
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
