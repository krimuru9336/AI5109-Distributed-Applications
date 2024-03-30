package com.example.chatstnr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatstnr.R;
import com.example.chatstnr.models.ChatMessageModel;
import com.example.chatstnr.models.UserModel;
import com.example.chatstnr.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Objects;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;
    //    private OnItemClickListener OnItemClickListener;
    private OnEditDeleteClickListener editDeleteClickListener;
    private int selectedItemPosition = RecyclerView.NO_POSITION;
    private static UserModel sender;
    private static final int MESSAGE_TYPE_NORMAL = 0;
    private static final int MESSAGE_TYPE_DELETED = 1;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    // Interface for item click handling
//    public interface OnItemClickListener {
//        void OnItemClick(ChatMessageModel chatMessage);
//    }
    // Setter method for setting the listener
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.OnItemClickListener = listener;
//    }

    // Interface for item click handling
    public interface OnEditDeleteClickListener {
        void OnEditDeleteClick(ChatMessageModel chatMessage);
    }

    // Setter method for setting the listener
    public void setOnEditDeleteClickListener(OnEditDeleteClickListener listener) {
        this.editDeleteClickListener = listener;
    }


    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        Log.i("haushd", "asjd");

        if (model.isDeleted()) {
            holder.bindDeletedMessage(model);
        } else {
            holder.bindNormalMessage(model);
        }
//        holder.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void OnItemClick(ChatMessageModel chatMessage) {
//                // Handle item click
//                if (OnItemClickListener != null) {
//
////                    viewHolder.rightChatLayout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.chat_color_editing)));
//                    OnItemClickListener.OnItemClick(chatMessage);
//                }
//            }
//        });

    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MESSAGE_TYPE_DELETED) {
            view = LayoutInflater.from(context).inflate(R.layout.deletet_message_recycler_row, parent, false);

        } else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        }

        ChatModelViewHolder viewHolder = new ChatModelViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageModel model = getItem(position);
        return model.isDeleted() ? MESSAGE_TYPE_DELETED : MESSAGE_TYPE_NORMAL;
    }

    public void setSelectedItemPosition(int position) {
        int previousSelectedPosition = selectedItemPosition;
        selectedItemPosition = position;
        notifyItemChanged(previousSelectedPosition);
        notifyItemChanged(selectedItemPosition);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftChatLayout, rightChatLayout, deletedLeftChatLayout, deletedRightChatLayout;
        TextView leftChatTextview, rightChatTextview, leftChatTimeview, rightChatTimeview, deletedLeftChatTextview, deletedRightChatTextview, leftUsername, leftPno;
        ImageButton editDeleteBtn;
        ImageView leftChatImageview, rightChatImageview;
        VideoView leftChatVideoview, rightChatVideoview;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            leftChatTimeview = itemView.findViewById(R.id.left_chat_timeview);
            rightChatTimeview = itemView.findViewById(R.id.right_chat_timeview);
            leftChatImageview = itemView.findViewById(R.id.left_chat_imageview);
            rightChatImageview = itemView.findViewById(R.id.right_chat_imageview);
            leftChatVideoview = itemView.findViewById(R.id.left_chat_videoview);
            rightChatVideoview = itemView.findViewById(R.id.right_chat_videoview);

            deletedLeftChatLayout = itemView.findViewById(R.id.deleted_left_chat_layout);
            deletedRightChatLayout = itemView.findViewById(R.id.deleted_right_chat_layout);
            deletedLeftChatTextview = itemView.findViewById(R.id.deleted_left_chat_textview);
            deletedRightChatTextview = itemView.findViewById(R.id.deleted_right_chat_textview);

            editDeleteBtn = itemView.findViewById(R.id.edit_delete_btn);

            leftUsername = itemView.findViewById(R.id.left_chat_username);
            leftPno = itemView.findViewById(R.id.left_chat_pno);

            if (editDeleteBtn != null) {
                editDeleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            // Call the appropriate method in the interface
                            ChatMessageModel chatMessage = getItem(position);

                            if (editDeleteClickListener != null) {
                                editDeleteClickListener.OnEditDeleteClick(chatMessage);
                            }
                        }
                    }
                });

            }
            // Set click listener for editDeleteBtn
        }

        public void bindNormalMessage(ChatMessageModel model) {
            if (model.getSenderId().equals(FirebaseUtil.currentUserid())) {
                leftChatLayout.setVisibility(View.GONE);
                rightChatLayout.setVisibility(View.VISIBLE);
                if (Objects.equals(model.getMessageType(), "text")) {
                    rightChatTextview.setText(model.getMessage());
                    rightChatTextview.setVisibility(View.VISIBLE);
                    rightChatVideoview.setVisibility(View.GONE);
                    rightChatImageview.setVisibility(View.GONE);
                } else if (Objects.equals(model.getMessageType(), "image")) {
                    Glide.with(context).load(model.getMessageUrl()).into(rightChatImageview);
                    rightChatVideoview.setVisibility(View.GONE);
                    rightChatImageview.setVisibility(View.VISIBLE);
                    rightChatTextview.setVisibility(View.GONE);
                } else if (Objects.equals(model.getMessageType(), "video")) {
                    rightChatVideoview.setVideoURI(Uri.parse(model.getMessageUrl()));
                    rightChatImageview.setImageResource(R.drawable.play_arrow_icon);
                    rightChatVideoview.setVisibility(View.VISIBLE);
                    rightChatImageview.setVisibility(View.GONE);
                    rightChatTextview.setVisibility(View.GONE);
                    rightChatTextview.setText(model.getMessage());

//                    rightChatVideoview.start();

                    rightChatImageview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rightChatVideoview.setVisibility(View.VISIBLE);
                            rightChatImageview.setVisibility(View.GONE);
                            rightChatVideoview.start();

                        }
                    });
                    rightChatVideoview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (rightChatVideoview.isPlaying()) {
                                // If the video is currently playing, pause it
                                rightChatVideoview.pause();
                            } else {
                                // If the video is paused or stopped, start playing it
                                rightChatVideoview.start();
                            }
                        }
                    });


                }
                rightChatTimeview.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
            } else {

                FirebaseUtil.getUserDetails(model.getSenderId()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sender = task.getResult().toObject(UserModel.class);
                        leftUsername.setText(sender.getUsername());
                        leftPno.setText(sender.getPhone());

                    }
                });

                rightChatLayout.setVisibility(View.GONE);
                leftChatLayout.setVisibility(View.VISIBLE);
                if (Objects.equals(model.getMessageType(), "text")) {
                    leftChatTextview.setText(model.getMessage());
                    leftChatTextview.setVisibility(View.VISIBLE);
                    leftChatVideoview.setVisibility(View.GONE);
                    leftChatImageview.setVisibility(View.GONE);
                } else if (Objects.equals(model.getMessageType(), "image")) {
                    Glide.with(context).load(model.getMessageUrl()).into(leftChatImageview);
                    leftChatVideoview.setVisibility(View.GONE);
                    leftChatImageview.setVisibility(View.VISIBLE);
                    leftChatTextview.setVisibility(View.GONE);
                } else if (Objects.equals(model.getMessageType(), "video")) {
                    leftChatVideoview.setVideoURI(Uri.parse(model.getMessageUrl()));
                    leftChatVideoview.setVisibility(View.VISIBLE);
                    leftChatImageview.setVisibility(View.GONE);
                    leftChatTextview.setVisibility(View.GONE);
                    leftChatTextview.setText(model.getMessage());
                }

                leftChatImageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leftChatVideoview.setVisibility(View.VISIBLE);
                        leftChatImageview.setVisibility(View.GONE);
                        leftChatVideoview.start();

                    }
                });
                leftChatVideoview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (leftChatVideoview.isPlaying()) {
                            // If the video is currently playing, pause it
                            leftChatVideoview.pause();
                        } else {
                            // If the video is paused or stopped, start playing it
                            leftChatVideoview.start();
                        }
                    }
                });


                leftChatTimeview.setText(FirebaseUtil.timestampToString(model.getTimestamp()));
            }
        }

        public void bindDeletedMessage(ChatMessageModel model) {
            if (model.getSenderId().equals(FirebaseUtil.currentUserid())) {
                deletedLeftChatLayout.setVisibility(View.GONE);
                deletedRightChatLayout.setVisibility(View.VISIBLE);
                deletedRightChatTextview.setText("This message has been deleted");
            } else {
                deletedLeftChatLayout.setVisibility(View.VISIBLE);
                deletedRightChatLayout.setVisibility(View.GONE);
                deletedLeftChatTextview.setText("This message has been deleted");
            }

            // You can set any specific properties for deleted messages here
            Log.d("ChatAdapter", "Binding deleted message view holder");
        }

        public void highlightSelectedMessage(ChatMessageModel model) {
            // Change the background color of the clicked item
//            if (model.getSenderId().equals(FirebaseUtil.currentUserid())) {

            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.chat_color_editing));

            // Post a delayed runnable to remove the highlight after a short duration
//                itemView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
            // Remove the background color after a short delay (e.g., 1 second)
//                        itemView.setBackgroundColor(Color.TRANSPARENT); // Change to the default color or set to transparent
//                    }
//                }, 1000); // Adjust the delay duration as needed (e.g., 1000 milliseconds = 1 second)

            //          }
//            else{
//                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.chat_color_editing));

            // Post a delayed runnable to remove the highlight after a short duration
//                itemView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
            // Remove the background color after a short delay (e.g., 1 second)
//                        itemView.setBackgroundColor(Color.TRANSPARENT); // Change to the default color or set to transparent
            //                   }
            //               }, 1000); // Adjust the delay duration as needed (e.g., 1000 milliseconds = 1 second)

            //          }
        }

        public void removehighlightSelectedMessage() {

            itemView.setBackgroundColor(Color.TRANSPARENT); // Change to the default color or set to transparent
        }

        // Method to set the click listener for each ViewHolder
//        public void setOnItemClickListener(OnItemClickListener listener) {
//            itemView.setOnItemClickListener(new OnItemClickListener() {
//                @Override
//                public void onItemClick(View v) {
//                    setSelectedItemPosition(getAdapterPosition());
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        listener.onItemClick(getItem(position));
//                    }
//                }
//            });
//        }

    }
}
