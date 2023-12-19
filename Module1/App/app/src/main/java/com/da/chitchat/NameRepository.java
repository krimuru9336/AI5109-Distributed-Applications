package com.da.chitchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NameRepository {
    private DbHelper dbHelper;

    public NameRepository(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void saveName(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Ensure only one name is saved
        db.delete(DbHelper.TABLE_NAME, null, null);

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_NAME, name);
        db.insertWithOnConflict(DbHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public String readName() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.TABLE_NAME, new String[]{DbHelper.COLUMN_NAME}, null, null, null, null, null);

        String name = "";

        if (cursor.moveToFirst()) {
            int cursorIndex = cursor.getColumnIndex(DbHelper.COLUMN_NAME);
            if (cursorIndex >= 0) {
                name = cursor.getString(cursorIndex);
            }
        }

        cursor.close();
        db.close();

        return name;
    }
}
