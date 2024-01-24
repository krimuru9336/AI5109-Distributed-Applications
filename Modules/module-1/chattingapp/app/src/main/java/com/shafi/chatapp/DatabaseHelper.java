package com.shafi.chatapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "UserInput";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TEXT_VALUE = "textValue";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TEXT_VALUE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @SuppressLint("Range")
    public String getLastInsertedValue() {
        String result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC LIMIT 1", null);

        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT_VALUE));
        }

        cursor.close();
        db.close();
        return result;
    }

    @SuppressLint("Range")
    public String getLatestEntry() {
        String result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_TEXT_VALUE + " FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC LIMIT 1", null);

        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT_VALUE));
        }

        cursor.close();
        db.close();
        return result;
    }


    public List<String> retrieveData() {
        List<String> resultList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String storedValue = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT_VALUE));
                resultList.add(storedValue);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return resultList;
    }
}