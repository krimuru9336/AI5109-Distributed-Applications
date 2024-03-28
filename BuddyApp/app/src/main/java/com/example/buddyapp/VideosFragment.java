package com.example.buddyapp;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.squareup.picasso.Picasso;

import java.util.Collections;

public class VideosFragment extends RecyclerView.ViewHolder {
    public VideosFragment(@NonNull View itemView) {
        super(itemView);
    }
    public  void setVideos(FragmentActivity activity, String name, String url, String postUri, String time,
                         String uid, String type, String desc){
        SimpleExoPlayer exoPlayer;
        PlayerView playerView  = itemView.findViewById(R.id.vv_post_ind);

            try{
                SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(activity).build();
                playerView.setPlayer(simpleExoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(postUri);
                simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(false);
            }catch (Exception e){
                Toast.makeText(activity, " Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

