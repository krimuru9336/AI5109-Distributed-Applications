package com.example.distributedapplicationsproject.android.listeners;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimatedImageDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;
import com.example.distributedapplicationsproject.firebase.StorageService;
import com.example.distributedapplicationsproject.models.Message;
import pl.droidsonroids.gif.GifImageView;

public class MediaDownloadListener implements StorageService.OnMediaDownloadListener {

    Message msg;
    View parentView;
    ImageView imgMedia;
    RelativeLayout layoutVideoMedia;
    VideoView videoMedia;
    ImageView imgPlayVideo;

    public MediaDownloadListener(Message msg, View parentView, ImageView imgMedia, RelativeLayout layoutVideoMedia, VideoView videoMedia, ImageView imgPlayVideo) {
        this.msg = msg;
        this.parentView = parentView;
        this.imgMedia = imgMedia;
        this.layoutVideoMedia = layoutVideoMedia;
        this.videoMedia = videoMedia;
        this.imgPlayVideo = imgPlayVideo;
    }

    @Override
    public void onMediaDownloaded() {

    }

    @Override
    public void onMediaFailed(Exception e) {

    }

    @Override
    public void onMediaProgression(int progression) {

    }

    @SuppressLint("NewApi")
    @Override
    public void onMediaLocalUrl(Uri mediaDownloadUri) {
        String mediaUrl = msg.getMediaUrl();
        // Very hacky maybe change
        if (mediaUrl.contains("%2Fimage%2F")) {
            imgMedia.setImageURI(mediaDownloadUri);
            imgMedia.setVisibility(View.VISIBLE);
            if (mediaUrl.contains(".gif")) {
                AnimatedImageDrawable animatedImageDrawable = ((AnimatedImageDrawable) imgMedia.getDrawable());
                animatedImageDrawable.setRepeatCount(AnimatedImageDrawable.REPEAT_INFINITE);
                animatedImageDrawable.start();
            }
        } else if (mediaUrl.contains("%2Fvideo%2F")) {
            videoMedia.setVideoURI(mediaDownloadUri);
            videoMedia.setOnPreparedListener(mediaPlayer -> {
                // When the video is prepared, start playback
                parentView.setOnClickListener(view -> {
                    if (videoMedia.isPlaying()) {
                        imgPlayVideo.setVisibility(View.VISIBLE);
                        videoMedia.pause();
                    } else {
                        imgPlayVideo.setVisibility(View.GONE);
                        videoMedia.start();
                    }
                });

            });
            videoMedia.setOnCompletionListener(mediaPlayer -> imgPlayVideo.setVisibility(View.VISIBLE));
            layoutVideoMedia.setVisibility(View.VISIBLE);
        }
    }
}
