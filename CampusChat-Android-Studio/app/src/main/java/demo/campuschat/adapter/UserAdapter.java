package demo.campuschat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import demo.campuschat.R;
import demo.campuschat.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private static List<User> userList = null;
    private final OnUserClickListener userClickListener;

    public UserAdapter(List<User> userList, OnUserClickListener userClickListener) {
        UserAdapter.userList = userList;
        this.userClickListener = userClickListener;
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
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface OnUserClickListener {
        void onUserClicked(User user);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userNameView;
        TextView userEmailView;

        public UserViewHolder(View itemView, OnUserClickListener userClickListener) {
            super(itemView);
            userNameView = itemView.findViewById(R.id.text_user_name);
            userEmailView = itemView.findViewById(R.id.text_user_email);

            itemView.setOnClickListener(v -> {
                if (userClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        userClickListener.onUserClicked(userList.get(position));
                    }
                }
            });
        }
    }
}
