package com.example.distributedapplicationsproject.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;

import java.io.File;

public class MediaCachingService extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DAProject.db";
    private static final String TABLE_NAME = "media";
    private static final String COLUMN_ID = "_externalMediaUri";
    private static final String COLUMN_NAME = "localMediaUri";

    public MediaCachingService(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s TEXT PRIMARY KEY, %s TEXT)",
                TABLE_NAME,
                COLUMN_ID, // Primary key (string URL)
                COLUMN_NAME // Local URL value
        );

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long addMedia(String externalMediaUri, String localMediaUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, externalMediaUri); // String URL as primary key
        values.put(COLUMN_NAME, localMediaUri);   // Local URL as corresponding value

        return db.insert(TABLE_NAME, null, values);
    }

    @SuppressLint("Range")
    public String getLocalMediaUri(String externalMediaUri) {
        SQLiteDatabase db = this.getReadableDatabase();
        String localMediaUri = null;

        if (db != null) {
            Cursor cursor = db.query(
                    TABLE_NAME,
                    new String[]{COLUMN_NAME},
                    COLUMN_ID + "=?",
                    new String[]{externalMediaUri},
                    null,
                    null,
                    null
            );


            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    localMediaUri = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                }
                cursor.close();
            }
        }
        return localMediaUri;
    }

    public void removeMedia(String externalMediaUri) {
        if (externalMediaUri == null) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete img from cache
        String localMediaUri = getLocalMediaUri(externalMediaUri);
        if (localMediaUri == null) {
            return;
        }
        File file = new File(localMediaUri);
        Log.d("FILE", file.getAbsolutePath());

        // delete on filesystem
        if (file.exists()) {
            file.delete();
        }

        if (db != null) {
            db.delete(
                    TABLE_NAME,
                    COLUMN_ID + "=?",
                    new String[]{externalMediaUri}
            );
        }

    }
}
