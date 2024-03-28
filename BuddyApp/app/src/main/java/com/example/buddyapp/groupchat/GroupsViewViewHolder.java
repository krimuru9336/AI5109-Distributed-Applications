package com.example.buddyapp.groupchat;

import android.app.Application;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buddyapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class GroupsViewViewHolder extends RecyclerView.ViewHolder{
    TextView sendGroupbtn;
    public GroupsViewViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setGroupSelection(Application application, String groupName, String createdBy, ArrayList<String> members){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        TextView createdbyText= itemView.findViewById(R.id.groupCreatedByText);
        TextView groupNameText= itemView.findViewById(R.id.groupNameText);
        sendGroupbtn=itemView.findViewById(R.id.send_message_GroupButton_btn);
        createdbyText.setText(createdBy);
        groupNameText.setText(groupName);
    }
}