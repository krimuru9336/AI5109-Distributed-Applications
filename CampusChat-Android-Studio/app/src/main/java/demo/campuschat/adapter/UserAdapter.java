package demo.campuschat.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import demo.campuschat.R;
import demo.campuschat.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private static List<User> userList = null;
    private HashSet<String> selectedUserIds = new HashSet<>();

    private final OnUserClickListener userClickListener;

    public UserAdapter(List<User> userList, OnUserClickListener userClickListener) {
        UserAdapter.userList = userList;
        this.userClickListener = userClickListener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view, userClickListener);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userNameView.setText(user.getUserName());
        holder.userEmailView.setText(user.getUserEmail());

        // Update background based on selection state
        holder.itemView.setBackgroundColor(selectedUserIds.contains(user.getUserId()) ? Color.LTGRAY : Color.TRANSPARENT);

        holder.checkBoxUser.setChecked(selectedUserIds.contains(user.getUserId()));

        holder.checkBoxUser.setOnCheckedChangeListener(null);
        holder.checkBoxUser.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                selectedUserIds.add(user.getUserId());
            } else {
                selectedUserIds.remove(user.getUserId());
            }
        }));

        holder.itemView.setOnClickListener(v -> {
            if(userClickListener != null) {
                userClickListener.onUserClicked(user);
            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface OnUserClickListener {
        void onUserClicked(User user);
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userNameView;
        TextView userEmailView;
        CheckBox checkBoxUser;

        public UserViewHolder(View itemView, OnUserClickListener userClickListener) {
            super(itemView);
            userNameView = itemView.findViewById(R.id.text_user_name);
            userEmailView = itemView.findViewById(R.id.text_user_email);
            checkBoxUser = itemView.findViewById(R.id.checkbox_user);


            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    User user = userList.get(position);
                    if (selectedUserIds.contains(user.getUserId())) {
                        selectedUserIds.remove(user.getUserId());
                    } else {
                        selectedUserIds.add(user.getUserId());
                    }
                    notifyItemChanged(position);
                }
                if (userClickListener != null) {
                    userClickListener.onUserClicked(userList.get(position));
                }
            });
        }
    }

    public Set<String> getSelectedUserIds() {
        return new HashSet<>(selectedUserIds);
    }
}
