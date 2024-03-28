package com.example.buddyapp.groupchat;

import android.app.Application;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buddyapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class UserSelectionViewHolder  extends RecyclerView.ViewHolder{
    public CheckBox checkBox;
    public UserSelectionViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setUserSelection(Application application, String name, String uid, String prof,String url){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        ImageView imageView1 = itemView.findViewById(R.id.imgview_prof);
        TextView nametv= itemView.findViewById(R.id.name_user_grp);
        TextView proftv = itemView.findViewById(R.id.user_prof_grp);
        checkBox=itemView.findViewById(R.id.checkBoxForuser);
        if(currentuid.equals(uid)){
            checkBox.setVisibility(View.INVISIBLE);
            Picasso.get().load(url).into(imageView1);
            nametv.setText(name);
            proftv.setText(prof);
        }else {
            Picasso.get().load(url).into(imageView1);
            nametv.setText(name);
            proftv.setText(prof);
        }
    }

    public void setUserSelection1(Application application, String groupname, String createdby){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        ImageView imageView1 = itemView.findViewById(R.id.imgview_prof);
        TextView nametv= itemView.findViewById(R.id.name_user_grp);
        TextView proftv = itemView.findViewById(R.id.user_prof_grp);
        checkBox=itemView.findViewById(R.id.checkBoxForuser);
        nametv.setText(groupname);
        proftv.setText(createdby);

    }
}
