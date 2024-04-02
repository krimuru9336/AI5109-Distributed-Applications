package com.example.myapplication6;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class databaseService extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mydb";
    private static final int DATABASE_VERSION = 1;

    public databaseService(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table
        String createTableQuery = "CREATE TABLE mytable (id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT);";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle upgrades if needed
    }
}
