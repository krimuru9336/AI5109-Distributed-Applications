// Sven Schickentanz - fdai7287
package com.da.chitchat.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.da.chitchat.Message;
import com.da.chitchat.R;
import com.da.chitchat.UserMessageStore;
import com.da.chitchat.activities.MessageActivity;
import com.da.chitchat.interfaces.OnDataChangedListener;
import com.da.chitchat.services.MediaConverter;
import com.da.chitchat.singletons.AppContextSingleton;
import com.da.chitchat.singletons.MediaConverterSingleton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * The `MessageAdapter` class is responsible for managing and displaying a list of messages in a RecyclerView.
 * It extends the RecyclerView.Adapter class and uses a custom ViewHolder, MessageViewHolder, to bind the data to the views.
 * The adapter also provides methods for adding, editing, and deleting messages, as well as handling user interactions.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private static List<Message> messageList = null;
    private final String username;
    private final OnDataChangedListener onDataChangedListener;
    private int position;
    private final MediaConverter mc;
    private final Context ctx;

    /**
     * Constructs a MessageAdapter with the given list of messages, username, listener, and context.
     *
     * @param messageList The list of messages to be displayed.
     * @param username The username of the current user.
     * @param listener The listener for data changes.
     * @param ctx The context of the activity.
     */
    public MessageAdapter(List<Message> messageList, String username, OnDataChangedListener listener,
                          MessageActivity ctx) {
        this.onDataChangedListener = listener;

        MessageAdapter.messageList = messageList;
        this.username = username;
        mc = MediaConverterSingleton.getInstance();
        this.ctx = ctx;

        loadAllUserMessages();
    }

    /**
     * Creates a new view holder for the message item view.
     *
     * @param parent The parent view group.
     * @param viewType The type of view.
     */
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);

        return new MessageViewHolder(view);
    }

    /**
     * Binds the message to the view holder at the given position.
     *
     * @param holder The view holder to bind the message to.
     * @param position The position of the message in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.resetState();

        Message message = messageList.get(position);
        holder.bind(message, mc, ctx);
        // Scrolls down to the last message
        updateScrollPosition();

        // Set the position of the message in the list
        holder.itemView.setOnLongClickListener(v -> {
            setPosition(holder.getBindingAdapterPosition());
            return false;
        });
    }

    /**
     * Called when a view holder is recycled.
     *
     * @param holder The view holder to be recycled.
     */
    @Override
    public void onViewRecycled(@NonNull MessageViewHolder holder) {
        super.onViewRecycled(holder);
        holder.unbind();
    }

    /**
     * Returns the number of messages in the list.
     *
     * @return The number of messages in the list.
     */
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    /**
     * Returns the current user's username.
     *
     * @return The current user's username.
     */
    public String currentUser() {
        return username;
    }

    /**
     * Returns the message at the given position in the list.
     *
     * @param position The position of the message in the list.
     * @return The message at the given position.
     */
    public Message getItem(int position) {
        if (position >= 0 && position < messageList.size()) {
            return messageList.get(position);
        }
        return null;
    }

    /**
     * Returns the position of the message in the list.
     *
     * @return The position of the message in the list.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the position of the message in the list.
     *
     * @param position The position of the message in the list.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Loads all messages for the current chat partner.
     */
    private void loadAllUserMessages() {
        if (messageList != null && messageList.size() > 0) {
            notifyItemRangeInserted(0, messageList.size() - 1);
        }
    }

    /**
     * Adds a new message to the list.
     * 
     * @param message The message to be added.
     * @param isGroup A boolean indicating whether the message is a group message.
     */
    public UUID addMessage(Message message, boolean isGroup) {
        if (isGroup) {
            UserMessageStore.addMessageToGroup(username, message);
        } else {
            UserMessageStore.addMessageToUser(username, message);
        }
        showNewMessage();
        return message.getID();
    }

    /**
     * Adds and updates the timestamp of a message in the list.
     * Includes normal timestamp and edit timestamp.
     * 
     * @param id The ID of the message.
     * @param timestamp The timestamp to be added.
     * @param isEditTimestamp A boolean indicating whether the timestamp is an edit timestamp.
     */
    public void addTimestamp(UUID id, long timestamp, boolean isEditTimestamp) {
        Message foundMessage = findMessageById(id);
        if (foundMessage != null) {
            if (isEditTimestamp) {
                foundMessage.setEditTimestamp(new Date(timestamp));
            } else {
                foundMessage.setTimestamp(timestamp);
            }
            notifyItemChanged(messageList.indexOf(foundMessage));
        }
    }

    /**
     * Adds media to a message in the list.
     * 
     * @param id The ID of the message.
     * @param mediaUri The URI of the media to be added.
     * @param isVideo A boolean indicating whether the media is a video.
     */
    public void addMedia(UUID id, Uri mediaUri, boolean isVideo) {
        Message foundMessage = findMessageById(id);
        if (foundMessage != null) {
            foundMessage.setMediaUri(mediaUri);
            foundMessage.setIsVideo(isVideo);
            notifyItemChanged(messageList.indexOf(foundMessage));
        }
    }

    /**
     * Edits a message in the list.
     * 
     * @param id The ID of the message.
     * @param userInput The user input to be edited.
     * @param editDate The date of the edit.
     */
    public void editMessage(UUID id, String userInput, Date editDate) {
        Message foundMessage = findMessageById(id);
        if (foundMessage != null) {
            foundMessage.setEditTimestamp(editDate);
            editMessage(foundMessage, userInput);
        }
    }

    /**
     * Edits a message in the list.
     * 
     * @param message The message to be edited.
     * @param userInput The user input to be edited.
     */
    public void editMessage(Message message, String userInput) {
        if (message.getState() != Message.State.DELETED) {
            message.setText(userInput);
            message.setState(Message.State.EDITED);
            notifyItemChanged(messageList.indexOf(message));
        }
    }

    /**
     * Deletes a message in the list.
     * 
     * @param id The ID of the message.
     */
    public void deleteMessage(UUID id) {
        Message foundMessage = findMessageById(id);
        if (foundMessage != null) {
            deleteMessage(foundMessage);
        }
    }

    /**
     * Deletes a message in the list.
     * 
     * @param message The message to be deleted.
     */
    public void deleteMessage(Message message) {
        message.setEditTimestamp(null);
        String deleteMessageText = AppContextSingleton.getInstance().getString(R.string.deleteMessageText);
        message.setText(deleteMessageText);
        message.setState(Message.State.DELETED);
        notifyItemChanged(messageList.indexOf(message));
    }

    /**
     * Finds a message by its ID.
     * 
     * @param messageId The ID of the message.
     * @return The message with the given ID.
     */
    private Message findMessageById(UUID messageId) {
        for (Message message : messageList) {
            if (message.getID().equals(messageId)) {
                return message;
            }
        }
        return null;
    }

    /**
     * Shows a new message in the list and updates the scroll position.
     */
    public void showNewMessage() {
        notifyItemInserted(messageList.size() - 1);
        updateScrollPosition();
    }

    /**
     * Updates the scroll position of the RecyclerView.
     */
    private void updateScrollPosition() {
        if (onDataChangedListener != null) {
            onDataChangedListener.onDataChanged();
        }
    }

    /**
     * The `MessageViewHolder` class is responsible for managing the views of a message item in the RecyclerView.
     * It extends the RecyclerView.ViewHolder class and provides methods for binding the message data to the views.
     */
    static class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        // Create a SimpleDateFormat to format the timestamp
        SimpleDateFormat dateFormat;
        private final TextView messageTextView;
        private final TextView usernameTextView;
        private final TextView timestampTextView;
        private final TextView editTimestampTextView;
        // Create view for displaying images and gifs
        private final ImageView messageImageView;
        // Create view for displaying videos
        private final PlayerView messageVideoView;
        private final LinearLayout backgroundContainer;
        private final LinearLayout messageContainer;
        private final LinearLayout editInfoContainer;
        private final int standardTextColor;
        // Create an ExoPlayer for playing video messages
        private ExoPlayer exoPlayer;

        /**
         * Constructs a MessageViewHolder with the given item view.
         *
         * @param itemView The item view for the message.
         */
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
            messageTextView = itemView.findViewById(R.id.messageTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            backgroundContainer = itemView.findViewById(R.id.messageBackgroundContainer);
            messageContainer = itemView.findViewById(R.id.messageContainer);
            editInfoContainer = itemView.findViewById(R.id.editInfoContainer);
            editTimestampTextView = itemView.findViewById(R.id.editTimestampTextView);
            messageImageView = itemView.findViewById(R.id.messageImageView);
            messageVideoView = itemView.findViewById(R.id.messageVideoView);
            standardTextColor = messageTextView.getCurrentTextColor();

            itemView.setOnCreateContextMenuListener(this);
        }

        /**
         * Binds the message data to the views.
         *
         * @param message The message to be displayed.
         * @param mc The media converter for displaying media.
         * @param ctx The context of the activity.
         */
        public void bind(Message message, MediaConverter mc, Context ctx) {
            String username = message.isIncoming() ? message.getSender() : "You";
            Uri mediaUri = message.getMediaUri();
            usernameTextView.setText(username);

            ViewGroup.LayoutParams layoutParams = usernameTextView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            usernameTextView.setLayoutParams(layoutParams);

            // Check if the message contains media and display it accordingly
            // Remove media if the message is deleted or edited
            if (mediaUri != null && message.getState() == Message.State.UNMODIFIED) {
                // Hide the message text view if the message contains media
                messageTextView.setVisibility(View.GONE);
                if (message.isVideo()) {
                    if (exoPlayer == null) {
                        // Create a new ExoPlayer for playing video messages
                        exoPlayer = new ExoPlayer.Builder(ctx).build();
                        messageVideoView.setPlayer(exoPlayer);
                    }
                    // Hide ImageView and display VideoView
                    messageImageView.setVisibility(View.GONE);
                    messageVideoView.setVisibility(View.VISIBLE);
                    mc.showVideo(mediaUri, exoPlayer);
                } else {
                    // Hide VideoView and display ImageView
                    messageImageView.setVisibility(View.VISIBLE);
                    messageVideoView.setVisibility(View.GONE);
                    mc.showImage(ctx, mediaUri, messageImageView);
                }
            } else {
                // Set the message text to the message text view
                messageTextView.setText(message.getText());
                // Show the message text view and hide the media views
                messageImageView.setVisibility(View.GONE);
                messageVideoView.setVisibility(View.GONE);
                messageTextView.setVisibility(View.VISIBLE);
            }

            // Format the timestamp and set it to the timestampTextView
            timestampTextView.setText(dateFormat.format(message.getTimestamp()));

            // Set background based on message type (incoming or outgoing)
            int backgroundResource = message.isIncoming() ?
                    R.drawable.incoming_message_background : R.drawable.outgoing_message_background;

            backgroundContainer.setBackgroundResource(backgroundResource);

            // Set gravity based on message type (incoming or outgoing)
            // Incoming messages are aligned to the start, outgoing messages are aligned to the end
            int curGrav = message.isIncoming() ? Gravity.START : Gravity.END;
            messageContainer.setGravity(curGrav);

            // Set text color based on message state (deleted or edited)
            if (message.getState() == Message.State.DELETED) {
                int textColor = ContextCompat.getColor(itemView.getContext(), R.color.grey);
                messageTextView.setTextColor(textColor);
            } else if (message.getState() == Message.State.EDITED) {
                editInfoContainer.setVisibility(View.VISIBLE);
                editTimestampTextView.setText(dateFormat.format(message.getEditTimestamp()));
            }
        }

        /**
         * Creates a context menu for the message item view.
         *
         * @param menu The context menu to be created.
         * @param v The view for the context menu.
         * @param menuInfo The context menu info.
         */
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Message selectedMessage = messageList.get(position);

                if (selectedMessage != null) {
                    boolean isDeleted = (selectedMessage.getState() == Message.State.DELETED);

                    // Add menu items for editing and deleting messages
                    MenuItem editItem = menu.add(0, R.id.menu_edit, 0, "Edit");
                    // Disable editing for deleted messages and incoming messages
                    editItem.setEnabled(!isDeleted && !selectedMessage.isIncoming());

                    MenuItem deleteItem = menu.add(0, R.id.menu_delete, 1, "Delete");
                    deleteItem.setEnabled(!isDeleted);
                }
            }
        }

        /**
         * Unbinds the ExoPlayer and resets the state of the message item view.
         */
        public void unbind() {
            if (exoPlayer != null) {
                exoPlayer.release();
                exoPlayer = null;
            }
        }

        /**
         * Resets the state of the message item view.
         */
        public void resetState() {
            editInfoContainer.setVisibility(View.GONE);
            // Reset the text color to the standard text color
            messageTextView.setTextColor(standardTextColor);
        }
    }
}
