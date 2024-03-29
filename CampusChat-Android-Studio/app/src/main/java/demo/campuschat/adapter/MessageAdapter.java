package demo.campuschat.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import demo.campuschat.ConversationActivity;
import demo.campuschat.R;
import demo.campuschat.model.Message;

import java.io.IOException;
import java.util.List;
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Message> messageList;
    private final MessageLongClickListener longClickListener;
    private final MessageClickListener messageClickListener;

    // Define view types
    private static final int VIEW_TYPE_TEXT_SENT = 1;
    private static final int VIEW_TYPE_TEXT_RECEIVED = 2;
    private static final int VIEW_TYPE_IMAGE_SENT = 3;
    private static final int VIEW_TYPE_IMAGE_RECEIVED = 4;

     private static final int VIEW_TYPE_VIDEO_SENT = 5;
     private static final int VIEW_TYPE_VIDEO_RECEIVED = 6;
     private static final int VIEW_TYPE_GIF_SENT = 7;
     private static final int VIEW_TYPE_GIF_RECEIVED = 8;


    public MessageAdapter(List<Message> messageList, MessageLongClickListener messageLongClickListener, MessageClickListener messageClickListener) {
        this.messageList = messageList;
        this.longClickListener = messageLongClickListener;
        this.messageClickListener = messageClickListener;
    }



    @Override
    public int getItemViewType(int position) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Message message = messageList.get(position);

        boolean isSender = message.getSenderId().equals(currentUser.getUid());
        Message.MediaType mediaType = message.getMediaType(); // Get the mediaType

        // Check if mediaType is null and default to TEXT if so
        if (mediaType == null) {
            return isSender ? VIEW_TYPE_TEXT_SENT : VIEW_TYPE_TEXT_RECEIVED;
        }

        switch (mediaType) {
            case IMAGE:
                return isSender ? VIEW_TYPE_IMAGE_SENT : VIEW_TYPE_IMAGE_RECEIVED;
            case VIDEO:
                return isSender ? VIEW_TYPE_VIDEO_SENT : VIEW_TYPE_VIDEO_RECEIVED;
            case GIF:
                return isSender ? VIEW_TYPE_GIF_SENT : VIEW_TYPE_GIF_RECEIVED;
            default:
                return isSender ? VIEW_TYPE_TEXT_SENT : VIEW_TYPE_TEXT_RECEIVED;
        }

    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_TEXT_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_sent, parent, false);
                return new TextMessageViewHolder(view);
            case VIEW_TYPE_TEXT_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_received, parent, false);
                return new TextMessageViewHolder(view);
            case VIEW_TYPE_IMAGE_SENT:
            case VIEW_TYPE_GIF_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_image_sent, parent, false);
                return new ImageMessageViewHolder(view);
            case VIEW_TYPE_IMAGE_RECEIVED:
            case VIEW_TYPE_GIF_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_image_recieved, parent, false);
                return new ImageMessageViewHolder(view);
            case VIEW_TYPE_VIDEO_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_video_sent, parent, false);
                return new VideoMessageViewHolder(view);
            case VIEW_TYPE_VIDEO_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_video_recieved, parent, false);
                return new VideoMessageViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        Context context = holder.itemView.getContext();

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_TEXT_SENT:
            case VIEW_TYPE_TEXT_RECEIVED:
                bindTextMessageViewHolder((TextMessageViewHolder) holder, message);
                break;
            case VIEW_TYPE_IMAGE_SENT:
            case VIEW_TYPE_GIF_SENT:
            case VIEW_TYPE_IMAGE_RECEIVED:
            case VIEW_TYPE_GIF_RECEIVED:
                bindImageMessageViewHolder((ImageMessageViewHolder) holder, message, context);
                break;
            case VIEW_TYPE_VIDEO_SENT:
            case VIEW_TYPE_VIDEO_RECEIVED:
                bindVideoViewHolder((VideoMessageViewHolder) holder, message, context);
                break;
        }
    }

    private void bindTextMessageViewHolder(TextMessageViewHolder holder, Message message) {
        holder.messageTextView.setText(message.getMessageText());
        holder.timestampView.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", message.getTimestamp()));
        attachMessageLongClickListener(holder.itemView, message, holder.getAdapterPosition());    }

    private void bindImageMessageViewHolder(ImageMessageViewHolder holder, Message message, Context context) {
        // Use mediaURL for loading images into ImageView
        if (message.getMediaURL() != null && !message.getMediaURL().isEmpty()) {
            Glide
                    .with(context)
                    .load(message.getMediaURL())
                    .into(holder.messageImageView);
            holder.messageImageView.setOnClickListener(v -> {
                Uri mediaUri = Uri.parse(message.getMediaURL());
                if (messageClickListener != null) {
                    String mimeType;
                    if (message.getMediaType() == Message.MediaType.GIF) {
                        mimeType = "image/gif";
                    } else {
                        mimeType = "image/jpeg"; // Assuming JPEG for images, adjust if needed
                    }
                    messageClickListener.onImageClicked(mediaUri, mimeType);
                }
            });
        }
        // Attach long click listener using the refactored method
        attachMessageLongClickListener(holder.itemView, message, holder.getAdapterPosition());
    }

    /**
     * Sets a long click listener on the provided view to handle message-related actions.
     *
     * @param view     The view to attach the long click listener to.
     * @param message  The message associated with the view.
     * @param position The position of the message in the adapter.
     */
    private void attachMessageLongClickListener(View view, Message message, int position) {
        view.setOnLongClickListener(v -> {
            triggerLongClickAction(v, message, position);
            return true; // Indicates the callback consumed the long click
        });
    }

    /**
     * Triggers the defined long click action for a message.
     *
     * @param view     The view that was long-clicked.
     * @param message  The message associated with the long-clicked view.
     * @param position The position of the message in the adapter.
     */
    private void triggerLongClickAction(View view, Message message, int position) {
        if (longClickListener != null) {
            longClickListener.onMessageLongClicked(view, message, position);
        }
    }


    private void bindVideoViewHolder(VideoMessageViewHolder holder, Message message, Context context) {
        Uri videoUri = Uri.parse(message.getMediaURL()); // Assuming mediaURL is set to the video's URI


        // Use Glide or Picasso to load the thumbnail into the ImageView
        Glide.with(context)
                .load(message.getThumbnailURL()) // Assuming you have a method or field for the thumbnail URL
                .into(holder.videoThumbnail);

        // Attach long click listener using the refactored method
        attachMessageLongClickListener(holder.itemView, message, holder.getAdapterPosition());

        holder.playButton.setOnClickListener(v -> {
            if (messageClickListener != null) {
                try {
                    messageClickListener.onVideoClicked(videoUri);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        // Set any additional video-specific logic here
    }






    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder for text messages
    static class TextMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, timestampView;

        TextMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text_view);
            timestampView = itemView.findViewById(R.id.timestamp_view);
        }
    }

    // ViewHolder for image messages
    static class ImageMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView messageImageView;
//        TextView timestampView;

        ImageMessageViewHolder(View itemView) {
            super(itemView);
            messageImageView = itemView.findViewById(R.id.message_image_view);
//            timestampView = itemView.findViewById(R.id.timestamp_view);
        }

    }

    static class VideoMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        ImageButton playButton;

        public VideoMessageViewHolder(View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.message_video_view);
            playButton = itemView.findViewById(R.id.playButtonImageView);
//            videoView = itemView.findViewById(R.id.message_video_view); // other purpose lateR?
        }
    }

    // Future ViewHolder classes for Video and GIF


    public interface MessageClickListener {
        void onImageClicked(Uri imageUri, String mimeType);
        void onVideoClicked(Uri videoUri) throws IOException;
        // Define other click types as needed
    }

    public interface MessageLongClickListener {
        void onMessageLongClicked(View view, Message message, int position);
    }

}
