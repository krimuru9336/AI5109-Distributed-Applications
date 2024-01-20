package com.example.distributedapplicationsproject.android;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.distributedapplicationsproject.R;
import com.example.distributedapplicationsproject.android.dialogs.MessageEditDialog;
import com.example.distributedapplicationsproject.android.listeners.MediaDownloadListener;
import com.example.distributedapplicationsproject.firebase.DatabaseService;
import com.example.distributedapplicationsproject.firebase.StorageService;
import com.example.distributedapplicationsproject.models.Message;
import com.example.distributedapplicationsproject.utils.DataShare;
import com.example.distributedapplicationsproject.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.ViewHolder> {

    DatabaseService databaseService = DatabaseService.getInstance();
    StorageService storageService = StorageService.getInstance();

    List<Message> messageList;
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;
    FragmentManager fragmentManager;

    public MessageRecyclerAdapter(List<Message> messageList, ProgressBar progressBar, NestedScrollView nestedScrollView, FragmentManager fragmentManager) {
        this.messageList = messageList;
        this.progressBar = progressBar;
        this.nestedScrollView = nestedScrollView;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @NotNull
    @Override
    public MessageRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_recycler_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageRecyclerAdapter.ViewHolder viewModel, int i) {
        Message msg = messageList.get(i);

        // If we are the sender of the message
        if (msg.getSenderId().equals(DataShare.getInstance().getCurrentUser().getId())) {
            viewModel.layoutMember.setVisibility(View.GONE);
            viewModel.textSenderMsg.setVisibility(View.VISIBLE);
            viewModel.textSender.setText("(You)");

            if (msg.isDeleted()) {
                // Delete media from cache if detected as deleted
                DataShare.getInstance().getMediaCachingService().removeMedia(msg.getMediaUrl());

                viewModel.textSenderMsg.setTypeface(viewModel.textSenderMsg.getTypeface(), Typeface.ITALIC);
                viewModel.textSenderMsg.setTextColor(viewModel.itemView.getContext().getColor(R.color.secondary_text));
                viewModel.textSenderMsg.setText(viewModel.itemView.getContext().getString(R.string.deleted_msg));
                viewModel.textSenderMsg.setVisibility(View.VISIBLE);
                viewModel.imgSenderMedia.setImageURI(null);
                viewModel.imgSenderMedia.setVisibility(View.GONE);
                viewModel.videoSenderMedia.setVideoURI(null);
                viewModel.layoutSenderVideoMedia.setVisibility(View.GONE);
                viewModel.textSenderTime.setVisibility(View.GONE);
                viewModel.textSenderEdited.setVisibility(View.GONE);
            } else {
                viewModel.textSenderMsg.setText(msg.getMessage());
                viewModel.textSenderTime.setText(Utils.parseDataToMessageTime(Utils.parseTime(msg.getLastEdited())));

                // If Media available enable view
                String mediaUrl = msg.getMediaUrl();
                if (mediaUrl != null) {
                    storageService.downloadMedia(mediaUrl, new MediaDownloadListener(msg, viewModel.itemView, viewModel.imgSenderMedia, viewModel.layoutSenderVideoMedia, viewModel.videoSenderMedia, viewModel.imgSenderPlayVideo));
                    // If media available and text empty -> dont render
                    if (msg.getMessage().isEmpty()) {
                        viewModel.textSenderMsg.setVisibility(View.GONE);
                    }
                }

                if (msg.isEdited()) {
                    viewModel.textSenderEdited.setVisibility(View.VISIBLE);
                    viewModel.textSenderMsg.setTypeface(viewModel.textSenderMsg.getTypeface(), Typeface.ITALIC);
                }
            }

        } else {
            viewModel.layoutSender.setVisibility(View.GONE);
            viewModel.textMemberMsg.setVisibility(View.VISIBLE);
            viewModel.textMember.setText(DataShare.getInstance().getUserList().stream().filter(user -> user.getId().equals(msg.getSenderId())).collect(Collectors.toList()).get(0).getName());

            if (msg.isDeleted()) {
                // Delete media from cache if detected as deleted
                DataShare.getInstance().getMediaCachingService().removeMedia(msg.getMediaUrl());

                viewModel.textMemberMsg.setTypeface(viewModel.textMemberMsg.getTypeface(), Typeface.ITALIC);
                viewModel.textMemberMsg.setTextColor(viewModel.itemView.getContext().getColor(R.color.secondary_text));
                viewModel.textMemberMsg.setText(viewModel.itemView.getContext().getString(R.string.deleted_msg));
                viewModel.textMemberMsg.setVisibility(View.VISIBLE);
                viewModel.imgMemberMedia.setImageURI(null);
                viewModel.imgMemberMedia.setVisibility(View.GONE);
                viewModel.videoMemberMedia.setVideoURI(null);
                viewModel.layoutMemberVideoMedia.setVisibility(View.GONE);
                viewModel.textMemberTime.setVisibility(View.GONE);
                viewModel.textMemberEdited.setVisibility(View.GONE);
            } else {
                viewModel.textMemberMsg.setText(msg.getMessage());
                viewModel.textMemberTime.setText(Utils.parseDataToMessageTime(Utils.parseTime(msg.getLastEdited())));

                // If Media available enable view
                String mediaUrl = msg.getMediaUrl();
                if (mediaUrl != null) {
                    storageService.downloadMedia(mediaUrl, new MediaDownloadListener(msg, viewModel.itemView, viewModel.imgMemberMedia, viewModel.layoutMemberVideoMedia, viewModel.videoMemberMedia, viewModel.imgMemberPlayVideo));
                    // If media available and text empty -> dont render
                    if (msg.getMessage().isEmpty()) {
                        viewModel.textMemberMsg.setVisibility(View.GONE);
                    }
                }

                if (msg.isEdited()) {
                    viewModel.textMemberEdited.setVisibility(View.VISIBLE);
                    viewModel.textMemberMsg.setTypeface(viewModel.textMemberMsg.getTypeface(), Typeface.ITALIC);
                }
            }

        }
        viewModel.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Only delete if your message and not already deleted
                if (msg.getSenderId().equals(DataShare.getInstance().getCurrentUser().getId()) & !msg.isDeleted()) {
                    MessageEditDialog dialog = new MessageEditDialog(msg, new MessageEditDialog.OnMessageEditDialogListener() {
                        @Override
                        public void onMessageEditDialogCancel() {
                            // Don't do anything
                        }

                        @Override
                        public void onMessageEditDialogDelete() {
                            databaseService.deleteMessage(DataShare.getInstance().getCurrentChatInfo(), msg);
                        }

                        @Override
                        public void onMessageEditDialogEdit(Message editedMessage) {
                            databaseService.editMessage(DataShare.getInstance().getCurrentChatInfo(), editedMessage);
                        }
                    });
                    dialog.show(fragmentManager, "DIALOG_MESSAGE_EDIT");
                }

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public void onViewAttachedToWindow(@NotNull ViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);

        if(progressBar.getVisibility() != View.GONE & viewHolder.getAdapterPosition() == messageList.size() - 1) {
            progressBar.setVisibility(View.GONE);
        }

        nestedScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.scrollBy(0, nestedScrollView.getChildAt(0).getHeight());
            }
        }, 200);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutMember;
        TextView textMember;
        TextView textMemberMsg;
        TextView textMemberTime;
        TextView textMemberEdited;
        ImageView imgMemberMedia;
        RelativeLayout layoutMemberVideoMedia;
        VideoView videoMemberMedia;
        ImageView imgMemberPlayVideo;

        LinearLayout layoutSender;
        TextView textSender;
        TextView textSenderMsg;
        TextView textSenderTime;
        TextView textSenderEdited;
        ImageView imgSenderMedia;
        RelativeLayout layoutSenderVideoMedia;
        VideoView videoSenderMedia;
        ImageView imgSenderPlayVideo;


        public ViewHolder(View view) {
            super(view);

            this.layoutMember = view.findViewById(R.id.layout_member);
            this.textMember = view.findViewById(R.id.text_member);
            this.textMemberMsg = view.findViewById(R.id.text_member_msg);
            this.textMemberTime = view.findViewById(R.id.text_member_time);
            this.textMemberEdited = view.findViewById(R.id.text_member_edited);
            this.imgMemberMedia = view.findViewById(R.id.img_member_media);
            this.layoutMemberVideoMedia = view.findViewById(R.id.layout_member_video_media);
            this.videoMemberMedia = view.findViewById(R.id.video_member_media);
            this.imgMemberPlayVideo = view.findViewById(R.id.img_member_play_video);

            this.layoutSender = view.findViewById(R.id.layout_sender);
            this.textSender = view.findViewById(R.id.text_sender);
            this.textSenderMsg = view.findViewById(R.id.text_sender_msg);
            this.textSenderTime = view.findViewById(R.id.text_sender_time);
            this.textSenderEdited = view.findViewById(R.id.text_sender_edited);
            this.imgSenderMedia = view.findViewById(R.id.img_sender_media);
            this.layoutSenderVideoMedia = view.findViewById(R.id.layout_sender_video_media);
            this.videoSenderMedia = view.findViewById(R.id.video_sender_media);
            this.imgSenderPlayVideo = view.findViewById(R.id.img_sender_play_video);
        }

    }
}
