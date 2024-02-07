package de.lorenz.da_exam_project.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

import de.lorenz.da_exam_project.ChatRoomActivity;
import de.lorenz.da_exam_project.Constants;
import de.lorenz.da_exam_project.R;
import de.lorenz.da_exam_project.models.ChatRoom;
import de.lorenz.da_exam_project.models.User;
import de.lorenz.da_exam_project.utils.AndroidUtil;
import de.lorenz.da_exam_project.utils.ChatRoomUtil;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class ChatListRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoom, ChatListRecyclerAdapter.ChatModelViewHolder> {

    Context context;
    TextView noChatsTextView;

    public ChatListRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoom> options, Context context, TextView noChatsTextView) {
        super(options);
        this.context = context;
        this.noChatsTextView = noChatsTextView;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatRoom model) {
        List<String> userIds = model.getUserIds();

        if (!userIds.contains(FirebaseUtil.getCurrentUserId())) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));   // set height to 0
            return;
        }

        // setup chat room rows specific to group or single chat
        if (userIds.size() >= Constants.MIN_GROUP_SIZE) {
            setupGroupChatRow(holder, model, userIds);
        } else {
            setupSingleChatRow(holder, model, userIds);
        }

        // hide checkbox
        holder.checkBox.setVisibility(View.GONE);

        // get last message
        String lastMessage = model.getLastMessage();

        // add prefix if last message is from own user
        String lastMessageSenderId = model.getLastMessageSenderId();
        if (lastMessageSenderId != null && lastMessageSenderId.equals(FirebaseUtil.getCurrentUserId())) {

            lastMessage = "You: " + lastMessage;
        }

        // get and set last message timestamp
        String lastMessageTimestamp = AndroidUtil.getFormattedDate(context, model.getLastMessageTimestamp());
        holder.lastDateTextView.setText(lastMessageTimestamp);

        // set last message
        holder.lastMessageTextView.setText(lastMessage);

        // add click listener on item list
        holder.itemView.setOnClickListener(v -> {
            // navigate to chat activity
            Intent intent = new Intent(context, ChatRoomActivity.class);
            AndroidUtil.passChatRoomAsIntent(intent, model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    /**
     * Hides or shows the no chats text view depending on the item count.
     */
    private void checkForEmptyDataSet() {
        if (getItemCount() == 0) {
            this.noChatsTextView.setVisibility(View.VISIBLE);
        } else {
            this.noChatsTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();

        checkForEmptyDataSet();
    }

    private void setupSingleChatRow(ChatModelViewHolder holder, ChatRoom model, List<String> userIds) {
        String partnerId = ChatRoomUtil.getPartnerId(userIds);

        FirebaseUtil.getUser(partnerId).get().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                return;
            }

            User partner = task.getResult().toObject(User.class);
            holder.usernameTextView.setText(partner.getUsername());

            // if the user is the current user, highlight it
            if (partner.getUserId().equals(FirebaseUtil.getCurrentUserId())) {
                String youUsername = "You";
                holder.usernameTextView.setText(youUsername);
                holder.profilePicView.setBackgroundTintList(context.getResources().getColorStateList(R.color.white, context.getTheme()));
                holder.itemView.setEnabled(false);
                holder.itemView.setAlpha(0.4f);
            }

        });
    }

    private void setupGroupChatRow(ChatModelViewHolder holder, ChatRoom model, List<String> userIds) {

        String title = model.getTitle();
        holder.usernameTextView.setText(title);
    }


    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    static class ChatModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView lastMessageTextView;
        TextView lastDateTextView;
        TextView currentUserIdTextView;
        ImageView profilePicView;
        CheckBox checkBox;

        public ChatModelViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_text_view);
            lastMessageTextView = itemView.findViewById(R.id.last_message_text_view);
            lastDateTextView = itemView.findViewById(R.id.last_date_text_view);
            currentUserIdTextView = itemView.findViewById(R.id.current_user_id_text_view);
            profilePicView = itemView.findViewById(R.id.profile_pic_view);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
