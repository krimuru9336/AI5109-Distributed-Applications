package com.example.myapplication5.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication5.ChatActivity;
import com.example.myapplication5.HelperClass;
import com.example.myapplication5.R;
import com.example.myapplication5.utils.AndroidUtil;
import com.example.myapplication5.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder> implements Filterable {
    private List<HelperClass> userList;
    private List<HelperClass> filteredList;
    private List<String> selectedMembersIDs = new ArrayList<>(); // List to keep track of selected members
    private Context context;

    //new
    FirebaseAuth firebaseAuth;
    String uid;

    public GroupMemberAdapter(List<HelperClass> userList, Context context) {
        this.userList = userList;
        this.filteredList = new ArrayList<>(userList);
        this.context = context;
//new
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.create_group_row, parent, false);
        return new ViewHolder(view);
    }

//new
@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    HelperClass user = filteredList.get(position);
    holder.usernameTextView.setText(user.getUsername());

    if(user.getUserId().equals(FirebaseUtil.currentUserId())){
        holder.usernameTextView.setText(user.getUsername()+" (Me)");
    }

    // Set the CheckBox state based on whether the user is already selected
    holder.checkboxView.setChecked(selectedMembersIDs.contains(user.getUserId()));

    System.out.println("selected members->>> before onclick!!"  + selectedMembersIDs);

    // Add a click listener to the itemView to toggle the CheckBox state and update the member list
    holder.checkboxView.setOnClickListener(v -> {
        if (selectedMembersIDs.contains(user.getUserId())) {
            selectedMembersIDs.remove(user.getUserId());
        } else {
            selectedMembersIDs.add(user.getUserId());
        }
        System.out.println("selected members->>>in onclick!!"  + selectedMembersIDs);
        // Update the checkbox state based on the updated list
        holder.checkboxView.setChecked(selectedMembersIDs.contains(user.getUserId()));

        // Notify the adapter that the item has changed
        notifyItemChanged(position);
        // Optionally, update the UI or perform other actions based on the selection
    });

    System.out.println("selected members after on click->>>"  + selectedMembersIDs +"      "+ selectedMembersIDs.contains(user.getUserId()));
}


    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString().toLowerCase().trim();
                List<HelperClass> filteredResults = new ArrayList<>();

                if (query.isEmpty()) {
                    filteredResults.addAll(userList);
                } else {
                    for (HelperClass user : userList) {
                        if (user.getUsername().toLowerCase().contains(query)) {
                            filteredResults.add(user);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredResults;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList.clear();
                filteredList.addAll((List<HelperClass>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

//    public List<String> getSelectedUsersList() {
//        System.out.println("next clicked  "+selectedMembersIDs);
//        return selectedMembersIDs;
//    }

    public Map<String, String> getSelectedUsers() {
        Map<String, String> selectedMembersMap = new HashMap<>();
        for (String userId : selectedMembersIDs) {
            for (HelperClass user : userList) {
                if (user.getUserId().equals(userId)) {
                    selectedMembersMap.put(userId, user.getUsername());
                    break;
                }
            }
        }
        System.out.println("Selected members map: " + selectedMembersMap);
        return selectedMembersMap;
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        CheckBox checkboxView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_text);
            checkboxView = itemView.findViewById(R.id.user_checkbox);
        }

    }
}



