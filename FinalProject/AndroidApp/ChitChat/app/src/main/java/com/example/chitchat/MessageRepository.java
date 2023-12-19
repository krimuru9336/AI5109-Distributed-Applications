package com.example.chitchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MessageRepository {
    private final DBMsgHandler dbMsgHandler;
    private final SQLiteDatabase dbWrite;
    private final SQLiteDatabase dbRead;
    public MessageRepository(Context context){
        this.dbMsgHandler = new DBMsgHandler(context);
        this.dbWrite = dbMsgHandler.getWritableDatabase();
        this.dbRead = dbMsgHandler.getReadableDatabase();
    }
    public void saveMessage(String msg){
        ContentValues values = new ContentValues();
        values.put("content",msg);
        dbWrite.insertWithOnConflict("messages",null,values,SQLiteDatabase.CONFLICT_REPLACE);
    }
    public String readMessage(){
        Cursor cursor = dbRead.query("messages",new String[]{"content"},null,null,null,null,null);
        String msg = "";
        if(cursor.moveToLast()){
            int cursorIdx = cursor.getColumnIndex("content");
            if (cursorIdx >= 0){
                msg = cursor.getString(cursorIdx);
            }
        }
        cursor.close();
        return msg;
    }
}
