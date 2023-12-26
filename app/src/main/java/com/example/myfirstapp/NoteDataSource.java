package com.example.myfirstapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class NoteDataSource {

    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public NoteDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertNote(String content) {
        ContentValues values = new ContentValues();
        values.put("content", content);

        return database.insert("notes", null, values);
    }

    public Cursor getAllNotes() {
        return database.query("notes", null, null, null, null, null, null);
    }
}
