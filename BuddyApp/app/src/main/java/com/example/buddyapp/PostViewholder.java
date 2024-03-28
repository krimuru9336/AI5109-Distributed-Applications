package com.example.buddyapp;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;

public class PostViewholder extends RecyclerView.ViewHolder {
    ImageView imageViewprofile,iv_post;
    TextView tv_name,tv_desc,tv_likes,tv_comment,tv_time,tv_nameprofile;
    ImageButton likebtn,menuoptions,commentbtn;
    DatabaseReference likesref;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/");
    int likescount;

    public PostViewholder(@NonNull View itemView) {
        super(itemView);
    }
     public  void setPost(FragmentActivity activity,String name,String url,String postUri,String time,
                          String uid,String type,String desc){
        imageViewprofile = itemView.findViewById(R.id.iv_profile_item);
        iv_post = itemView.findViewById(R.id.iv_post_view);
        tv_desc = itemView.findViewById(R.id.tv_desc_post);
        tv_likes = itemView.findViewById(R.id.tv_Likes_post);
        tv_comment = itemView.findViewById(R.id.tv_comment_post);
        tv_time = itemView.findViewById(R.id.tv_time_post);
        tv_nameprofile = itemView.findViewById(R.id.tv_name_post);
        commentbtn = itemView.findViewById(R.id.commentsbutton_posts);
        menuoptions = itemView.findViewById(R.id.morebutton_posts);
        likebtn = itemView.findViewById(R.id.likebutton_posts);


         PlayerView playerView  = itemView.findViewById(R.id.exoplayer_item_post);

         if(type.equals("iv")){
             Picasso.get().load(url).into(imageViewprofile);
             Picasso.get().load(postUri).into(iv_post);
             tv_desc.setText(desc);
             tv_time.setText(time);
             tv_nameprofile.setText(name);
             playerView.setVisibility(View.INVISIBLE);
         } else if (type.equals("vv")) {
             iv_post.setVisibility(View.INVISIBLE);
             tv_desc.setText(desc);
             tv_time.setText(time);
             tv_nameprofile.setText(name);
             Picasso.get().load(url).into(imageViewprofile);

             try{
//                 BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(activity).build();
//                 ExoTrackSelection.Factory factory=new AdaptiveTrackSelection.Factory(bandwidthMeter);
//                // TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory());
//                 exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(activity);
//                 Uri video = Uri.parse(postUri);
//                 //DefaultHttpDataSource df = new DefaultHttpDataSource("video");
//                 DataSource.Factory  dataSourceFactory = new DefaultDataSourceFactory()
//                 ExtractorsFactory ef =  new DefaultExtractorsFactory();
//                 //MediaSource mediaSource =  new ExtractorMediaSource(video, (DataSource.Factory) df,ef,null,null);
//                 MediaSource mediaSource=new ExtractorMediaSource(video,df,ef,null,null);
//                 playerView.setPlayer(exoPlayer);
//                 exoPlayer.prepare(mediaSource);
//                 exoPlayer.setPlayWhenReady(false);
                 SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(activity).build();
                 playerView.setPlayer(simpleExoPlayer);
                 MediaItem mediaItem = MediaItem.fromUri(postUri);
                 simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                 simpleExoPlayer.prepare();
                 simpleExoPlayer.setPlayWhenReady(false);


             }catch (Exception e){
                 Log.d("setPost: ",e.getMessage());
                 Toast.makeText(activity, " 91 Error", Toast.LENGTH_SHORT).show();
             }
         }
     }
    public void likechecker(String postkey) {
        likebtn = itemView.findViewById(R.id.likebutton_posts);

        likesref= database.getReference("post likes");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuserid = user.getUid();

        likesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postkey).hasChild(currentuserid)){
                    likebtn.setImageResource(R.drawable.like);
                    likescount = (int)snapshot.child(postkey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likescount)+ "likes");
                }
                else{
                    likebtn.setImageResource(R.drawable.ic_dislike);
                    likescount = (int)snapshot.child(postkey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likescount)+ "likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
