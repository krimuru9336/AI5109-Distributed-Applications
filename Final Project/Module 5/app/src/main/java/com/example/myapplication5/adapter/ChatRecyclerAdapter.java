package com.example.myapplication5.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication5.R;
import com.example.myapplication5.ChatMessageModel;
import com.example.myapplication5.utils.FirebaseUtil;

import java.util.List;



public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatModelViewHolder>  {
    Context context;
    private List<ChatMessageModel> chatMessageList;
    public String chatroomId;

    MediaPlayer mediaPlayer;

    public ChatRecyclerAdapter(List<ChatMessageModel> chatMessageList, Context context, String chatroomId) {
        this.chatMessageList = chatMessageList;
        this.context = context;
        this.chatroomId =  chatroomId;
    }



    @Override
    public void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position) {

//        chatMessageList.clear();

        ChatMessageModel model = chatMessageList.get(position);


//        System.out.println("in chat adapter:  " + chatMessageList);
        boolean isSenderCurrentUser = model.getSenderId().equals(FirebaseUtil.currentUserId());
        System.out.println("in chat adapter\t" + model.getSenderId() + "message model\t "+model.toMap());
        switch (model.getMessageType()) {
            case TEXT:
                handleTextMessage(holder, model, isSenderCurrentUser);
                break;
            case IMAGE:
                handleImageMessage(holder, model, isSenderCurrentUser);
                break;
            case VIDEO:
                handleVideoMessage(holder, model, isSenderCurrentUser);
                break;
            case GIF:
                handleGifMessage(holder, model, isSenderCurrentUser);
                break;
        }
    }

    private void handleTextMessage(ChatModelViewHolder holder, ChatMessageModel model, Boolean isSenderCurrentUser) {
        if (isSenderCurrentUser){  //fill right
            setRightLayout(holder, model);
            holder.rightChatTextview.setText(model.getMessage());

            holder.rightChatLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Show options for sender's messages (Edit, Delete)
                    showOptionsDialog(model);
                    return true;
                }
            });

        } else { // fill left
            setLeftLayout(holder, model);
            holder.leftChatTextview.setText(model.getMessage());
        }

    }


    private void handleGifMessage(ChatModelViewHolder holder, ChatMessageModel model, Boolean isSenderCurrentUser) {
        if (isSenderCurrentUser){  //fill right
            setRightLayout(holder, model);
            Glide.with(context).asGif().load(model.getMediaUrl()).into(holder.gifImageView);
            holder.gifImageView.setVisibility(View.VISIBLE);
        } else { // fill left
            setLeftLayout(holder, model);
            Glide.with(context).asGif().load(model.getMediaUrl()).into(holder.leftGifImageView);  //load gif
            holder.leftGifImageView.setVisibility(View.VISIBLE);   //turn ON GIF viewer
            }

    }

//    private void handleVideoMessage(ChatModelViewHolder holder, ChatMessageModel model, Boolean isSenderCurrentUser) {
//    }

    //new for video-
    private void handleVideoMessage(ChatModelViewHolder holder, ChatMessageModel model, Boolean isSenderCurrentUser) {
        if (isSenderCurrentUser) {
            setRightLayout(holder, model);
            holder.videoView.setVisibility(View.VISIBLE);
            holder.videoView.setVideoURI(Uri.parse(model.getMediaUrl()));
            holder.videoView.start();

//            holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    // Store the MediaPlayer instance for later use
//                     mediaPlayer = mp;
//                }
//            });
//
//            holder.videoView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent event) {
//                    if (mediaPlayer != null) {
//                        if (mediaPlayer.isPlaying()) {
//                            mediaPlayer.pause();
//                        } else {
//                            mediaPlayer.start();
//                        }
//                    }
//                    return true;
//                }
//            });
//            holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    mp.setLooping(true);
//                }
//            });
        } else {
            setLeftLayout(holder, model);
            holder.leftVideoView.setVisibility(View.VISIBLE);
            holder.leftVideoView.setVideoURI(Uri.parse(model.getMediaUrl()));
            holder.leftVideoView.start();
            holder.leftVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
        }
    }



    private void handleImageMessage(ChatModelViewHolder holder, ChatMessageModel model, Boolean isSenderCurrentUser) {
        if (isSenderCurrentUser){  //fill right
            setRightLayout(holder, model);
            Glide.with(context)
                    .load(model.getMediaUrl())
                    .into(holder.imageView);
            holder.imageView.setVisibility(View.VISIBLE);
        } else { // fill left
            setLeftLayout(holder, model);
            Glide.with(context).load(model.getMediaUrl()).into(holder.leftImageView);  //load img
            holder.leftImageView.setVisibility(View.VISIBLE);
        }

    }

    private static void setRightLayout(@NonNull ChatModelViewHolder holder, ChatMessageModel model) {
        //left view OFF, right view ON, timestamp set
        holder.leftChatLayout.setVisibility(View.GONE);
        holder.rightChatLayout.setVisibility(View.VISIBLE);
        holder.rightTimestamp.setText(getTimestamp(model));
    }

    private static void setLeftLayout(ChatModelViewHolder holder, ChatMessageModel model) {
        //right view OFF, left view ON, timestamp set
        holder.rightChatLayout.setVisibility(View.GONE);  //make right side layout gone
        holder.leftChatLayout.setVisibility(View.VISIBLE); // left ON
        holder.leftTimestamp.setText(getTimestamp(model));  // turn on timestamp
    }
    @NonNull
    private static String getTimestamp(ChatMessageModel model) {
        return FirebaseUtil.timestampToString(model.getTimestamp());
    }





    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    public void addData(ChatMessageModel newMessage) {
        chatMessageList.add(newMessage);
        notifyItemInserted(chatMessageList.size() - 1); // Notify the adapter about the new item
    }

    public void clearData() {
        if (chatMessageList != null) {    //think to clear gifs also here.
            chatMessageList.clear();
            notifyDataSetChanged();
        }
    }

    //edit del
    public void showOptionsDialog(ChatMessageModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Message Options");

        // Edit option
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showEditDialog(model);
            }
        });

        // Delete option
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDeleteDialog(model);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showEditDialog(ChatMessageModel model) {
        // Implement a dialog for editing messages
        // You can use an AlertDialog or a custom dialog for this
        // On editing, update the message in the list and notify the adapter
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Message");

        // Create an EditText to allow the user to edit the message
        final EditText input = new EditText(context);
        input.setText(model.getMessage());
        builder.setView(input);

        // Set up the buttons for positive (update) and negative (cancel) actions
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the updated message from the EditText
                String updatedMessage = input.getText().toString().trim();

//                String room = "chatrooms";

                // Update the message in the list
                model.setMessage(updatedMessage);
                for (ChatMessageModel chatMessage : chatMessageList) {
                    if (chatMessage.getTimestamp().equals(model.getTimestamp())) {
                        chatMessage.setMessage(updatedMessage);
                        // Update the message in the Firebase database using your logic
                        FirebaseUtil.updateChatMessage(chatroomId, chatMessage);
                        break;
                    }
                }

                // Notify the adapter about the change
                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDeleteDialog(ChatMessageModel model) {
        // Implement a dialog for deleting messages
        // You can use an AlertDialog or a custom dialog for this
        // On deletion, remove the message from the list and notify the adapter
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Message");
        builder.setMessage("Are you sure you want to delete this message?");

        // Set up the buttons for positive (delete) and negative (cancel) actions
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove the message from the list

                for (ChatMessageModel chatMessage : chatMessageList) {
                    if (chatMessage.getTimestamp().equals(model.getTimestamp())) {
                        chatMessageList.remove(model);
//                        chatMessage.setMessage(updatedMessage);
                        // Update the message in the Firebase database using your logic
                        System.out.println("delete method call");
                        FirebaseUtil.deleteChatMessage(chatroomId, chatMessage);
                        break;
                    }
                }

                // Notify the adapter about the removal
                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //edit del

    class ChatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextview,rightChatTextview, leftTimestamp, rightTimestamp;

        //new additions for media types
        ImageView imageView, leftImageView;
        VideoView videoView, leftVideoView;
        ImageView gifImageView, leftGifImageView;

        private MediaPlayer mediaPlayer;


        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);

            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
//            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);

            leftTimestamp = itemView.findViewById(R.id.left_chat_timestamp);
            rightTimestamp = itemView.findViewById(R.id.right_chat_timestamp);

            //right side chat views
            imageView = itemView.findViewById(R.id.right_chat_imageView);
            videoView = itemView.findViewById(R.id.right_chat_videoView);



            gifImageView = itemView.findViewById(R.id.right_chat_gifImageView);

            //left side views

            leftImageView = itemView.findViewById(R.id.left_chat_imageView);
            leftVideoView = itemView.findViewById(R.id.left_chat_videoView);
            leftGifImageView = itemView.findViewById(R.id.left_chat_gifImageView);
        }
    }
}


