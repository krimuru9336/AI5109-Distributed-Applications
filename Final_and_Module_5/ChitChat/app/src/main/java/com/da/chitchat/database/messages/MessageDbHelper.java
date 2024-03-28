package com.da.chitchat.database.messages;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Messages.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MessageContract.MessageEntry.TABLE_NAME + " (" +
                    MessageContract.MessageEntry.COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                    MessageContract.MessageEntry.COLUMN_NAME_PARTNER + " TEXT," +
                    MessageContract.MessageEntry.COLUMN_NAME_INCOMING + " INTEGER," +
                    MessageContract.MessageEntry.COLUMN_NAME_MESSAGE + " TEXT," +
                    MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP + " INTEGER," +
                    MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP_EDIT + " INTEGER DEFAULT NULL," +
                    MessageContract.MessageEntry.COLUMN_NAME_DELETED + " INTEGER DEFAULT 0," +
                    MessageContract.MessageEntry.COLUMN_NAME_CHAT_GROUP + " TEXT DEFAULT NULL," +
                    MessageContract.MessageEntry.COLUMN_NAME_IMAGE_URI + " TEXT DEFAULT NULL)";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MessageContract.MessageEntry.TABLE_NAME;

    public MessageDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
