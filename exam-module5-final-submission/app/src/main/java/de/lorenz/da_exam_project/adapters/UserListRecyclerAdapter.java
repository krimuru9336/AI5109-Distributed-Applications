package de.lorenz.da_exam_project.adapters;

import android.content.Context;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

import de.lorenz.da_exam_project.R;
import de.lorenz.da_exam_project.listeners.NewChatClickListener;
import de.lorenz.da_exam_project.listeners.user_list.CheckboxChangeListener;
import de.lorenz.da_exam_project.models.ChatRoom;
import de.lorenz.da_exam_project.models.User;
import de.lorenz.da_exam_project.utils.AndroidUtil;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class UserListRecyclerAdapter extends FirestoreRecyclerAdapter<User, UserListRecyclerAdapter.UserModelViewHolder> {

    Context context;
    List<String> selectedUsers;
    FloatingActionButton addGroupButton;

    public UserListRecyclerAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context, List<String> selectedUsers, FloatingActionButton addGroupButton) {
        super(options);
        this.context = context;
        this.selectedUsers = selectedUsers;
        this.addGroupButton = addGroupButton;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserListRecyclerAdapter.UserModelViewHolder holder, int position, @NonNull User model) {
        holder.usernameTextView.setText(model.getUsername());

        String chatRoomId = AndroidUtil.getChatRoomId(Objects.requireNonNull(FirebaseUtil.getCurrentUserId()), model.getUserId());

        // get chat room
        FirebaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }

            ChatRoom chatRoom = task.getResult().toObject(ChatRoom.class);

            holder.lastMessageTextView.setVisibility(View.GONE);
            holder.lastDateTextView.setVisibility(View.GONE);

            // if the user is the current user, highlight it
            if (model.getUserId().equals(FirebaseUtil.getCurrentUserId())) {
                String youUsername = model.getUsername() + " (you)";
                holder.usernameTextView.setText(youUsername);
                holder.profilePicView.setBackgroundTintList(context.getResources().getColorStateList(R.color.light_gray, context.getTheme()));
                holder.itemView.setAlpha(0.4f);
                holder.itemView.setEnabled(false);
                holder.checkBox.setVisibility(View.GONE);
            }

            // add click listener on item list
            NewChatClickListener newChatClickListener = new NewChatClickListener(context, chatRoom, model);
            holder.itemView.setOnClickListener(newChatClickListener);

            // add meta data (tag) add change listener on checkbox
            holder.checkBox.setTag(model.getUserId());
            CheckboxChangeListener checkboxChangeListener = new CheckboxChangeListener(this, this.selectedUsers, this.addGroupButton);
            holder.checkBox.setOnCheckedChangeListener(checkboxChangeListener);
        });
    }

    @NonNull
    @Override
    public UserListRecyclerAdapter.UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recycler_row, parent, false);
        return new UserListRecyclerAdapter.UserModelViewHolder(view);
    }

    static class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView lastMessageTextView;
        TextView lastDateTextView;
        TextView currentUserIdTextView;
        ImageView profilePicView;
        CheckBox checkBox;

        public UserModelViewHolder(View itemView) {
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
