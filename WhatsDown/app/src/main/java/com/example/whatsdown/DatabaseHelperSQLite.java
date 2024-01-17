package com.example.whatsdown;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperSQLite extends SQLiteOpenHelper {
    /*
     * Jonas Wagner - 1315578
     */
    private static final String DATABASE_NAME = "your_database.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "your_table";
    private static final String COLUMN_NAME = "input_data";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME + " TEXT)";

    public DatabaseHelperSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades
    }

    public void insertData(String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, data);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public String retrieveData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_NAME}, null, null, null, null, null);
        String result = "";

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_NAME);
                if (columnIndex != -1) {
                    result = cursor.getString(columnIndex);
                }
            }
            cursor.close();
        }
        db.close();
        return result;
    }

    public void clearData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME); // Delete all rows from the table
        db.close();
    }

    @SuppressLint("Range")
    public String retrieveMostRecentData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_NAME}, null,
                null, null, null, "ROWID DESC", "1");

        String result = "";

        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            cursor.close();
        }
        db.close();
        return result;
    }

}