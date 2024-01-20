package com.example.chitchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private Context context;
    private String messageSenderId;

    public MessageAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();
        this.context = context;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receiverMessageText;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverPicture;
        public VideoView messageReceiverVideo,messageSenderVideo;

        private MediaController mediaController; // Add this member variable
        private Context context; // Add this member variable




        public MessageViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            mediaController = new MediaController(context);

            // Set anchor view for MediaController
            mediaController.setAnchorView(messageReceiverVideo);


            senderMessageText = itemView.findViewById(R.id.sender_messsage_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = itemView.findViewById(R.id.message_profile_image);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);

            messageReceiverVideo = itemView.findViewById(R.id.sender_message_video_view);
            messageSenderVideo = itemView.findViewById(R.id.receiver_message_video_view);



            // Attach MediaController to VideoViews
            messageReceiverVideo.setMediaController(mediaController);
            messageSenderVideo.setMediaController(mediaController);

            messageReceiverVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaController.show();
                }
            });
            messageSenderVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaController.show();
                }
            });



        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout, viewGroup, false);

        return new MessageViewHolder(view, viewGroup.getContext());

    }


    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, @SuppressLint("RecyclerView") final int i) {

        Messages messages = userMessagesList.get(i);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")) {
                    String receiverImage = dataSnapshot.child("image").getValue().toString();
                    // You can use the receiverImage if needed
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Reset visibility
        messageViewHolder.receiverMessageText.setVisibility(View.GONE);
        messageViewHolder.receiverProfileImage.setVisibility(View.GONE);
        messageViewHolder.senderMessageText.setVisibility(View.GONE);
        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);

        messageViewHolder.messageReceiverVideo.setVisibility(View.GONE);
        messageViewHolder.messageSenderVideo.setVisibility(View.GONE);

        // Handle different message types
        if (fromMessageType.equals("text")) {
            if (fromUserID.equals(messageSenderId)) {
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                messageViewHolder.senderMessageText.setTextColor(Color.BLACK);
                messageViewHolder.senderMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
            } else {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                messageViewHolder.receiverMessageText.setTextColor(Color.BLACK);
                messageViewHolder.receiverMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
            }
        } else if (fromMessageType.equals("image")) {
            Log.d("PicassoDebug", "Image URL: " + messages.getMessage());
            if (fromUserID.equals(messageSenderId)) {
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);
            } else {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);
            }
        } else if (fromMessageType.equals("Video") || fromMessageType.equals("Gifs")) {
            if (fromUserID.equals(messageSenderId)) {
                messageViewHolder.messageSenderVideo.setVisibility(View.VISIBLE);
                Uri videoUri = Uri.parse(messages.getMessage());
                messageViewHolder.messageSenderVideo.setVideoURI(videoUri);
                // You may want to add media controls for the user to play/pause the video
                // messageViewHolder.messageSenderVideo.setMediaController(new MediaController(context));
                // Start the video
                messageViewHolder.messageSenderVideo.start();
            } else {
                // Handling video/gif display for receiver
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverVideo.setVisibility(View.VISIBLE);
                Uri videoUri = Uri.parse(messages.getMessage());
                messageViewHolder.messageReceiverVideo.setVideoURI(videoUri);

                // Add media controls for the receiver
                messageViewHolder.messageReceiverVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        messageViewHolder.mediaController.setMediaPlayer(messageViewHolder.messageReceiverVideo);
                        messageViewHolder.messageReceiverVideo.setMediaController(messageViewHolder.mediaController);
                        messageViewHolder.mediaController.setAnchorView(messageViewHolder.messageReceiverVideo);
                        messageViewHolder.messageReceiverVideo.start();
                    }
                });
                // You can use a placeholder image for videos, or display a video icon.
                // If your design requires a specific view for videos, make sure to update your XML layout accordingly.
            }
        }

        if(fromUserID.equals(messageSenderId)){
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(userMessagesList.get(i).getType().equals("image")){
                        CharSequence options[] = new CharSequence[]{
                                "Delete for me",
                                "Delete for everyone",
                                "Cancel"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                if(position==0)
                                {
                                    deleteSentMessage(position,messageViewHolder);

                                }else if(position==1)
                                {
                                    deleteMessageForEveryone(position,messageViewHolder);

                                }
                            }
                        });
                        builder.show();
                    }

                    else if(userMessagesList.get(i).getType().equals("text")){
                        CharSequence options[] = new CharSequence[]{
                                "Delete for me",
                                "Delete for everyone",
                                "Cancel"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                if(position==0)
                                {
                                    deleteSentMessage(position,messageViewHolder);

                                }else if(position==1)
                                {
                                    deleteMessageForEveryone(position,messageViewHolder);

                                }
                            }
                        });
                        builder.show();
                    }

                }
            });
        }
        else {
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(userMessagesList.get(i).getType().equals("image")){
                        CharSequence options[] = new CharSequence[]{
                                "Delete for me",
                                "Cancel"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                if(position==0)
                                {
                                    deleteReceiveMessage(position,messageViewHolder);

                                }
                            }
                        });
                        builder.show();
                    }

                    else if(userMessagesList.get(i).getType().equals("text")){
                        CharSequence options[] = new CharSequence[]{
                                "Delete for me",
                                "Cancel"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                if(position==0)
                                {
                                    deleteReceiveMessage(position,messageViewHolder);

                                }
                            }
                        });
                        builder.show();
                    }

                }
            });
        }
        // Add handling for other types of messages (images, etc.) if needed
    }


    public void addMessage(Messages message) {
        userMessagesList.add(message);
        notifyDataSetChanged(); // Notify adapter that the data has changed
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


    private void deleteSentMessage(final int position, final MessageViewHolder holder) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String fromUserId = userMessagesList.get(position).getFrom();
        String toUserId = userMessagesList.get(position).getTo();
        String messageId = userMessagesList.get(position).getMessageID();

        Log.d("DeleteMessage", "FromUserID: " + fromUserId);
        Log.d("DeleteMessage", "ToUserID: " + toUserId);
        Log.d("DeleteMessage", "MessageID: " + messageId);

        rootRef.child("Messages")
                .child(fromUserId)
                .child(toUserId)
                .child(messageId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(holder.itemView.getContext(), "Cannot Delete!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void deleteReceiveMessage(final int position, final MessageViewHolder holder)
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(holder.itemView.getContext(),"Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(holder.itemView.getContext(),"Cannot Delete!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void deleteMessageForEveryone(final int position, final MessageViewHolder holder)
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            rootRef.child("Messages")
                                    .child(userMessagesList.get(position).getFrom())
                                    .child(userMessagesList.get(position).getTo())
                                    .child(userMessagesList.get(position).getMessageID())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(holder.itemView.getContext(),"Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                        }else{
                            Toast.makeText(holder.itemView.getContext(),"Cannot Delete!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}