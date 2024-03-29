package com.example.mysheetchatda.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mysheetchatda.Models.ChatMessageModel;
import com.example.mysheetchatda.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
- Name: Adrianus Jonathan Engelbracht
- Matriculation number: 1151826
- Date: 29.03.2024
*/
public class ChatAdapter extends RecyclerView.Adapter {

    ArrayList<ChatMessageModel> chatMessageModel;
    Context context;
    String receiverId;

    final int SENDER_TEXT_VIEW_TYPE = 1;
    final int RECEIVER_TEXT_VIEW_TYPE = 2;
    final int SENDER_IMAGE_VIEW_TYPE = 3;
    final int RECEIVER_IMAGE_VIEW_TYPE = 4;
    final int SENDER_VIDEO_VIEW_TYPE = 5;
    final int RECEIVER_VIDEO_VIEW_TYPE = 6;

    final int SENDER_GIF_VIEW_TYPE = 7;
    final int RECEIVER_GIF_VIEW_TYPE = 8;


    public ChatAdapter(ArrayList<ChatMessageModel> chatMessageModel, Context context, String receiverId) {
        this.chatMessageModel = chatMessageModel;
        this.context = context;
        this.receiverId = receiverId;
    }

    public ChatAdapter(ArrayList<ChatMessageModel> chatMessageModel, Context context) {
        this.chatMessageModel = chatMessageModel;
        this.context = context;
    }


    // distinguish between different view types returns the appropriate viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case SENDER_IMAGE_VIEW_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.sender_image_message, parent, false);
                return new SenderImageViewHolder(view);
            case RECEIVER_IMAGE_VIEW_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.receiver_image_message, parent, false);
                return new ReceiverImageViewHolder(view);
            case SENDER_TEXT_VIEW_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.sender_text_message, parent, false);
                return new SenderTextViewHolder(view);
            case RECEIVER_TEXT_VIEW_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.receiver_text_message, parent, false);
                return new ReceiverTextViewHolder(view);
            case SENDER_VIDEO_VIEW_TYPE:
                Log.e("ChatAdapter", "onCreateViewHolder for sender video type");
                view = LayoutInflater.from(context).inflate(R.layout.sender_video_message, parent, false);
                return new SenderVideoViewHolder(view);
            case RECEIVER_VIDEO_VIEW_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.receiver_video_message, parent, false);
                return new ReceiverVideoViewHolder(view);
            case SENDER_GIF_VIEW_TYPE:
                Log.e("ChatAdapter", "OnCreateViewHolder Gif Send: ");
                view = LayoutInflater.from(context).inflate(R.layout.sender_gif_message, parent, false);
                return new SenderGifViewHolder(view);
            case RECEIVER_GIF_VIEW_TYPE:
                Log.e("ChatAdapter", "OnCreateViewHolder Gif Receive: ");
                view = LayoutInflater.from(context).inflate(R.layout.receiver_gif_message, parent, false);
                return new ReceiverGifViewHolder(view);
        }
        return null;
    }

    // gets the right item view type like text,image,gif,video and if it is receiver or sender
    @Override
    public int getItemViewType(int position) {
        ChatMessageModel message = chatMessageModel.get(position);
        if (message.getUserId().equals(FirebaseAuth.getInstance().getUid())) {
            // Sender
            if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
                return SENDER_IMAGE_VIEW_TYPE;
            } else if (message.getVideoUrl() != null && !message.getVideoUrl().isEmpty()) {
                return SENDER_VIDEO_VIEW_TYPE;
            } else if (message.getGifUrl() != null && !message.getGifUrl().isEmpty()) {
                return SENDER_GIF_VIEW_TYPE;
            } else {
                return SENDER_TEXT_VIEW_TYPE;
            }
        } else {
            // Receiver
            if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
                return RECEIVER_IMAGE_VIEW_TYPE;
            } else if (message.getVideoUrl() != null && !message.getVideoUrl().isEmpty()) {
                return RECEIVER_VIDEO_VIEW_TYPE;
            } else if (message.getGifUrl() != null && !message.getGifUrl().isEmpty()) {
                return RECEIVER_GIF_VIEW_TYPE;
            } else {
                return RECEIVER_TEXT_VIEW_TYPE;
            }
        }
    }


    // handling onBind of  different  view holders (messages, images, videos)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ChatMessageModel chatMsgModel = chatMessageModel.get(position);

        // gets the correct time for time stamps
        Date date = new Date(chatMsgModel.getTimestamp());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String strDate = simpleDateFormat.format(date);

        switch (holder.getItemViewType()) {
            case SENDER_TEXT_VIEW_TYPE:
                SenderTextViewHolder senderTextHolder = (SenderTextViewHolder) holder;
                senderTextHolder.senderMsg.setText(chatMsgModel.getMessageText());
                senderTextHolder.senderTime.setText(strDate.toString());
                break;
            case RECEIVER_TEXT_VIEW_TYPE:
                ReceiverTextViewHolder receiverTextHolder = (ReceiverTextViewHolder) holder;
                receiverTextHolder.receiverMsg.setText(chatMsgModel.getMessageText());
                receiverTextHolder.receiverTime.setText(strDate.toString());
                break;
            case SENDER_IMAGE_VIEW_TYPE:
                SenderImageViewHolder senderImageHolder = (SenderImageViewHolder) holder;
                Glide.with(context).load(chatMsgModel.getImageUrl()).into(senderImageHolder.image);
                break;
            case RECEIVER_IMAGE_VIEW_TYPE:
                ReceiverImageViewHolder receiverImageHolder = (ReceiverImageViewHolder) holder;
                Glide.with(context).load(chatMsgModel.getImageUrl()).into(receiverImageHolder.image);
                break;
            case SENDER_VIDEO_VIEW_TYPE:
                SenderVideoViewHolder senderVideoHolder = (SenderVideoViewHolder) holder;
                if (senderVideoHolder != null || senderVideoHolder.videoView != null) {
                    Log.e("ChatAdapter", "svvt" + Uri.parse(chatMsgModel.getVideoUrl()));
                    senderVideoHolder.bind(chatMessageModel.get(position));
                } else {
                    Log.e("ChatAdapter", "VideoView is null");
                }
                break;
            case RECEIVER_VIDEO_VIEW_TYPE:
                ReceiverVideoViewHolder receiverVideoHolder = (ReceiverVideoViewHolder) holder;
                if (receiverVideoHolder != null || receiverVideoHolder.videoView != null) {
                    Log.e("ChatAdapter", "rvvt" + Uri.parse(chatMsgModel.getVideoUrl()));
                    receiverVideoHolder.bind(chatMessageModel.get(position));
                } else {
                    Log.e("ChatAdapter", "VideoView is null");
                }
                break;
            case SENDER_GIF_VIEW_TYPE:
                Log.e("ChatAdapter", "OnBndViewHolder Gif Sender");
                SenderGifViewHolder senderGifHolder = (SenderGifViewHolder) holder;
                Glide.with(context).asGif().load(chatMsgModel.getGifUrl()).into(senderGifHolder.gifView);
                break;
            case RECEIVER_GIF_VIEW_TYPE:
                Log.e("ChatAdapter", "OnBndViewHolder Gif Receive");
                ReceiverGifViewHolder receiverGifHolder = (ReceiverGifViewHolder) holder;
                Glide.with(context).asGif().load(chatMsgModel.getGifUrl()).into(receiverGifHolder.gifView);
                break;
        }

        holder.itemView.setOnLongClickListener(view -> {
            // options to display in the dialog
            final CharSequence[] options = {"Delete", "Edit"};

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Choose an action");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        // delete message
                        showDeleteConfirmation(position, chatMsgModel);
                    } else if (which == 1) {
                        // edit the message
                        showEditMessageDialog(position, chatMsgModel);
                    }
                }
            });
            builder.show();
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return chatMessageModel.size();
    }

    public class SenderTextViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime;
        public SenderTextViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderTextMessage);
            senderTime = itemView.findViewById(R.id.senderTimestamp);
        }
    }

    public class ReceiverTextViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg, receiverTime;
        public ReceiverTextViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiverTextMessage);
            receiverTime = itemView.findViewById(R.id.receiverTimestamp);
        }
    }

    public class SenderImageViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public SenderImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.senderImage);
        }
        void bind(ChatMessageModel message) {
            Glide.with(itemView.getContext()).load(message.getImageUrl()).into(image);
        }
    }

    public class ReceiverImageViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public ReceiverImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.receiverImage);
        }
        void bind(ChatMessageModel message) {
            Glide.with(itemView.getContext()).load(message.getImageUrl()).into(image);
        }
    }

    public class SenderGifViewHolder extends RecyclerView.ViewHolder {
        ImageView gifView;
        public SenderGifViewHolder(@NonNull View itemView) {
            super(itemView);
            gifView = itemView.findViewById(R.id.senderGifView);
        }
    }

    public class ReceiverGifViewHolder extends RecyclerView.ViewHolder {
        ImageView gifView;

        public ReceiverGifViewHolder(@NonNull View itemView) {
            super(itemView);
            gifView = itemView.findViewById(R.id.receiverGifView);
        }
    }


    public class SenderVideoViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        public SenderVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.senderVideoView);
        }

        void bind(ChatMessageModel message) {
            if (videoView != null) {
                Uri videoUri = Uri.parse(message.getVideoUrl());
                videoView.setVideoURI(videoUri);
                // set up MediaController
                MediaController mediaController = new MediaController(context);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
            }
        }
    }

    public class ReceiverVideoViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        public ReceiverVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.receiverVideoView);
        }

        void bind(ChatMessageModel message) {
            if (videoView != null) {
                Uri videoUri = Uri.parse(message.getVideoUrl());
                videoView.setVideoURI(videoUri);
                // set up MediaController
                MediaController mediaController = new MediaController(context);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
            }
        }
    }


    // delete dialog. which handles the deletion of the message in firebase
    private void showDeleteConfirmation(int position, ChatMessageModel chatMessageModelModel) {
        new AlertDialog.Builder(context)
                .setTitle("Delete")
                .setMessage("Do you want to delete this message?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
                        String receiverRoom = receiverId + FirebaseAuth.getInstance().getUid();

                        // delete message from sender room
                        database.getReference().child("Chats")
                                .child(senderRoom)
                                .child(chatMessageModelModel.getMessageId())
                                .removeValue()
                                .addOnSuccessListener(unused -> {
                                    Log.d("ChatAdapter", "Message deleted successfully");
                                    //message.remove(position);
                                    //notifyItemRemoved(position);
                                })
                                .addOnFailureListener(e -> Log.d("ChatAdapter", "Error deleting message", e));

                        // delete message from Receiver's Room
                        database.getReference().child("Chats")
                                .child(receiverRoom)
                                .child(chatMessageModelModel.getMessageId())
                                .removeValue()
                                .addOnSuccessListener(unused -> {
                                    Log.d("ChatAdapter", "Message deleted successfully (receiver)");
                                })
                                .addOnFailureListener(e -> Log.d("ChatAdapter", "Error deleting message (receiver)", e));

                    }
                })
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }


    // edit message dialog, which calls updateMessageInFirebase
    private void showEditMessageDialog(int position, ChatMessageModel chatMessageModelModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.edit_message_popup, null);

        EditText editText = view.findViewById(R.id.edit_message_text);
        editText.setText(chatMessageModelModel.getMessageText());

        builder.setView(view)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String updatedText = editText.getText().toString();
                        updateMessageInFirebase(position, chatMessageModelModel, updatedText);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // edits a message in firebase
    private void updateMessageInFirebase(int position, ChatMessageModel chatMessageModelModel, String updatedText) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
        String receiverRoom = receiverId + FirebaseAuth.getInstance().getUid();

        // edit message in sender room
        database.getReference().child("Chats")
                .child(senderRoom)
                .child(chatMessageModelModel.getMessageId())
                .child("messageText")
                .setValue(updatedText)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ChatAdapter", "Message updated");
                    chatMessageModelModel.setMessageText(updatedText); // Update local model
                    notifyItemChanged(position); // Refresh item
                })
                .addOnFailureListener(e -> Log.e("ChatAdapter", "Failed to update message.", e));

        // edit message in receiver room
        database.getReference().child("Chats")
                .child(receiverRoom)
                .child(chatMessageModelModel.getMessageId())
                .child("messageText")
                .setValue(updatedText)
                .addOnSuccessListener(aVoid -> {
                    Log.d("WhatsappTag", "Message updated (receiver)");
                    //messageModel.setMessageText(updatedText);
                    //notifyItemChanged(position);
                })
                .addOnFailureListener(e -> Log.e("WhatsappTag", "Failed to update message (receiver)", e));

    }


}
