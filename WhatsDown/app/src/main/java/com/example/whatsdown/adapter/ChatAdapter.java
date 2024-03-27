package com.example.whatsdown.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsdown.R;
import com.example.whatsdown.model.ChatMessageModel;
import com.example.whatsdown.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ChatAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatAdapter.ChatModelViewHolder> {
    private final ClickListener clickListener;

    Context context;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context, ClickListener cl) {
        super(options);
        this.context = context;
        clickListener = cl;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {

        switch (model.getMediaType()) {
            case "image":
                if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
                    // This is a media message
                    Glide.with(holder.itemView.getContext())
                            .load(model.getMessage())
                            .into(holder.rightChatImageView); // or holder.rightChatImageView
                    holder.rightChatImageView.setVisibility(View.VISIBLE); //
                    holder.rightChatLayout.setVisibility(View.VISIBLE);
                    holder.leftChatLayout.setVisibility(View.GONE);
                    holder.rightChatTimestamp.setText(model.getSenderId().substring(0, 5)+" "+model.getFormattedTimestampAsString());

                } else {
                    // This is a media message
                    Glide.with(holder.itemView.getContext())
                            .load(model.getMessage())
                            .into(holder.leftChatImageView); // or holder.rightChatImageView
                    holder.leftChatImageView.setVisibility(View.VISIBLE); //
                    holder.leftChatLayout.setVisibility(View.VISIBLE);
                    holder.rightChatLayout.setVisibility(View.GONE);
                    holder.leftChatTimestamp.setText(model.getSenderId().substring(0, 5)+" "+model.getFormattedTimestampAsString());

                }
                break;
            case "gif":
                if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
                    // This is a media message
                    Glide.with(holder.itemView.getContext())
                            .asGif()
                            .load(model.getMessage())
                            .into(holder.rightChatImageView); // or holder.rightChatImageView
                    holder.rightChatImageView.setVisibility(View.VISIBLE); //
                    holder.rightChatLayout.setVisibility(View.VISIBLE);
                    holder.leftChatLayout.setVisibility(View.GONE);
                    holder.rightChatTimestamp.setText(model.getSenderId().substring(0, 5)+" "+model.getFormattedTimestampAsString());
                } else {
                    // This is a media message
                    Glide.with(holder.itemView.getContext())
                            .asGif()
                            .load(model.getMessage())
                            .into(holder.leftChatImageView); // or holder.rightChatImageView
                    holder.leftChatImageView.setVisibility(View.VISIBLE); //
                    holder.leftChatLayout.setVisibility(View.VISIBLE);
                    holder.rightChatLayout.setVisibility(View.GONE);
                    holder.leftChatTimestamp.setText(model.getSenderId().substring(0, 5)+" "+model.getFormattedTimestampAsString());

                }
                break;
            /*case "video":
                StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("media/4334");
                File localFile;
                try {
                    localFile = File.createTempFile("videos", ".mp4");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                fileRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        Uri video = FileProvider.getUriForFile(context, context.getPackageName(), new File(localFile.getPath()));

                        holder.rightChatVideoView.setVideoURI(video);
                        holder.rightChatVideoView.setVisibility(View.VISIBLE); //
                        holder.rightChatLayout.setVisibility(View.VISIBLE);
                        holder.leftChatLayout.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("ChatAdapter", Objects.requireNonNull(exception.getMessage()));
                        // Handle any errors
                    }
                });
                if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
                    // This is a video message
                    *//*holder.rightChatVideoView.setVideoURI(Uri.parse(model.getMessage()));
                    holder.rightChatVideoView.setVisibility(View.VISIBLE); //
                    holder.rightChatLayout.setVisibility(View.VISIBLE);
                    holder.leftChatLayout.setVisibility(View.GONE);*//*
                } else {
                    // This is a video message
                    holder.leftChatVideoView.setVideoURI(Uri.parse(model.getMessage()));
                    holder.leftChatVideoView.setVisibility(View.VISIBLE); //
                    holder.leftChatLayout.setVisibility(View.VISIBLE);
                    holder.rightChatLayout.setVisibility(View.GONE);
                }
                break;*/
            default:
                if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
                    holder.leftChatLayout.setVisibility(View.GONE);
                    holder.rightChatTextview.setVisibility(View.VISIBLE);
                    holder.rightChatLayout.setVisibility(View.VISIBLE);
                    holder.rightChatTextview.setText(model.getMessage());
                    holder.rightChatTimestamp.setText(model.getSenderId().substring(0, 5)+" "+model.getFormattedTimestampAsString());
                } else {
                    holder.rightChatLayout.setVisibility(View.GONE);
                    holder.leftChatTextview.setVisibility(View.VISIBLE);
                    holder.leftChatLayout.setVisibility(View.VISIBLE);
                    holder.leftChatTextview.setText(model.getMessage());
                    holder.leftChatTimestamp.setText(model.getSenderId().substring(0, 5)+" "+model.getFormattedTimestampAsString());
                }
                break;
        }
       /* if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            holder.rightChatTimestamp.setText(model.getFormattedTimestampAsString());
        } else {
            holder.leftChatTimestamp.setText(model.getFormattedTimestampAsString());
        }*/
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder implements  View.OnLongClickListener{

        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextview, rightChatTextview;
        TextView leftChatTimestamp, rightChatTimestamp;

        ImageView leftChatImageView;
        ImageView rightChatImageView;

        VideoView leftChatVideoView;
        VideoView rightChatVideoView;


        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            leftChatTimestamp = itemView.findViewById(R.id.left_chat_timestamp);
            rightChatTimestamp = itemView.findViewById(R.id.right_chat_timestamp);
            leftChatImageView = itemView.findViewById(R.id.left_chat_imageview);
            rightChatImageView = itemView.findViewById(R.id.right_chat_imageview);
            leftChatVideoView = itemView.findViewById(R.id.left_chat_videoview);
            rightChatVideoView = itemView.findViewById(R.id.right_chat_videoview);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getBindingAdapterPosition();
            if (position >= 0) {
                clickListener.onItemLongClick(position, v);
                return true;
            }
            return false;
        }


    }
    public interface ClickListener {
        void onItemLongClick(int position, View v);
    }
}
