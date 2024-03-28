package com.example.buddyapp;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ProfileViewHolder extends RecyclerView.ViewHolder {
    TextView textViewname,textViewProfession,viewUserProfile,sendmessagebtn;
    ImageView imageView;
    CardView cardView;

    public ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public void setProfile(FragmentActivity fragmentActivity,String name,String uid,String prof,String url){
        cardView = itemView.findViewById(R.id.cardView_profile);
        textViewname = itemView.findViewById(R.id.name_profile);
        textViewProfession = itemView.findViewById(R.id.prof_profile);
        viewUserProfile = itemView.findViewById(R.id.viewProfileButton);
        imageView = itemView.findViewById(R.id.profileImage);

        Picasso.get().load(url).into(imageView);
        textViewProfession.setText(prof);
        textViewname.setText(name);
    }
    public void setProfileInchat(Application application, String name, String uid, String prof, String url){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        ImageView imageView1 = itemView.findViewById(R.id.iv_ch_item);
        TextView nametv= itemView.findViewById(R.id.name_ch_item_tv);
        TextView proftv = itemView.findViewById(R.id.ch_itemprof_tv);
        sendmessagebtn = itemView.findViewById(R.id.send_message_item_btn);

        if(currentuid.equals(uid)){
            Picasso.get().load(url).into(imageView1);
            nametv.setText(name);
            proftv.setText(prof);
            sendmessagebtn.setVisibility(View.INVISIBLE);
        }else {
            Picasso.get().load(url).into(imageView1);
            nametv.setText(name);
            proftv.setText(prof);
        }

    }
}
