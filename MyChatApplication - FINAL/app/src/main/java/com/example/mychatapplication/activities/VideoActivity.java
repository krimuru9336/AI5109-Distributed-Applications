package com.example.mychatapplication.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.mychatapplication.adapters.UsersAdapter;
import com.example.mychatapplication.databinding.ActivityPlayVideoBinding;
import com.example.mychatapplication.databinding.ActivityUsersBinding;
import com.example.mychatapplication.listeners.UserListener;
import com.example.mychatapplication.models.User;
import com.example.mychatapplication.utilities.Constants;
import com.example.mychatapplication.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends BaseActivity  {

    private ActivityPlayVideoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            String url = intent.getStringExtra("url");
            playVideoFromUrl(url,binding.videoView);

        }

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    public void playVideoFromUrl(String videoUrl, VideoView videoView) {
        // Create a media controller
        MediaController mediaController = new MediaController(VideoActivity.this);
        mediaController.setAnchorView(videoView);

        // Set the media controller to the video view
        videoView.setMediaController(mediaController);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        // Set the video URI
        Uri videoUri = Uri.parse(videoUrl);
        videoView.setVideoURI(videoUri);

        // Start playing the video
        videoView.start();
    }

}