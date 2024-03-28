// Sven Schickentanz - fdai7287
package com.da.chitchat.database.messages;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.da.chitchat.Message;
import com.da.chitchat.Message.State;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Date;

/**
 * The MessageRepository class is responsible for managing the storage and retrieval of messages in the database.
 * It provides methods to add, delete, edit, and update messages, as well as retrieve all messages from the database.
 */
public class MessageRepository {
    private SQLiteDatabase database;
    private final MessageDbHelper dbHelper;

    /**
     * Constructor for the MessageRepository class.
     * 
     * @param context The context of the application.
     */
    public MessageRepository(Context context) {
        dbHelper = new MessageDbHelper(context);
    }

    /**
     * Opens the database connection.
     */
    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Adds a message to the database.
     * 
     * @param message The message to be added.
     * @param partner The name of the partner.
     */
    public void addMessage(Message message, String partner) {
        open();
        ContentValues values = new ContentValues();
        values.put(MessageContract.MessageEntry.COLUMN_NAME_ID, message.getID().toString());
        values.put(MessageContract.MessageEntry.COLUMN_NAME_PARTNER, partner);
        // Store incoming as 1 if true, 0 if false
        values.put(MessageContract.MessageEntry.COLUMN_NAME_INCOMING, message.isIncoming() ? 1 : 0);
        values.put(MessageContract.MessageEntry.COLUMN_NAME_MESSAGE, message.getText());
        values.put(MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP, message.getTimestamp().getTime());
        // Store deleted as 1 if true, 0 if false
        values.put(MessageContract.MessageEntry.COLUMN_NAME_DELETED, message.isDeleted() ? 1 : 0);
        // Store edit timestamp if it exists
        if (message.getEditTimestamp() != null) {
            values.put(MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP_EDIT, message.getEditTimestamp().getTime());
        }
        // Store chat group if it exists
        if (message.getChatGroup() != null) {
            values.put(MessageContract.MessageEntry.COLUMN_NAME_CHAT_GROUP, message.getChatGroup());
        }
        // Store media URI if it exists
        if (message.getMediaUri() != null) {
            values.put(MessageContract.MessageEntry.COLUMN_NAME_MEDIA_URI, message.getMediaUri().toString());
        }
        // Store isVideo as 1 if true, 0 if false
        values.put(MessageContract.MessageEntry.COLUMN_NAME_IS_VIDEO, message.isVideo() ? 1 : 0);

        database.insert(MessageContract.MessageEntry.TABLE_NAME, null, values);
        close();
    }

    /**
     * Deletes a message from the database.
     * 
     * @param id The ID of the message to be deleted.
     */
    public void deleteMessage(UUID id) {
        open();
        ContentValues values = new ContentValues();
        // Set message to "Message has been deleted."
        values.put(MessageContract.MessageEntry.COLUMN_NAME_MESSAGE, "Message has been deleted.");
        values.put(MessageContract.MessageEntry.COLUMN_NAME_DELETED, 1);

        String selection = MessageContract.MessageEntry.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = { id.toString() };

        database.update(
            MessageContract.MessageEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        );
        close();
    }

    /**
     * Edits a message in the database.
     * 
     * @param id The ID of the message to be edited.
     * @param newMessageText The new text of the message.
     * @param editDate The date of the edit.
     */
    public void editMessage(UUID id, String newMessageText, Date editDate) {
        open();
        ContentValues values = new ContentValues();
        // Store new message text
        values.put(MessageContract.MessageEntry.COLUMN_NAME_MESSAGE, newMessageText);
        // Store edit timestamp
        values.put(MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP_EDIT, editDate.getTime());

        String selection = MessageContract.MessageEntry.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {id.toString()};

        database.update(
            MessageContract.MessageEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        );
        close();
    }

    /**
     * Updates the timestamp of a message in the database.
     * 
     * @param id The ID of the message to be updated.
     * @param timestamp The new timestamp of the message.
     */
    public void updateTimestamp(UUID id, long timestamp) {
        open();
        ContentValues values = new ContentValues();
        values.put(MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP, timestamp);

        String selection = MessageContract.MessageEntry.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {id.toString()};

        database.update(
                MessageContract.MessageEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        close();
    }

    /**
     * Updates the media URI of a message in the database.
     * 
     * @param id The ID of the message to be updated.
     * @param uri The new URI of the media added.
     * @param isVideo A boolean indicating whether the URI is for a video.
     */
    public void updateMedia(UUID id, Uri uri, boolean isVideo) {
        open();
        ContentValues values = new ContentValues();
        values.put(MessageContract.MessageEntry.COLUMN_NAME_MEDIA_URI, uri.toString());
        values.put(MessageContract.MessageEntry.COLUMN_NAME_IS_VIDEO, isVideo ? 1 : 0);

        String selection = MessageContract.MessageEntry.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {id.toString()};

        database.update(
                MessageContract.MessageEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        close();
    }

    /**
     * Retrieves all messages from the database.
     * 
     * @return A list of all messages in the database.
     */
    public List<Message> getAllMessages() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Message> messages = new ArrayList<>();

        if (db == null) {
            return messages;
        }

        // Query the database for all messages
        Cursor cursor = db.query(
                MessageContract.MessageEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP + " ASC"
        );

        try {
            // Iterate through the cursor and add each message to the list
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Message message = cursorToMessage(cursor);
                messages.add(message);
                cursor.moveToNext();
            }
        } finally {
            db.close();
            cursor.close();
        }

        return messages;
    }

    /**
     * Converts a database cursor to a Message object.
     * 
     * @param cursor The cursor to be converted.
     */
    private Message cursorToMessage(Cursor cursor) {
        // Get the column indices for the cursor
        int idIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_ID);
        int partnerIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_PARTNER);
        int incomingIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_INCOMING);
        int messageIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_MESSAGE);
        int timestampIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP);
        int editTimestampIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP_EDIT);
        int deletedIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_DELETED);
        int chatGroupIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_CHAT_GROUP);
        int mediaUriIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_MEDIA_URI);
        int isVideoUriIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_IS_VIDEO);

        // Check if the required columns exist in the cursor
        if (idIndex == -1 || partnerIndex == -1 || incomingIndex == -1 || messageIndex == -1 ||
                timestampIndex == -1 || editTimestampIndex == -1 || deletedIndex == -1 ||
                chatGroupIndex == -1) {
            return null;
        }

        // Get the values from the cursor - convert to the appropriate types
        // Check 0 and 1 for boolean values
        UUID id = UUID.fromString(cursor.getString(idIndex));
        String partnerName = cursor.getString(partnerIndex);
        boolean isIncoming = cursor.getInt(incomingIndex) == 1;
        String text = cursor.getString(messageIndex);
        long timestamp = cursor.getLong(timestampIndex);
        long editTimestamp = cursor.getLong(editTimestampIndex);
        boolean isDeleted = cursor.getInt(deletedIndex) == 1;
        State state = isDeleted ? State.DELETED : (editTimestamp > 0 ? State.EDITED : State.UNMODIFIED);
        String chatGroup = cursor.getString(chatGroupIndex);
        String mediaUri = cursor.getString(mediaUriIndex);
        boolean isVideo = cursor.getInt(isVideoUriIndex) == 1;

        // Create a new message object with the retrieved values
        Message msg = new Message(text, partnerName, isIncoming, timestamp, id, state, editTimestamp);
        msg.setIsVideo(isVideo);
        if (chatGroup != null) msg.setChatGroup(chatGroup);
        if (mediaUri != null) {
            // Set the media URI if it exists
            msg.setMediaUri(Uri.parse(mediaUri));
        }

        return msg;
    }
}