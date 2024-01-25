package de.lorenz.da_exam_project.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.lorenz.da_exam_project.ChatRoomActivity;
import de.lorenz.da_exam_project.R;
import de.lorenz.da_exam_project.models.ChatRoom;
import de.lorenz.da_exam_project.models.User;
import de.lorenz.da_exam_project.utils.AndroidUtil;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class ChatListRecyclerAdapter extends FirestoreRecyclerAdapter<User, ChatListRecyclerAdapter.UserModelViewHolder> {

    Context context;

    public ChatListRecyclerAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull User model) {
        holder.usernameTextView.setText(model.getUsername());

        String chatRoomId = AndroidUtil.getChatRoomId(FirebaseUtil.getCurrentUserId(), model.getUserId());
        System.out.println("chatRoomId = " + chatRoomId);

        // get chat room
        FirebaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }

            // get chat room
            ChatRoom chatRoom = task.getResult().toObject(ChatRoom.class);

            // if the user is the current user, highlight it
            if (model.getUserId().equals(FirebaseUtil.getCurrentUserId())) {
                String youUsername = model.getUsername() + " (you)";
                holder.usernameTextView.setText(youUsername);
                holder.itemView.setEnabled(false);
                holder.itemView.setAlpha(0.4f);
            }

            // if chat room doesn't exists
            if (chatRoom == null) {
                // remove last message and date
                holder.lastMessageTextView.setVisibility(View.GONE);
                holder.lastDateTextView.setVisibility(View.GONE);

                // add note if chat hasn't started yet
                holder.usernameTextView.setText(holder.usernameTextView.getText() + " (no messages yet)");
                holder.usernameTextView.setTextSize(14);
                holder.profilePicView.setBackgroundTintList(context.getResources().getColorStateList(R.color.white, context.getTheme()));
                holder.itemView.setAlpha(0.4f);

            } else {    // chat room exists

                // get last message
                String lastMessage = chatRoom.getLastMessage();

                // add prefix if last message is from own user
                if (chatRoom.getLastMessageSenderId().equals(FirebaseUtil.getCurrentUserId())) {
                    lastMessage = "You: " + lastMessage;
                }

                // get and set last message timestamp
                String lastMessageTimestamp = AndroidUtil.getFormattedDate(context, chatRoom.getLastMessageTimestamp());
                holder.lastDateTextView.setText(lastMessageTimestamp);

                // set last message
                holder.lastMessageTextView.setText(lastMessage);

            }

            // add click listener on item list
            holder.itemView.setOnClickListener(v -> {
                // navigate to chat activity
                Intent intent = new Intent(context, ChatRoomActivity.class);
                AndroidUtil.passUserAsIntentExtra(intent, model);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });

        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    static class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView lastMessageTextView;
        TextView lastDateTextView;
        TextView currentUserIdTextView;
        ImageView profilePicView;

        public UserModelViewHolder(View itemView) {
            super(itemView);
            System.out.println("UserModelViewHolder");
            usernameTextView = itemView.findViewById(R.id.username_text_view);
            lastMessageTextView = itemView.findViewById(R.id.last_message_text_view);
            lastDateTextView = itemView.findViewById(R.id.last_date_text_view);
            currentUserIdTextView = itemView.findViewById(R.id.current_user_id_text_view);
            profilePicView = itemView.findViewById(R.id.profile_pic_view);
        }
    }
}
