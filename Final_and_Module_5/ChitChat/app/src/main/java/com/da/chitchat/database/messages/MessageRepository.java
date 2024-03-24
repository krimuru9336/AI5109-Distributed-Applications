package com.da.chitchat.database.messages;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.da.chitchat.Message;
import com.da.chitchat.Message.State;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Date;

public class MessageRepository {
    private static final String TAG = "MessageRepository";

    private SQLiteDatabase database;
    private final MessageDbHelper dbHelper;

    public MessageRepository(Context context) {
        dbHelper = new MessageDbHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addMessage(Message message, String partner) {
        open();
        ContentValues values = new ContentValues();
        values.put(MessageContract.MessageEntry.COLUMN_NAME_ID, message.getID().toString());
        values.put(MessageContract.MessageEntry.COLUMN_NAME_PARTNER, partner);
        values.put(MessageContract.MessageEntry.COLUMN_NAME_INCOMING, message.isIncoming() ? 1 : 0);
        values.put(MessageContract.MessageEntry.COLUMN_NAME_MESSAGE, message.getText());
        values.put(MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP, message.getTimestamp().getTime());
        values.put(MessageContract.MessageEntry.COLUMN_NAME_DELETED, message.isDeleted() ? 1 : 0);
        if (message.getEditTimestamp() != null) {
            values.put(MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP_EDIT, message.getEditTimestamp().getTime());
        }
        if (message.getChatGroup() != null) {
            values.put(MessageContract.MessageEntry.COLUMN_NAME_CHAT_GROUP, message.getChatGroup());
        }

        database.insert(MessageContract.MessageEntry.TABLE_NAME, null, values);
        close();
    }

    public void deleteMessage(UUID id) {
        open();
        ContentValues values = new ContentValues();
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

    public void editMessage(UUID id, String newMessageText, Date editDate) {
        open();
        ContentValues values = new ContentValues();
        values.put(MessageContract.MessageEntry.COLUMN_NAME_MESSAGE, newMessageText);
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

    public List<Message> getAllMessages() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Message> messages = new ArrayList<>();

        if (db == null) {
            return messages;
        }

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

    private Message cursorToMessage(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_ID);
        int partnerIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_PARTNER);
        int incomingIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_INCOMING);
        int messageIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_MESSAGE);
        int timestampIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP);
        int editTimestampIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP_EDIT);
        int deletedIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_DELETED);
        int chatGroupIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_CHAT_GROUP);

        if (idIndex == -1 || partnerIndex == -1 || incomingIndex == -1 || messageIndex == -1 ||
                timestampIndex == -1 || editTimestampIndex == -1 || deletedIndex == -1 ||
                chatGroupIndex == -1) {
            return null;
        }

        UUID id = UUID.fromString(cursor.getString(idIndex));
        String partnerName = cursor.getString(partnerIndex);
        boolean isIncoming = cursor.getInt(incomingIndex) == 1;
        String text = cursor.getString(messageIndex);
        long timestamp = cursor.getLong(timestampIndex);
        long editTimestamp = cursor.getLong(editTimestampIndex);
        boolean isDeleted = cursor.getInt(deletedIndex) == 1;
        State state = isDeleted ? State.DELETED : (editTimestamp > 0 ? State.EDITED : State.UNMODIFIED);
        String chatGroup = cursor.getString(chatGroupIndex);

        Message msg = new Message(text, partnerName, isIncoming, timestamp, id, state, editTimestamp);
        if (chatGroup != null) msg.setChatGroup(chatGroup);

        return msg;
    }
}