package com.example.mychatapplication.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychatapplication.databinding.ItemContainerUserBinding;
import com.example.mychatapplication.listeners.UserListener;
import com.example.mychatapplication.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private final List<User> users;
    private final UserListener userListener;
    private final List<String> selectedUsers;
    private String createType;

    public UsersAdapter(List<User> users, UserListener userListener,String createType) {
        this.users = users;
        this.userListener = userListener;
        selectedUsers = new ArrayList<>();
        this.createType=createType;
    }

    public void setUsers(List<User> users) {
        this.users.clear();
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public List<String> getSelectedUsers() {
        return selectedUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding binding = ItemContainerUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position),holder.itemView);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerUserBinding binding;

        UserViewHolder(ItemContainerUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(User user, View context) {
            binding.textName.setText(user.getName());
            binding.textEmail.setText(user.getEmail());

            if(user.getImage()!=null) {
                byte[] bytes = Base64.decode(user.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                binding.imageProfile.setImageBitmap(bitmap);
            }

            binding.getRoot().setOnClickListener(v -> {
                userListener.onUserClicked(user);
            });


            if(createType.equals("group")){
                binding.checkboxSelect.setVisibility(View.VISIBLE);
                binding.checkboxSelect.setOnCheckedChangeListener(null); // Avoid recycling issues
                binding.checkboxSelect.setChecked(selectedUsers.contains(user));

                binding.checkboxSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedUsers.add(user.getId());
                    } else {
                        selectedUsers.remove(user);
                    }
                });
            }

        }
    }
}
