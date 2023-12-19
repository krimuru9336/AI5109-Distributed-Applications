package com.da.chitchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NameRepository {
    private NameDbHelper dbHelper;

    public NameRepository(Context context) {
        dbHelper = new NameDbHelper(context);
    }

    public void saveName(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Ensure only one name is saved
        db.delete(NameContract.NameEntry.TABLE_NAME, null, null);

        ContentValues values = new ContentValues();
        values.put(NameContract.NameEntry.COLUMN_NAME, name);
        db.insertWithOnConflict(NameContract.NameEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public String readName() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NameContract.NameEntry.TABLE_NAME, new String[]{NameContract.NameEntry.COLUMN_NAME}, null, null, null, null, null);

        String name = "";

        if (cursor.moveToFirst()) {
            int cursorIndex = cursor.getColumnIndex(NameContract.NameEntry.COLUMN_NAME);
            if (cursorIndex >= 0) {
                name = cursor.getString(cursorIndex);
            }
        }

        cursor.close();
        db.close();

        return name;
    }
}
