package com.example.module5.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.module5.R;
import com.example.module5.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> users;

    StartChat listener;

    private int from ;

    private Context context;

    public UserAdapter(List<User> users, Context context, int i) {
        this.context = context;
        this.users = users;
        this.from = i;
        listener = (StartChat) context;

    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.userName.setText(user.getUserName());

        Drawable img = context.getResources().getDrawable(R.drawable.baseline_person_2_24);
        Drawable img2 = context.getResources().getDrawable(R.drawable.baseline_groups_2_24);
        if (user.getUserType() != null && user.getUserType().equals("group")){
            holder.userName.setCompoundDrawablesWithIntrinsicBounds(img2, null, null, null);
        }
        else {
            holder.userName.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }


        if (from==0){
            holder.checkbox.setVisibility(View.GONE);
        }
        else {
            holder.checkbox.setVisibility(View.VISIBLE);
        }

        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from==0){
                    if (user.getUserType() != null && user.getUserType().equals("group")){
                        listener.startChat(user.getUserId(),"group");
                    }
                    else {
                        listener.startChat(user.getUserId(),null);
                    }

                }
                else {
                    if (holder.checkbox.isChecked()) {
                        holder.checkbox.setChecked(false);
                    } else {
                        holder.checkbox.setChecked(true);
                    }

                    listener.selectUser(user.getUserId());
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        LinearLayout linear;

        CheckBox checkbox;

        public UserViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            checkbox = itemView.findViewById(R.id.checkbox);
            linear = itemView.findViewById(R.id.linear);

        }




    }


    public interface StartChat {
        void startChat(String chatId, String group);
        void selectUser(String userID);

    }


}

