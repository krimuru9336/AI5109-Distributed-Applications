// Sven Schickentanz - fdai7287
package com.da.chitchat.database.user;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

/**
 * Helper class for managing the SQLite database for storing own username and UUID.
 */
public class UserDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "User.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " +
                    UserContract.UserEntry.TABLE_NAME + " (" +
                    UserContract.UserEntry.COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                    UserContract.UserEntry.COLUMN_NAME_USERNAME + " TEXT)";

    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Keep data as is
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
