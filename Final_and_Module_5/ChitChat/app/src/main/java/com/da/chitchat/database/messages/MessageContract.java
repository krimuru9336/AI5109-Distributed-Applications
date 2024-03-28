// Sven Schickentanz - fdai7287
package com.da.chitchat.database.messages;

import android.provider.BaseColumns;

/**
 * The contract class for the messages table in the database.
 * This class defines the table name and column names for the messages table.
 */
public class MessageContract {
    private MessageContract() {}

    public static class MessageEntry implements BaseColumns {
        public static final String TABLE_NAME = "messages";
        public static final String COLUMN_NAME_ID = "messageId";
        public static final String COLUMN_NAME_PARTNER = "partnerName";
        public static final String COLUMN_NAME_INCOMING = "incoming";
        public static final String COLUMN_NAME_MESSAGE = "messageText";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_TIMESTAMP_EDIT = "timestampEdit";
        public static final String COLUMN_NAME_DELETED = "deleted";
        public static final String COLUMN_NAME_CHAT_GROUP = "chatGroup";
        public static final String COLUMN_NAME_MEDIA_URI = "mediaUri";
        public static final String COLUMN_NAME_IS_VIDEO = "isVideo";
    }
}
