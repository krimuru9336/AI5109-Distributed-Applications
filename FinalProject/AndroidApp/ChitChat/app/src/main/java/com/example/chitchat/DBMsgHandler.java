package com.example.chitchat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBMsgHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SQLite.db";
    private static final String SQL_CREATE_TABLE_MESSAGES = "CREATE TABLE IF NOT EXISTS messages (content TEXT);";
    private static final String SQL_DELETE_TABLE_MESSAGES = "DROP TABLE IF EXISTS messages;";

    public DBMsgHandler(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_MESSAGES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(SQL_DELETE_TABLE_MESSAGES);
        onCreate(db);
    }
}
