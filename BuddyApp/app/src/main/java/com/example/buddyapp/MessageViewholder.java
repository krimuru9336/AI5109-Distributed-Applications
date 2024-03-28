package com.example.buddyapp;

import android.app.Application;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buddyapp.groupchat.GroupMessagePojo;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Collections;

import pl.droidsonroids.gif.GifImageView;

public class MessageViewholder extends RecyclerView.ViewHolder {
    public TextView senderTv, receiverTv,senderTv_group,receiverTv_group;
    public ImageView iv_sender, iv_receiver;
    public ImageButton play_sender, play_receiver;
    PlayerView senderplayerView;
    PlayerView receiverplayerView;
    MediaController mediaController;
    private ImageView gifImageView;

    public MessageViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setMessage(Application application, String message, String time, String date, String type,
                           String senderuid, String receiveruid, String sendername, String audio, String image, String video, String gifUrl) {
        senderTv = itemView.findViewById(R.id.sender_tv);
        receiverTv = itemView.findViewById(R.id.receiver_tv);
        senderTv_group = itemView.findViewById(R.id.sender_tv);
        receiverTv_group = itemView.findViewById(R.id.receiver_tv);
        iv_receiver = itemView.findViewById(R.id.iv_receiver);
        iv_sender = itemView.findViewById(R.id.iv_sender);
        play_sender = itemView.findViewById(R.id.play_message_sender);
        play_receiver = itemView.findViewById(R.id.play_message_receiver);
        senderplayerView = itemView.findViewById(R.id.senderVideoView);
        receiverplayerView = itemView.findViewById(R.id.receiver_VideoView);
        LinearLayout llsender = itemView.findViewById(R.id.llsender);
        LinearLayout llreceiver = itemView.findViewById(R.id.llreceiver);
        LinearLayout llsender_video = itemView.findViewById(R.id.llsender_video);
        LinearLayout llreceiver_video = itemView.findViewById(R.id.llreceiver_video);
        gifImageView = itemView.findViewById(R.id.gifImageView);
        mediaController = new MediaController(application);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        if (currentuid.equals(senderuid)) {
            if (type.equals("i")) {
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                iv_sender.setVisibility(View.VISIBLE);
                Picasso.get().load(image).into(iv_sender);
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.GONE);
            } else if (type.equals("t")) {
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.VISIBLE);
                senderTv.setText(message);
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
            } else if (type.equals("a")) {
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.VISIBLE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
            } else if (type.equals("v")) {
                //videoViewSender.setVisibility(View.VISIBLE);
                // Set up video playback logic using video URL (video)
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
                llsender_video.setVisibility(View.VISIBLE);
                llreceiver_video.setVisibility(View.GONE);
                SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(application).build();
                senderplayerView.setPlayer(simpleExoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(video));
                simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(false);
            }
        } else if (currentuid.equals(receiveruid)) {

            if (type.equals("i")) {
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.VISIBLE);
                Picasso.get().load(image).into(iv_receiver);
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.GONE);
            } else if (type.equals("t")) {
                receiverTv.setVisibility(View.VISIBLE);
                senderTv.setVisibility(View.GONE);
                receiverTv.setText(message);
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
            } else if (type.equals("a")) {
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                llreceiver.setVisibility(View.VISIBLE);
                llsender.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
            } else if (type.equals("v")) {
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
                llreceiver_video.setVisibility(View.VISIBLE);
                llreceiver_video.setVisibility(View.GONE);
                SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(application).build();
                receiverplayerView.setPlayer(simpleExoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(video));
                simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(false);
            } else if (type.equals("g")) {
                // If it's a GIF, load and display it using Glide
                gifImageView.setVisibility(View.VISIBLE);
                Glide.with(application)
                        .asGif()
                        .load(image) // Use the field where the GIF URL is stored
                        .into(gifImageView);
            } else {
                // If it's not a GIF, hide the gifImageView
                gifImageView.setVisibility(View.GONE);
            }

        }
    }

    public void setGroupMessage(Application application, GroupMessagePojo message) {
        senderTv = itemView.findViewById(R.id.sender_tv);
        receiverTv = itemView.findViewById(R.id.receiver_tv);
        iv_receiver = itemView.findViewById(R.id.iv_receiver);
        iv_sender = itemView.findViewById(R.id.iv_sender);
        play_sender = itemView.findViewById(R.id.play_message_sender);
        play_receiver = itemView.findViewById(R.id.play_message_receiver);
        senderplayerView = itemView.findViewById(R.id.senderVideoView);
        receiverplayerView = itemView.findViewById(R.id.receiver_VideoView);
        LinearLayout llsender = itemView.findViewById(R.id.llsender);
        LinearLayout llreceiver = itemView.findViewById(R.id.llreceiver);
        LinearLayout llsender_video = itemView.findViewById(R.id.llsender_video);
        LinearLayout llreceiver_video = itemView.findViewById(R.id.llreceiver_video);
        gifImageView = itemView.findViewById(R.id.gifImageView);
        mediaController = new MediaController(application);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        String type = message.getType();

        if (currentuid.equals(message.getSender())) {
            if (type.equals("i")) {
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                iv_sender.setVisibility(View.VISIBLE);
//                Picasso.get().load(image).into(iv_sender);
//                llreceiver.setVisibility(View.GONE);
//                llsender.setVisibility(View.GONE);
            } else if (type.equals("t")) {
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.VISIBLE);
                senderTv.setText(message.getMessage());
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
            } else if (type.equals("a")) {
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.VISIBLE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
            } else if (type.equals("v")) {
                //videoViewSender.setVisibility(View.VISIBLE);
                // Set up video playback logic using video URL (video)
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
                llsender_video.setVisibility(View.VISIBLE);
                llreceiver_video.setVisibility(View.GONE);
                //videoViewSender.setVideoURI(Uri.parse(video));
                //mediaController.setAnchorView(videoViewSender);
                //videoViewSender.setMediaController(mediaController);
                //videoViewSender.start();
//                SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(application).build();
//                senderplayerView.setPlayer(simpleExoPlayer);
//                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(video));
//                simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
//                simpleExoPlayer.prepare();
//                simpleExoPlayer.setPlayWhenReady(false);
            }
        } else {

            if (type.equals("i")) {
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.VISIBLE);
//                Picasso.get().load(image).into(iv_receiver);
//                llreceiver.setVisibility(View.GONE);
//                llsender.setVisibility(View.GONE);
            } else if (type.equals("t")) {
                receiverTv.setVisibility(View.VISIBLE);
                senderTv.setVisibility(View.GONE);
                receiverTv.setText(message.getMessage());
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
            } else if (type.equals("a")) {
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                llreceiver.setVisibility(View.VISIBLE);
                llsender.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
            } else if (type.equals("v")) {
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
                llreceiver_video.setVisibility(View.VISIBLE);
                llreceiver_video.setVisibility(View.GONE);
//                SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(application).build();
//                receiverplayerView.setPlayer(simpleExoPlayer);
//                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(video));
//                simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
//                simpleExoPlayer.prepare();
//                simpleExoPlayer.setPlayWhenReady(false);
            } else if (type.equals("g")) {
                // If it's a GIF, load and display it using Glide
//                gifImageView.setVisibility(View.VISIBLE);
//                Glide.with(application)
//                        .asGif()
//                        .load(image) // Use the field where the GIF URL is stored
//                        .into(gifImageView);
            } else {
                // If it's not a GIF, hide the gifImageView
                gifImageView.setVisibility(View.GONE);
            }

        }
    }
}
