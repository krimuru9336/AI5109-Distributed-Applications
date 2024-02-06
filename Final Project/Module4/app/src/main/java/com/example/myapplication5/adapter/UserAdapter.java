package com.example.myapplication5.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;



import androidx.recyclerview.widget.RecyclerView;

//import com.example.myapplication5.ChatActivity;
import com.example.myapplication5.ChatActivity;
import com.example.myapplication5.HelperClass;
import com.example.myapplication5.R;
import com.example.myapplication5.utils.AndroidUtil;
import com.example.myapplication5.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;

//import com.example.myapplication5.utils.AndroidUtil;
//import com.example.myapplication5.utils.FirebaseUtil;
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
//import com.google.firebase.firestore.auth.User;

import java.util.List;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;


import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;



public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements Filterable {
    private List<HelperClass> userList;
    private List<HelperClass> filteredList;
    private Context context;

    //new
    FirebaseAuth firebaseAuth;
    String uid;

    public UserAdapter(List<HelperClass> userList, Context context) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.row_user, parent, false);
        return new ViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //new
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {



        HelperClass user = filteredList.get(position);
        holder.usernameTextView.setText(user.getUsername());

        System.out.println(user.getUsername());
        System.out.println(FirebaseUtil.currentUserDetails()+" (from firebase)");

        if(user.getUserId().equals(FirebaseUtil.currentUserId())){
            holder.usernameTextView.setText(user.getUsername()+" (Me)");

        }

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent,user);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        });
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_text);
        }
    }
}




//public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
//
//    Context context;
//    ArrayList<User> list;
//    public MyAdapter(Context context, ArrayList<User> list) {
//        this.context = context;
//        this.list = list;
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.row_user,parent,false);
//        return new MyViewHolder(view);
//    }
//
//    @SuppressLint("RestrictedApi")
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        User user = list.get(position);
//        holder.username.setText(user.getUid());
////        if(user.getUid().equals(FirebaseUtil.currentUserId())){
////            holder.usernameText.setText(model.getUsername()+" (Me)");
////        }
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public static class MyViewHolder extends RecyclerView.ViewHolder{
//
//        TextView username;
//        public MyViewHolder(@NonNull View itemView) {
//            super(itemView);
//            username = itemView.findViewById(R.id.username_text);
//        }
//    }
//
//}

