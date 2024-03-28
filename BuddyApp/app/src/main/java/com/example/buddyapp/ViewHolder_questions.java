package com.example.buddyapp;

import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewHolder_questions extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView time_result,name_result,question_result,deletebtn,replybtn,replybtn1;
    ImageButton fvrt_btn;
    DatabaseReference favouriteref;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/");

    public ViewHolder_questions(@NonNull View itemView) {
        super(itemView);
    }
    public void setitem(FragmentActivity activity,String name,String url,String userid,String key,String question,String privacy,String time){
        imageView= itemView.findViewById(R.id.iv_que_item);
        time_result=itemView.findViewById(R.id.time_que_item_tv);
        name_result=itemView.findViewById(R.id.name_que_item_tv);
        question_result=itemView.findViewById(R.id.que_item_tv);
        replybtn=itemView.findViewById(R.id.reply_item_que);

        Picasso.get().load(url).into(imageView);
        time_result.setText(time);
        name_result.setText(name);
        question_result.setText(question);
    }


    public void favouriteChecker(String postkey) {
        fvrt_btn=itemView.findViewById(R.id.fvrt_f2_item);

        favouriteref = database.getReference("favourites");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuserid = user.getUid();

        favouriteref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postkey).hasChild(currentuserid)){
                    fvrt_btn.setImageResource(R.drawable.baseline_bookmark_24);
                }
                else{
                    fvrt_btn.setImageResource(R.drawable.baseline_bookmark_border_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void setitemRelated(Application activity, String name, String url, String userid, String key, String question, String privacy, String time){
        TextView timetv=itemView.findViewById(R.id.time_que_related_tv);
        ImageView imageView1 = itemView.findViewById(R.id.iv_que_related);
        TextView nametv = itemView.findViewById(R.id.name_que_related_tv);
        TextView quetv = itemView.findViewById(R.id.que_related_tv);
        //TextView viewreply = itemView.findViewById(R.id.view_related_tv);
        //TextView replybtn = itemView.findViewById(R.id.view_related_tv);
        replybtn1=itemView.findViewById(R.id.view_related_tv);

        Picasso.get().load(url).into(imageView1);
        nametv.setText(name);
        timetv.setText(time);
        quetv.setText(question);

    }
    public void setitemDelete(Application activity, String name, String url, String userid, String key, String question, String privacy, String time){
        TextView timetv=itemView.findViewById(R.id.time_que_Your_tv);
        ImageView imageView1 = itemView.findViewById(R.id.iv_que_Your_Question);
        TextView nametv = itemView.findViewById(R.id.name_que_your_tv);
        TextView quetv = itemView.findViewById(R.id.que_Your_tv);
         deletebtn = itemView.findViewById(R.id.view_Your_tv);

        Picasso.get().load(url).into(imageView1);
        nametv.setText(name);
        timetv.setText(time);
        quetv.setText(question);

    }
}
